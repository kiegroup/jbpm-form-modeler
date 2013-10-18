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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Map;
import java.util.TreeMap;

@ApplicationScoped
public class RangeProviderManagerImpl implements RangeProviderManager {

    @Inject
    protected Instance<RangeProvider> defaultRangeProviders;

    public RangeProvider getRangeProviderByType(String providerCode) {
        for (RangeProvider provider : defaultRangeProviders) {
            if(provider.getType().equals(providerCode)) return provider;
        }

        return null;
    }

    @Override
    public Map<String,String> getRangeValues(String type, String namespace) {
        RangeProvider rangeProvider = getRangeProviderByType(type);
        if (rangeProvider != null) {
            return rangeProvider.getRangesMap(namespace);
        }
        return generateMapFromExpresion(type);
    }

    private Map<String,String> generateMapFromExpresion(String rangeFormula) {
        CSVParser rangeParser = new CSVParser(';');
        CSVParser optionParser = new CSVParser(',');

        try {
            if (!StringUtils.isEmpty(rangeFormula) && rangeFormula.startsWith("{") && rangeFormula.endsWith("}")) {
                rangeFormula = rangeFormula.substring(1, rangeFormula.length() - 1);

                String[] options = rangeParser.parseLine(rangeFormula);
                if (options != null) {
                    Map<String,String> rangeValues = new TreeMap<String,String>();
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
