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

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.processing.PropertyDefinition;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface FieldTypeManager extends Serializable {
    String getDefaultIcon();

    void setDefaultIcon(String defaultIcon);

    Map<String, String> getIconsMappings();

    void setIconsMappings(Map<String, String> iconsMappings);

    List<FieldType> getFieldTypes();

    void setFieldTypes(List<FieldType> fieldTypes);

    List getSuitableFieldTypes(String propertyType);

    List<FieldType> getFormDecoratorTypes();

    List<FieldType> getFormComplexTypes();

    FieldType getTypeByCode(String typeCode);

    FieldType getTypeByClass(String classType);

    FieldType getSimpleTypeByClass(String className);

    FieldType getComplexTypeByClass(String className);

    FieldType getDecoratorTypeByClass(String className);

    String getIconPathForCode(String code);

    boolean isDisplayableType(String code);

    boolean isbaseType(String code);

    FieldType getTypeByCode(String typeCode, String fieldClass);

    String getFieldTypeLabel(FieldType fieldType);
}
