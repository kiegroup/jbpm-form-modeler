package org.jbpm.formModeler.api.config;

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.processing.PropertyDefinition;

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

    List getSuitableFieldTypes(String propertyName, PropertyDefinition propDefinition) throws Exception;

    List getSuitableFieldTypes(String managerClass) throws Exception;

    List getFormDecoratorTypes() throws Exception;

    FieldType getTypeByCode(String typeCode) throws Exception;

    FieldType getTypeByClass(String classType) throws Exception;

    FieldType getTypeByCode(String typeCode, boolean tryToCreateTypes) throws Exception;

    String getIconPathForCode(String code);
}
