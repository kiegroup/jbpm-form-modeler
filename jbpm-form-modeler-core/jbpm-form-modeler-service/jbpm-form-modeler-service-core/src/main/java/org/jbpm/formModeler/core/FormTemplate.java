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
package org.jbpm.formModeler.core;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.core.config.FieldTypeManagerImpl;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.wrappers.HTMLi18n;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.apache.commons.lang.ArrayUtils;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.processing.FieldHandler;

import java.util.Iterator;
import java.util.List;

public class FormTemplate extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormTemplate.class.getName());

    private String subject;
    private String name;
    private String displayMode;
    private String labelMode;
    private String showMode;
    private String formProcessor;
    private long status;
    private boolean isDefault;
    private boolean isDefaultView;
    private boolean isShortView;
    private boolean isCreationView;
    private boolean isSearchView;
    private FieldTemplate[] fields;
    private String template;
    private String customPage;

    public FieldTemplate[] getFields() {
        return fields;
    }

    public void setFields(FieldTemplate[] fields) {
        this.fields = fields;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public String getFormProcessor() {
        return formProcessor;
    }

    public void setFormProcessor(String formProcessor) {
        this.formProcessor = formProcessor;
    }

    public boolean isCreationView() {
        return isCreationView;
    }

    public void setCreationView(boolean creationView) {
        isCreationView = creationView;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isDefaultView() {
        return isDefaultView;
    }

    public void setDefaultView(boolean defaultView) {
        isDefaultView = defaultView;
    }

    public boolean isSearchView() {
        return isSearchView;
    }

    public void setSearchView(boolean searchView) {
        isSearchView = searchView;
    }

    public boolean isShortView() {
        return isShortView;
    }

    public void setShortView(boolean shortView) {
        isShortView = shortView;
    }

    public String getLabelMode() {
        return labelMode;
    }

    public void setLabelMode(String labelMode) {
        this.labelMode = labelMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCustomPage() {
        return customPage;
    }

    public void setCustomPage(String customPage) {
        this.customPage = customPage;
    }

    public boolean deploy(FormManagerImpl formsManager) throws Exception {
        List existing = formsManager.getFormsBySubjectAndName(getSubject(), getName());
        if (existing.size() > 0) {
            log.error("There are " + existing.size() + " forms with name=" + getName() + " for subject " + getSubject());
        } else if (existing.size() == 0) {
            //Create the form
            Form newForm = formsManager.createForm(getName());
            fillTypeValues(newForm, formsManager);
            createFields(newForm, getFields(), formsManager);
            return true;
        }
        return false;
    }

    protected void fillTypeValues(Form form, FormManagerImpl formsManager) throws Exception {
        form.setCreationView(Boolean.valueOf(isCreationView()));
        form.setDefault(Boolean.valueOf(isDefault()));
        form.setDefaultView(Boolean.valueOf(isDefaultView()));
        form.setSearchView(Boolean.valueOf(isSearchView()));
        form.setShortView(Boolean.valueOf(isShortView()));
        form.setDisplayMode(getDisplayMode());
        form.setLabelMode(getLabelMode());
        form.setName(getName());
        form.setShowMode(getShowMode());
        form.setStatus(new Long(getStatus()));
        form.setSubject(getSubject());
        if (getTemplate() != null) form.setFormTemplate(getTemplate());
        if (getCustomPage() != null) form.setCustomRenderPage(getCustomPage());
    }

    protected void createFields(Form newForm, FieldTemplate[] fields, FormManagerImpl formsManager) throws Exception {
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                FieldTemplate field = fields[i];
                addFieldToForm(newForm, field, i, formsManager);
            }
        }
    }

    protected void addFieldToForm(Form newForm, FieldTemplate field, int position, FormManagerImpl formsManager) throws Exception {
        FieldType type = FieldTypeManagerImpl.lookup().getTypeByCode(field.getType());
        if (type != null) {
            if (field.getFieldName().startsWith(":")) {
                Field fieldAdded = formsManager.addFieldToForm(newForm, field.getFieldName(), type, new I18nSet(field.getLabel()));
                fillFieldValues(fieldAdded, field);
            } else if (ArrayUtils.contains(((FieldHandler) Factory.lookup(type.getManagerClass())).getCompatibleClassNames(), field.getType())) {
                Field fieldAdded = formsManager.addFieldToForm(newForm, field.getFieldName(), type, new I18nSet(field.getLabel()));
                fillFieldValues(fieldAdded, field);
            } else {
                log.error("Error creating new field for form from template. Property " + field.getFieldName() + " cannot be assigned an incompatible field type. ");
                log.error("Property is " + field.getType() + ", and intended type " + type.getManagerClass() + " is not compatible.");
            }
        }
    }

    protected void fillFieldValues(Field field, FieldTemplate template) throws Exception {
        field.setAccesskey(template.getAccesskey());
        field.setCssStyle(template.getCssStyle());
        field.setDisabled(Boolean.valueOf(template.getDisabled()));
        field.setFieldRequired(Boolean.valueOf(template.getFieldRequired()));
        field.setFormula(template.getFormula());
        field.setGroupWithPrevious(Boolean.valueOf(template.getGroupWithPrevious()));
        field.setHeight(template.getHeight());
        if (template.getMaxlength() > 0)
            field.setMaxlength(new Long(template.getMaxlength()));
        field.setPattern(template.getPattern());
        field.setRangeFormula(template.getRangeFormula());
        field.setReadonly(Boolean.valueOf(template.getReadonly()));
        field.setSize(template.getSize());
        field.setStyleclass(template.getStyleclass());
        if (template.getTabindex() > 0)
            field.setTabindex(new Long(template.getTabindex()));
        if (template.getHtmlContent() != null) {
            HTMLi18n str = new HTMLi18n();
            for (Iterator it = template.getHtmlContent().keySet().iterator(); it.hasNext();) {
                String lang = (String) it.next();
                String val = template.getHtmlContent().getProperty(lang);
                str.setValue(lang, val);
            }
        }

        field.setTitle(new I18nSet(template.getTitle()));
    }

    public String getFactoryPath() {
        return super.getComponentName();
    }
}
