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

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.config.builders.fieldType.FieldTypeBuilder;
import org.jbpm.formModeler.core.config.builders.fieldType.DecoratorFieldTypeBuilder;
import org.jbpm.formModeler.core.config.builders.fieldType.SimpleFieldTypeBuilder;
import org.jbpm.formModeler.core.config.builders.fieldType.ComplexFieldTypeBuilder;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

/**
 *
 */
@ApplicationScoped
public class FieldTypeManagerImpl implements FieldTypeManager {

    private List<FieldType> fieldTypes;
    private List<FieldType> decoratorTypes;
    private List<FieldType> complexTypes;

    private Logger log = LoggerFactory.getLogger(FieldTypeManager.class);

    @Inject
    FieldTypeLabelBuilder fieldTypeLabelBuilder;

    @Inject
    protected Instance<SimpleFieldTypeBuilder> builders;
    @Inject
    protected Instance<DecoratorFieldTypeBuilder> decoratorBuilders;
    @Inject
    protected Instance<ComplexFieldTypeBuilder> complexBuilders;

    private Map<String, String> iconsMappings = new HashMap<String, String>();
    private String defaultIcon = "fieldTypes/default.png";

    private ArrayList<String> hiddenFieldTypesCodes= new ArrayList<String>();

    private ArrayList<String> baseTypes= new ArrayList<String>();

    protected FieldType customType = new FieldType();

    @PostConstruct
    protected void init() {

        fieldTypes = new ArrayList<FieldType>();
        decoratorTypes = new ArrayList<FieldType>();
        complexTypes = new ArrayList<FieldType>();

        for (FieldTypeBuilder builder : builders) {
            fieldTypes.addAll(builder.buildList());
        }

        for (FieldTypeBuilder builder : decoratorBuilders) {
            decoratorTypes.addAll(builder.buildList());
        }

        for (FieldTypeBuilder builder : complexBuilders) {
            complexTypes.addAll(builder.buildList());
        }

        customType.setCode("CustomInput");
        customType.setFieldClass("*");
        customType.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.customInput.CustomInputFieldHandler");

        iconsMappings.put("InputTextShort", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextInteger", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextIBAN", "fieldTypes/box_number.png");
        iconsMappings.put("Separator", "fieldTypes/splitter_box.png");
        iconsMappings.put("InputTextLong", "fieldTypes/box_number.png");
        iconsMappings.put("InputDate", "fieldTypes/date_selector.png");
        iconsMappings.put("I18nHTMLText", "fieldTypes/rich_text_box.png");
        iconsMappings.put("HTMLEditor", "fieldTypes/rich_text_box.png");
        iconsMappings.put("InputTextArea", "fieldTypes/scroll_zone.png");
        iconsMappings.put("I18nTextArea", "fieldTypes/scroll_zone.png");
        iconsMappings.put("CheckBox", "fieldTypes/checkbox.png");
        iconsMappings.put("CheckBoxPrimitiveBoolean", "fieldTypes/checkbox.png");
        iconsMappings.put("InputShortDate", "fieldTypes/date_selector.png");
        iconsMappings.put("I18nText", "fieldTypes/textbox.png");
        iconsMappings.put("InputTextFloat", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextBigDecimal", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextBigInteger", "fieldTypes/box_number.png");
        iconsMappings.put("InputTextDouble", "fieldTypes/box_number.png");
        iconsMappings.put("HTMLLabel", "fieldTypes/rich_text_box.png");
        iconsMappings.put("InputText", "fieldTypes/textbox.png");
        iconsMappings.put("InputTextEmail", "fieldTypes/mailbox.png");
        iconsMappings.put("Subform", "fieldTypes/master_details.png");
        iconsMappings.put("MultipleSubform", "fieldTypes/master_details.png");
        iconsMappings.put("Document", "fieldTypes/file.png");
        iconsMappings.put("File", "fieldTypes/file.png");

        hiddenFieldTypesCodes.add("InputTextPrimitiveByte");
        hiddenFieldTypesCodes.add("InputTextPrimitiveShort");
        hiddenFieldTypesCodes.add("InputTextPrimitiveInteger");
        hiddenFieldTypesCodes.add("InputTextPrimitiveLong");
        hiddenFieldTypesCodes.add("InputTextPrimitiveFloat");
        hiddenFieldTypesCodes.add("InputTextPrimitiveDouble");
        hiddenFieldTypesCodes.add("CheckBoxPrimitiveBoolean");
        hiddenFieldTypesCodes.add("InputTextPrimitiveCharacter");
        hiddenFieldTypesCodes.add("I18nHTMLText");
        hiddenFieldTypesCodes.add("I18nText");
        hiddenFieldTypesCodes.add("I18nTextArea");
        hiddenFieldTypesCodes.add("CustomInput");

        baseTypes.add("InputText");
        baseTypes.add("InputDate");
        baseTypes.add("CheckBox");
        baseTypes.add("InputTextCharacter");
        baseTypes.add("InputTextByte");
        baseTypes.add("InputTextShort");
        baseTypes.add("InputTextInteger");
        baseTypes.add("InputTextLong");
        baseTypes.add("InputTextFloat");
        baseTypes.add("InputTextDouble");
        baseTypes.add("InputTextBigDecimal");
        baseTypes.add("InputTextBigInteger");

    }

    @Override
    public String getDefaultIcon() {
        return defaultIcon;
    }

    @Override
    public void setDefaultIcon(String defaultIcon) {
        this.defaultIcon = defaultIcon;
    }

    @Override
    public Map<String, String> getIconsMappings() {
        return iconsMappings;
    }

    @Override
    public void setIconsMappings(Map<String, String> iconsMappings) {
        this.iconsMappings = iconsMappings;
    }

    @Override
    public List<FieldType> getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public void setFieldTypes(List<FieldType> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    /**
     * Get all fieldtypes suitable to generate a value of the given class.
     *
     * @param propertyType Expected property definition that the field type should generate.
     * @return A list of FieldType objects suitable to generate a value of the given class.
     * @throws Exception in case of error
     */
    @Override
    public List<FieldType> getSuitableFieldTypes(String propertyType) {
        final List<FieldType> validFieldTypes = new ArrayList<FieldType>();
        if (!StringUtils.isEmpty(propertyType)) {
            boolean isDecorator = false;

            for (FieldType fieldType : fieldTypes) {
                if (fieldType.getFieldClass().equals(propertyType))
                    validFieldTypes.add(fieldType);
            }

            for (FieldType fieldType : complexTypes) {
                if (fieldType.getFieldClass().equals(propertyType))
                    validFieldTypes.add(fieldType);
            }

            for (FieldType fieldType : decoratorTypes) {
                if (fieldType.getFieldClass().equals(propertyType)) {
                    validFieldTypes.add(fieldType);
                    isDecorator = true;
                }
            }

            if (!isDecorator) {
                FieldType custom = new FieldType(customType);
                custom.setFieldClass(propertyType);
                validFieldTypes.add(custom);
            }
        }

        return validFieldTypes;
    }

    @Override
    public List<FieldType> getFormDecoratorTypes() {
        return decoratorTypes;
    }

    @Override
    public List<FieldType> getFormComplexTypes() {
        return complexTypes;
    }

    @Override
    public FieldType getTypeByCode(String typeCode, String fieldClass) {
        if (customType.getCode().equals(typeCode)) {
            FieldType ft = new FieldType(customType);
            ft.setFieldClass(fieldClass);
            return ft;
        }

        for (FieldType fieldType : fieldTypes) {
            if (fieldType.getCode().equals(typeCode)) return fieldType;
        }

        for (FieldType decorator : decoratorTypes) {
            if (decorator.getCode().equals(typeCode)) return decorator;
        }

        for (FieldType complexType : complexTypes) {
            if (complexType.getCode().equals(typeCode)) return complexType;
        }
        return null;
    }

    @Override
    public FieldType getTypeByCode(String typeCode) {
        return getTypeByCode(typeCode, null);
    }

    @Override
    public FieldType getTypeByClass(String className) {
        if (StringUtils.isEmpty(className)) return null;

        FieldType fieldType = getSimpleTypeByClass(className);

        if (fieldType != null) return fieldType;

        fieldType = getDecoratorTypeByClass(className);

        if (fieldType != null) return fieldType;

        fieldType = getComplexTypeByClass(className);

        if (fieldType != null) return fieldType;

        return getTypeByCode("Subform");
    }

    @Override
    public FieldType getSimpleTypeByClass(String className) {
        return getFieldTypeByClass(fieldTypes, className);
    }

    @Override
    public FieldType getComplexTypeByClass(String className) {
        return getFieldTypeByClass(complexTypes, className);
    }

    @Override
    public FieldType getDecoratorTypeByClass(String className) {
        return getFieldTypeByClass(decoratorTypes, className);
    }

    protected FieldType getFieldTypeByClass(List<FieldType> fieldTypes, String className) {
        for (FieldType fieldType : fieldTypes) {
            if (fieldType.getFieldClass().equals(className)) return fieldType;
        }
        return null;
    }

    @Override
    public String getIconPathForCode(String code) {
        if (code == null) {
            log.warn("Retrieving icon for field type with code null. All types must have a code.");
            return defaultIcon;
        }
        String s = getIconsMappings().get(code);
        if (s == null) {
            return defaultIcon;
        }
        return s;
    }

    public static FieldTypeManagerImpl lookup() {
        return (FieldTypeManagerImpl) CDIBeanLocator.getBeanByType(FieldTypeManagerImpl.class);
    }

    public boolean isDisplayableType(String typeCode){
        if (typeCode==null) return false;
        return !hiddenFieldTypesCodes.contains(typeCode);
    }

    @Override
    public boolean isbaseType(String code) {
        if (code==null) return false;
        for(String codeId: baseTypes){
            if(code.equals(codeId)) return true;
        }
        return false;
    }

    @Override
    public String getFieldTypeLabel(FieldType fieldType) {
        return fieldTypeLabelBuilder.getFieldTypeLabel(fieldType);
    }
}
