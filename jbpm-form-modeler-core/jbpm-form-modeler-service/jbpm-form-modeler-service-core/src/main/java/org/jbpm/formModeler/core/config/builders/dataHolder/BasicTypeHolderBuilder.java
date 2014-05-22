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
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.model.BasicTypeDataHolder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class BasicTypeHolderBuilder implements RangedDataHolderBuilder {
    public static final String HOLDER_TYPE_BASIC_TYPE = "basicType";

    @Inject
    private FieldTypeManager fieldTypeManager;

    @Override
    public String getId() {
        return HOLDER_TYPE_BASIC_TYPE;
    }

    @Override
    public DataHolder buildDataHolder(DataHolderBuildConfig config) {
        String fieldClass = config.getValue();
        if (fieldTypeManager.getSimpleTypeByClass(fieldClass) == null) return null;

        return new BasicTypeDataHolder(config.getHolderId(), config.getInputId(), config.getOutputId(), fieldClass, config.getRenderColor());
    }

    @Override
    public boolean supportsPropertyType(String typeClass, String path) {
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

    @Override
    public Map<String, String> getHolderSources(String context) {
        Map<String, String> result = new TreeMap<String, String>();
        try {
            List<FieldType> allFieldTypes = fieldTypeManager.getFieldTypes();
            for (FieldType fieldType: allFieldTypes){
                if(fieldTypeManager.isbaseType(fieldType.getCode())) {
                    result.put(fieldType.getFieldClass(), fieldType.getFieldClass());
                }
            }

        } catch (Throwable e) {
            result.put("-", "-");
        }
        return result;
    }

    @Override
    public String[] getSupportedHolderTypes() {
        return new String[0];
    }

    @Override
    public String getDataHolderName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.config.builders.dataHolder.messages", locale);
        return bundle.getString("dataHolder_basicType");
    }
}
