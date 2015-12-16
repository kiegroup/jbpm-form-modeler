/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.formModeler.core.processing.fieldHandlers.multiple;

import org.apache.commons.collections.CollectionUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("org.jbpm.formModeler.core.processing.fieldHandlers.multiple.MultipleInputFieldHandler")
public class MultipleInputFieldHandler extends DefaultFieldHandler {
    private Logger log = LoggerFactory.getLogger(MultipleInputFieldHandler.class);

    @Override
    public String[] getCompatibleClassNames() {
        return new String[]{List.class.getName()};
    }

    @Override
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        List value = (List) previousValue;

        if (value == null) value = new ArrayList();

        FieldHandler handler = getFieldHandler(field);

        String[] newValue = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "addItem");
        String[] deleteValue = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "deleteItem");

        if (newValue != null && newValue.length == 1 && newValue[0].equals("true")) {
            try {
                Object valueToAdd = handler.getValue(field, inputName, parametersMap, filesMap, field.getBag(), null);
                value.add(valueToAdd);
            } catch (Exception ex) {
                log.debug("Unable to add value to list '{}': {}", field.getFieldName(), ex);
                throw new Exception("Unable to add value");
            }
        } else if (deleteValue != null && deleteValue.length == 1 && !deleteValue[0].equals("-1")) {
            int index = Integer.decode(deleteValue[0]);
            if (index <= value.size()) value.remove(index);
        } else {
            if (!value.isEmpty()) {
                for (int i = 0; i < value.size(); i++) {
                    try {
                        value.set(i, handler.getValue(field, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i, parametersMap, filesMap, field.getBag(), value.get(i)));
                    } catch (Exception ex) {
                        log.debug("Unable to edit value '{}' on list '{}': {}", i, field.getFieldName(), ex);
                        throw new Exception("Unable to edit value");
                    }
                }
            }
        }
        return value;
    }

    @Override
    public Map getParamValue(Field field, String inputName, Object objectValue) {
        Map<String, Object> result = new HashMap<String, Object>();

        List values = (List) objectValue;

        if (CollectionUtils.isEmpty(values)) return result;

        FieldHandler handler = getFieldHandler(field);

        if (values != null && values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                result.putAll(handler.getParamValue(field, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i, values.get(i)));
            }
        }

        return result;
    }

    @Override
    public boolean isEmpty(Object value) {
        return CollectionUtils.isEmpty((List) value);
    }

    protected FieldHandler getFieldHandler(Field field) {
        FieldType bagFieldType = getFieldTypeManager().getTypeByClass(field.getBag());
        if (bagFieldType != null) {
            return getFieldHandlersManager().getHandler(bagFieldType);
        }
        return null;
    }
}
