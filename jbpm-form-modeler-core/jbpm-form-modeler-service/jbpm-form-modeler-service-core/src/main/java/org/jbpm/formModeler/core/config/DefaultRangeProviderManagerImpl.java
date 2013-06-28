/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.config;


import au.com.bytecode.opencsv.CSVParser;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.RangeProvider;
import org.jbpm.formModeler.core.config.builders.RangeProviderBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Map;
import java.util.TreeMap;

@ApplicationScoped
public class DefaultRangeProviderManagerImpl implements DefaultRangeProviderManager {

    @Inject
    protected Instance<RangeProviderBuilder> rangeProviderBuilders;

    @Override
    public RangeProviderBuilder getBuilderByType(String builderId) {
        for (RangeProviderBuilder builder : rangeProviderBuilders) {
            if (builder.getId().equals(builderId)) return builder;
        }
        return null;
    }

    @Override
    public Map getRangeValues(String providerCode) {
        RangeProviderBuilder rangeProviderBuilder = getBuilderByType(providerCode);
        if (rangeProviderBuilder != null) {
            RangeProvider rangeProvider = rangeProviderBuilder.buildRangeProvider(null);
            return rangeProvider.getValuesMap(null);
        }
        return generateMapFromExpresion(providerCode);  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Map generateMapFromExpresion(String rangeFormula) {
        CSVParser rangeParser = new CSVParser(';');
        CSVParser optionParser = new CSVParser(',');

        try {
            if (!StringUtils.isEmpty(rangeFormula) && rangeFormula.startsWith("{") && rangeFormula.endsWith("}")) {
                rangeFormula = rangeFormula.substring(1, rangeFormula.length() - 1);

                String[] options = rangeParser.parseLine(rangeFormula);
                if (options != null) {
                    Map rangeValues = new TreeMap();
                    for (String option : options) {
                        String[] values = optionParser.parseLine(option);
                        if (values != null && values.length == 2) {
                            rangeValues.put(values[0], values[1]);
                        }
                    }
                    return rangeValues;
                }
            }

        } catch (Exception e) {

        }
        return null;
    }


}
