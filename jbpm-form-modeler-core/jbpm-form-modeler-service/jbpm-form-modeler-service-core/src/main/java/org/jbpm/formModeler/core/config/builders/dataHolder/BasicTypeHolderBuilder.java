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
package org.jbpm.formModeler.core.config.builders.dataHolder;

import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.core.model.BasicTypeDataHolder;
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class BasicTypeHolderBuilder implements DataHolderBuilder {
    @Inject
    private FieldTypeManager fieldTypeManager;

    @Override
    public String getId() {
        return Form.HOLDER_TYPE_CODE_BASIC_TYPE;
    }

    @Override
    public DataHolder buildDataHolder(Map<String, Object> config) {
        return new BasicTypeDataHolder((String)config.get("id"), (String) config.get("outId"), (String)config.get("value"), (String)config.get("color"));
    }

    @Override
    public Map getOptions(Object path) {
        Map result = new HashMap();
        try {
            FieldTypeManager fieldTypeManager = (FieldTypeManager) CDIBeanLocator.getBeanByType(FieldTypeManager.class);
            List<FieldType> allFieldTypes = fieldTypeManager.getFieldTypes();
            for (FieldType fieldType: allFieldTypes){
                if(fieldTypeManager.isbaseType(fieldType.getCode())) {
                    result.put(fieldType.getFieldClass(), fieldType.getCode());
                }
            }

        } catch (Throwable e) {
            result.put("-", "-");
        }
        return result;
    }

    @Override
    public boolean supportsPropertyType(String typeClass, Object path) {
        List<FieldType> types = fieldTypeManager.getFieldTypes();

        for (FieldType type : types) {
            String className = type.getFieldClass();
            if (className.equals(typeClass)) return true;
            if (typeClass.indexOf(".") == -1 && className.endsWith("." + typeClass)) return true;
        }

        return false;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
