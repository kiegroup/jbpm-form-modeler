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
package org.jbpm.formModeler.core.processing.fieldHandlers.customInput;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.fieldTypes.CustomFieldType;
import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.inject.Inject;
import java.util.Map;

public class CustomInputFieldHandler extends DefaultFieldHandler {

    @Inject
    private NamespaceManager namespaceManager;

    @Override
    public String[] getCompatibleClassNames() {
        return new String[]{"*"};
    }

    @Override
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        if (StringUtils.isEmpty(field.getCustomFieldType())) return previousValue;

        CustomFieldType customFieldType = (CustomFieldType) CDIBeanLocator.getBeanByType(field.getCustomFieldType());

        if (customFieldType == null) return previousValue;

        return customFieldType.getValue(parametersMap, filesMap, field.getFieldName(), namespaceManager.getNamespace(inputName).getNamespace(), previousValue, field.getFieldRequired(), field.getReadonly(), field.getParam1(), field.getParam2(), field.getParam3(), field.getParam4(), field.getParam5());
    }

    @Override
    public Map getParamValue(String inputName, Object objectValue, String pattern) {
        return null;
    }

    @Override
    public boolean isEmpty(Object value) {
        return value == null;
    }
}
