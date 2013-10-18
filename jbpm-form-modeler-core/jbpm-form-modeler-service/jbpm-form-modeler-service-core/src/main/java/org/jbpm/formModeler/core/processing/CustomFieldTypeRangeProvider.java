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
package org.jbpm.formModeler.core.processing;

import org.jbpm.formModeler.api.model.RangeProvider;
import org.jbpm.formModeler.core.fieldTypes.CustomFieldType;
import org.jbpm.formModeler.service.LocaleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Map;
import java.util.TreeMap;

@ApplicationScoped
public class CustomFieldTypeRangeProvider implements RangeProvider {
    private Logger log = LoggerFactory.getLogger(CustomFieldTypeRangeProvider.class);

    @Inject
    private LocaleManager localeManager;

    @Inject
    private Instance<CustomFieldType> customFieldTypes;

    @Override
    public String getType() {
        return "{$custom_field_type_range_provider}";
    }

    @Override
    public Map<String,String> getRangesMap(String namespace) {
        TreeMap<String,String> result = new TreeMap<String,String>();

        if (customFieldTypes != null) {
            for (CustomFieldType type : customFieldTypes) {
                result.put(type.getClass().getName(), type.getDescription(localeManager.getCurrentLocale()));
            }
        }

        return result;
    }
}
