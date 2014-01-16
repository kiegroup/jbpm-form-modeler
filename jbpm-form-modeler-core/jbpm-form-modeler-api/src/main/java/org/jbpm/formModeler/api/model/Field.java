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
package org.jbpm.formModeler.api.model;

import org.jbpm.formModeler.api.model.wrappers.I18nSet;

import java.io.Serializable;
import java.util.*;

/**
 * Definition of a Form Field.
 */
public class Field implements Serializable, Comparable<Field> {
    private Long id;

    private I18nSet title;

    private I18nSet label;

    private I18nSet errorMessage;

    private String fieldName;

    private Boolean fieldRequired;

    private Boolean readonly;

    private String size;

    private String formula;

    private Boolean groupWithPrevious;

    private String pattern;

    private Long maxlength;

    private String styleclass;

    private String cssStyle;

    private String height;

    private Long tabindex;

    private String accesskey;

    private String rangeFormula;

    private String labelCSSStyle;
    private String labelCSSClass;

    private Boolean isHTML;
    private Boolean hideContent;
    private String defaultValueFormula;

    private I18nSet htmlContent;

    private FieldType fieldType;

    private String inputBinding;
    private String outputBinding;

    private int position;

    //Subform data
    private String defaultSubform;
    private String previewSubform;
    private String tableSubform;

    private I18nSet newItemText;
    private I18nSet addItemText;
    private I18nSet cancelItemText;

    private Boolean deleteItems;
    private Boolean updateItems;
    private Boolean visualizeItem;
    private Boolean hideCreateItem;
    private Boolean expanded;

    private Boolean enableTableEnterData;

    //Custom types
    private String customFieldType;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;


    private Form form;

    public Field() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFieldRequired() {
        return fieldRequired;
    }

    public void setFieldRequired(Boolean fieldRequired) {
        this.fieldRequired = fieldRequired;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getGroupWithPrevious() {
        return groupWithPrevious;
    }

    public void setGroupWithPrevious(Boolean groupWithPrevious) {
        this.groupWithPrevious = groupWithPrevious;
    }

    public Long getMaxlength() {
        return this.maxlength;
    }

    public void setMaxlength(Long maxlength) {
        this.maxlength = maxlength;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public String getStyleclass() {
        return this.styleclass;
    }

    public void setStyleclass(String styleclass) {
        this.styleclass = styleclass;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Long getTabindex() {
        return this.tabindex;
    }

    public void setTabindex(Long tabindex) {
        this.tabindex = tabindex;
    }

    public String getAccesskey() {
        return this.accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public FieldType getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getRangeFormula() {
        return rangeFormula;
    }

    public void setRangeFormula(String rangeFormula) {
        this.rangeFormula = rangeFormula;
    }

    public Boolean getIsHTML() {
        return isHTML;
    }

    public void setIsHTML(Boolean HTML) {
        isHTML = HTML;
    }

    public Boolean getHideContent() {
        return hideContent;
    }

    public void setHideContent(Boolean hideContent) {
        this.hideContent = hideContent;
    }

    public String getDefaultValueFormula() {
        return defaultValueFormula;
    }

    public void setDefaultValueFormula(String defaultValueFormula) {
        this.defaultValueFormula = defaultValueFormula;
    }

    //Subform data
    public String getDefaultSubform() {
        return defaultSubform;
    }

    public void setDefaultSubform(String defaultSubform) {
        this.defaultSubform = defaultSubform;
    }

    public String getPreviewSubform() {
        return previewSubform;
    }

    public void setPreviewSubform(String previewSubform) {
        this.previewSubform = previewSubform;
    }

    public String getTableSubform() {
        return tableSubform;
    }

    public void setTableSubform(String tableSubform) {
        this.tableSubform = tableSubform;
    }

    public I18nSet getNewItemText() {
        return newItemText;
    }

    public void setNewItemText(I18nSet newItemText) {
        this.newItemText = newItemText;
    }

    public I18nSet getAddItemText() {
        return addItemText;
    }

    public void setAddItemText(I18nSet addItemText) {
        this.addItemText = addItemText;
    }

    public I18nSet getCancelItemText() {
        return cancelItemText;
    }

    public void setCancelItemText(I18nSet cancelItemText) {
        this.cancelItemText = cancelItemText;
    }

    public Boolean getDeleteItems() {
        return deleteItems;
    }

    public void setDeleteItems(Boolean deleteItems) {
        this.deleteItems = deleteItems;
    }

    public Boolean getUpdateItems() {
        return updateItems;
    }

    public void setUpdateItems(Boolean updateItems) {
        this.updateItems = updateItems;
    }

    public Boolean getVisualizeItem() {
        return visualizeItem;
    }

    public void setVisualizeItem(Boolean visualizeItem) {
        this.visualizeItem = visualizeItem;
    }

    public Boolean getHideCreateItem() {
        return hideCreateItem;
    }

    public void setHideCreateItem(Boolean hideCreateItem) {
        this.hideCreateItem = hideCreateItem;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Boolean getEnableTableEnterData() {
        return enableTableEnterData;
    }

    public void setEnableTableEnterData(Boolean enableTableEnterData) {
        this.enableTableEnterData = enableTableEnterData;
    }

    public String getCustomFieldType() {
        return customFieldType;
    }

    public void setCustomFieldType(String customFieldType) {
        this.customFieldType = customFieldType;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    public String getParam5() {
        return param5;
    }

    public void setParam5(String param5) {
        this.param5 = param5;
    }

    public String toString() {
        return getId().toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Field)) return false;
        Field castOther = (Field) other;
        return this.getId().equals(castOther.getId());
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public I18nSet getTitle() {
        return title;
    }

    public void setTitle(I18nSet title) {
        this.title = title;
    }

    public I18nSet getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(I18nSet errorMessage) {
        this.errorMessage = errorMessage;
    }

    public I18nSet getLabel() {
        return label;
    }

    public void setLabel(I18nSet label) {
        this.label = label;
    }

    public String getFieldPattern() {
        if ((getPattern() == null || "".equals(getPattern())) && getFieldType() != null)
            return getFieldType().getPattern();
        else
            return getPattern();
    }

    public String getFieldFormula() {
        if ((getFormula() == null || "".equals(getFormula())) && getFieldType() != null)
            return getFieldType().getFormula();
        else
            return getFormula();
    }

    public String getFieldRangeFormula() {
        if ((getRangeFormula() == null || "".equals(getRangeFormula())) && getFieldType() != null)
            return getFieldType().getRangeFormula();
        else
            return getRangeFormula();
    }

    public void putAll(Field field) {
        this.setAccesskey(field.getAccesskey());
        this.setCssStyle(field.getCssStyle());
        this.setTitle(field.getTitle());
        this.setLabel(field.getLabel());
        this.setErrorMessage(field.getErrorMessage());
        this.setFieldName(field.getFieldName());
        this.setFieldType(field.getFieldType());
        this.setFieldRequired(field.getFieldRequired());
        this.setFormula(field.getFormula());
        this.setGroupWithPrevious(field.getGroupWithPrevious());
        this.setHeight(field.getHeight());
        this.setLabelCSSClass(field.getLabelCSSClass());
        this.setLabelCSSStyle(field.getLabelCSSStyle());
        this.setReadonly(field.getReadonly());
        this.setSize(field.getSize());
        this.setRangeFormula(field.getRangeFormula());
        this.setPattern(field.getPattern());
        this.setMaxlength(field.getMaxlength());
        this.setStyleclass(field.getStyleclass());
        this.setTabindex(field.getTabindex());
        this.setIsHTML(field.getIsHTML());
        this.setHideContent(field.getHideContent());
        this.setDefaultValueFormula(field.getDefaultValueFormula());

        //subforms
        this.setDefaultSubform(field.getDefaultSubform());
        this.setPreviewSubform(field.getPreviewSubform());
        this.setTableSubform(field.getTableSubform());
        this.setNewItemText(field.getNewItemText());
        this.setAddItemText(field.getAddItemText());
        this.setCancelItemText(field.getCancelItemText());

        this.setDeleteItems(field.getDeleteItems());
        this.setUpdateItems(field.getUpdateItems());
        this.setVisualizeItem(field.getVisualizeItem());
        this.setHideCreateItem(field.getHideCreateItem());
        this.setExpanded(field.getExpanded());
        this.setEnableTableEnterData(field.getEnableTableEnterData());

        this.setCustomFieldType(field.getCustomFieldType());
        this.setParam1(field.getParam1());
        this.setParam2(field.getParam2());
        this.setParam3(field.getParam3());
        this.setParam4(field.getParam4());
        this.setParam5(field.getParam5());
    }

    public String getLabelCSSStyle() {
        return labelCSSStyle;
    }

    public void setLabelCSSStyle(String labelCSSStyle) {
        this.labelCSSStyle = labelCSSStyle;
    }

    public String getLabelCSSClass() {
        return labelCSSClass;
    }

    public void setLabelCSSClass(String labelCSSClass) {
        this.labelCSSClass = labelCSSClass;
    }

    public I18nSet getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(I18nSet htmlContent) {
        this.htmlContent = htmlContent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getInputBinding() {
        return inputBinding;
    }

    public void setInputBinding(String inputBinding) {
        this.inputBinding = inputBinding;
    }

    public String getOutputBinding() {
        return outputBinding;
    }

    public void setOutputBinding(String outputBinding) {
        this.outputBinding = outputBinding;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Set getPropertyNames() {
        Set names = new TreeSet();

        names.add("fieldName");
        names.add("fieldRequired");
        names.add("groupWithPrevious");
        names.add("height");
        names.add("labelCSSClass");
        names.add("labelCSSStyle");
        names.add("label");
        names.add("title");
        names.add("errorMessage");
        names.add("disabled");
        names.add("readonly");
        names.add("size");
        names.add("formula");
        names.add("rangeFormula");
        names.add("pattern");
        names.add("maxlength");
        names.add("styleclass");
        names.add("cssStyle");
        names.add("tabindex");
        names.add("accesskey");
        names.add("isHTML");
        names.add("hideContent");
        names.add("defaultValueFormula");
        names.add("htmlContent");

        names.add("inputBinding");
        names.add("outputBinding");

        names.add("subformClass");
        names.add("defaultSubform");
        names.add("creationSubform");
        names.add("editionSubform");
        names.add("previewSubform");
        names.add("tableSubform");

        names.add("newItemText");
        names.add("addItemText");
        names.add("cancelItemText");

        names.add("deleteItems");
        names.add("updateItems");
        names.add("visualizeItem");
        names.add("hideCreateItem");
        names.add("expanded");

        names.add("separator");

        names.add("enableTableEnterData");

        names.add("customFieldType");
        names.add("param1");
        names.add("param2");
        names.add("param3");
        names.add("param4");
        names.add("param5");

        return names;
    }

    public Map asMap() {
        Map value = new HashMap();

        value.put("fieldName", getFieldName());
        value.put("fieldRequired", getFieldRequired());
        value.put("groupWithPrevious", getGroupWithPrevious());
        value.put("height", getHeight());
        value.put("labelCSSClass", getLabelCSSClass());
        value.put("labelCSSStyle", getLabelCSSStyle());
        value.put("label", getLabel());
        value.put("errorMessage", getErrorMessage());
        value.put("title", getTitle());
        value.put("readonly", getReadonly());
        value.put("size", getSize());
        value.put("formula", getFormula());
        value.put("rangeFormula", getRangeFormula());
        value.put("pattern", getPattern());
        value.put("maxlength", getMaxlength());
        value.put("styleclass", getStyleclass());
        value.put("cssStyle", getCssStyle());
        value.put("tabindex", getTabindex());
        value.put("accesskey", getAccesskey());
        value.put("isHTML", getIsHTML());
        value.put("hideContent", getHideContent());
        value.put("defaultValueFormula", getDefaultValueFormula());
        value.put("htmlContent", getHtmlContent());

        value.put("inputBinding", getInputBinding());
        value.put("outputBinding", getOutputBinding());

        //SubForms
        value.put("defaultSubform",getDefaultSubform());
        value.put("previewSubform",getPreviewSubform());
        value.put("tableSubform",getTableSubform());

        value.put("newItemText",getNewItemText());
        value.put("addItemText",getAddItemText());
        value.put("cancelItemText",getCancelItemText());

        value.put("deleteItems",getDeleteItems());
        value.put("updateItems",getUpdateItems());
        value.put("visualizeItem",getVisualizeItem());
        value.put("hideCreateItem",getHideCreateItem());
        value.put("expanded",getExpanded());

        value.put("enableTableEnterData",getEnableTableEnterData());

        value.put("customFieldType", getCustomFieldType());
        value.put("param1", getParam1());
        value.put("param2", getParam2());
        value.put("param3", getParam3());
        value.put("param4", getParam4());
        value.put("param5", getParam5());

        return value;
    }

    public int compareTo(Field o) {
        return new Integer(getPosition()).compareTo(o.getPosition());
    }

    public static class Comparator implements java.util.Comparator {
        public int compare(Object o1, Object o2) {
            Field f1 = (Field) o1;
            Field f2 = (Field) o2;
            int pos1 = f1.getPosition();
            int pos2 = f2.getPosition();
            if (pos1 != pos2)
                return f1.getPosition() - f2.getPosition();
            else
                return (int) (f1.getId().longValue() - f2.getId().longValue());
        }
    }
}
