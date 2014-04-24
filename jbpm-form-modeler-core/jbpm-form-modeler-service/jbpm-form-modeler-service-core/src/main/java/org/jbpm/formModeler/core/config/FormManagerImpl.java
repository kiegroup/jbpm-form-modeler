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
import org.jbpm.formModeler.api.model.*;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.service.LocaleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class FormManagerImpl implements FormManager {

    private Logger log = LoggerFactory.getLogger(FormManager.class);

    @Inject
    private DataHolderManager dataHolderManager;

    @Inject
    protected FieldTypeManager fieldTypeManager;

    private HashSet<Form> forms = new HashSet<Form>();

    /**
     * Get all forms by subject.
     */
    public List<Form> getFormsBySubject(String subject) throws Exception {

        if (subject == null) subject = "";

        List<Form> result = new ArrayList<Form>();

        for (Form form : forms) {
            if (subject.equals(form.getSubject())) result.add(form);
        }
        return result;
    }

    /**
     * Get all forms by subject and name.
     */
    public List<Form> getFormsBySubjectAndName(String subject, String name) throws Exception {
        if (subject == null) subject = "";
        if (name == null) name = "";

        List<Form> result = new ArrayList<Form>();

        for (Form form : forms) {
            if (subject.equals(form.getSubject()) && name.equals(form.getName())) result.add(form);
        }

        return result;
    }

    public Form[] getAllForms() {
        return forms.toArray(new Form[forms.size()]);
    }

    /**
     * Get the form to edit a form field depending on its type
     *
     * @param fieldType for which we want to edit
     * @return a form suitable for editing it, or null if none fits
     * @throws Exception in case of error
     */
    public Form getFormForFieldEdition(FieldType fieldType) throws Exception {
        String code = fieldType.getCode();
        if (code == null) {
            logError("Found field type without code.");
        } else {

            // Literal search
            for(Form form : forms) {
                if (form.getName() != null && form.getName().equals(code))
                    return form;
            }
            // Pattern search
            for (Form form : forms) {
                if (form.getName() != null) {
                    try {
                        if (code.matches(form.getName())) {
                            return form;
                        }
                    } catch (Exception pse) {
                        //Ignore wrong patterns
                    }
                }
            }
        }

        Form defaultForm = null;

        List<Form> defaultForms = getFormsBySubjectAndName(Field.class.getName(), "default");

        if (!defaultForms.isEmpty()) defaultForm = defaultForms.get(0);

        return defaultForm;
    }

    /**
     * Get form by id
     *
     * @param id Form id
     * @return a form by id
     */
    public Form getFormById(final Long id) {
        for (Form form : forms) {
            if (form.getId().equals(id)) return form;
        }
        return null;
    }

    public Form createForm(String name) {
        return createForm("", name, Form.DISPLAY_MODE_DEFAULT, new Long(FORMSTATUS_NORMAL));
    }

    /**
     * Create an empty form with given parameters
     *
     * @param subject     Form subject
     * @param name        Form name
     * @param displayMode Form display mode
     * @param status      Form status
     * @return The created form
     * @throws Exception in case of error
     */
    public Form createForm(String subject, String name, String displayMode, Long status) {

        final Form pForm = new Form();
        pForm.setId(generateUniqueId());
        pForm.setSubject(subject);
        pForm.setDisplayMode(displayMode);
        pForm.setStatus(status);
        pForm.setName(name);
        pForm.setFormFields(new HashSet());

        return pForm;
    }

    @Override
    public Field addFieldToForm(Form pForm, FieldType fieldType) throws Exception {
        return addFieldToForm(pForm, "", fieldType, new I18nSet());
    }

    /**
     * Adds a field to a form.
     *
     * @param pForm     Form to be modified
     * @param fieldName Field name to create
     * @param fieldType Field type
     * @throws Exception in case of error
     */
    @Override
    public Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label) {
        return addFieldToForm(pForm, fieldName, fieldType,label, "", "");
    }


    /**
     * Adds a field to a form.
     *
     * @param pForm     Form to be modified
     * @param fieldName Field name to create
     * @param fieldType Field type
     * @throws Exception in case of error
     */
    @Override
    public Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label, String inputBindingString, String outputBindingString) {
        return addFieldToForm(pForm, fieldName, fieldType, null, label, inputBindingString, outputBindingString);
    }

    @Override
    public Field addFieldToForm(Form form, String fieldName, FieldType fieldType, String fieldClass, I18nSet label, String inputBinding, String outputBinding) {
        synchronized (form.getSynchronizationObject()) {
            Set<Field> fields = form.getFormFields();

            if (fieldName != null && !fieldName.isEmpty()) {
                for (Field field : fields) {
                    if (fieldName.equals(field.getFieldName())) {
                        logError("Cannot add field with name " + fieldName + " as it already exists.");
                        return field;
                    }
                }
            }

            Field field = new Field();
            field.setId(generateUniqueId());

            if (StringUtils.isEmpty(fieldName)) fieldName = field.getId().toString();

            field.setFieldName(fieldName);
            field.setFieldRequired(Boolean.FALSE);
            field.setReadonly(Boolean.FALSE);
            field.setFieldType(fieldType);
            field.setInputBinding(inputBinding);
            field.setOutputBinding(outputBinding);
            field.setForm(form);
            field.setPosition(form.getFormFields().size());

            if (label != null) field.setLabel(label);

            if (label != null && !"Separator".equals(fieldType.getCode()) && !"HTMLLabel".equals(fieldType.getCode())){  //default label
                String currentLang = LocaleManager.lookup().getDefaultLang();
                if(label.getValue(currentLang)==null) {
                    label.setValue(currentLang, fieldType.getCode()+field.getPosition());
                }
                field.setLabel(label);
            }

            form.getFormFields().add(field);

            return field;
        }
    }

    protected static Long generateUniqueId() {
        UUID idOne = UUID.randomUUID();
        String str = "" + idOne;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        return Long.decode(str);
    }

    public void promoteField(Form pForm, int fieldPos, int destPos, boolean groupWithPrevious, boolean nextFieldGrouped) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            final List<Field> fields = new ArrayList(pForm.getFormFields());
            Collections.sort(fields, new Field.Comparator());

            boolean wasGrouped = false;
            for (Field formField : fields) {
                int position = formField.getPosition();
                if (position == fieldPos) {
                    formField.setPosition(destPos);
                    wasGrouped = Boolean.TRUE.equals(formField.getGroupWithPrevious());
                    formField.setGroupWithPrevious(Boolean.valueOf(groupWithPrevious));
                } else if (position >= destPos && position < fieldPos) {
                    formField.setPosition(formField.getPosition() + 1);
                    if (position == destPos && !Boolean.TRUE.equals(formField.getGroupWithPrevious()))
                        formField.setGroupWithPrevious(Boolean.valueOf(nextFieldGrouped));
                } else if (position == fieldPos + 1 && Boolean.TRUE.equals(formField.getGroupWithPrevious()))
                    formField.setGroupWithPrevious(Boolean.valueOf(wasGrouped));
            }
        }
    }

    public void degradeField(Form pForm, int fieldPos, int destPos, boolean groupWithPrevious, boolean nextFieldGrouped) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            final List<Field> fields = new ArrayList(pForm.getFormFields());
            Collections.sort(fields, new Field.Comparator());

            boolean wasGrouped = false;
            for (Field formField : fields) {
                int position = formField.getPosition();
                if (position == fieldPos) {
                    formField.setPosition(destPos);
                    wasGrouped = Boolean.TRUE.equals(formField.getGroupWithPrevious());
                    formField.setGroupWithPrevious(Boolean.valueOf(groupWithPrevious));
                } else if (position <= destPos && position > fieldPos) {
                    formField.setPosition(formField.getPosition() - 1);
                    if (position == fieldPos + 1 && Boolean.TRUE.equals(formField.getGroupWithPrevious()))
                        formField.setGroupWithPrevious(Boolean.valueOf(wasGrouped));
                } else if (position == destPos + 1 && !Boolean.TRUE.equals(formField.getGroupWithPrevious()))
                    formField.setGroupWithPrevious(Boolean.valueOf(nextFieldGrouped));
            }
        }
    }

    /**
     * Moves a field to the specified position
     *
     * @param pForm             form to modify
     * @param fieldPos          original field position
     * @param destPos           destination position
     * @param groupWithPrevious determines that the field must be grouped with the previous field
     * @param nextFieldGrouped  determines that the next field must be grouped with the modified field
     * @throws Exception
     */
    public void changeFieldPosition(Form pForm, int fieldPos, int destPos, boolean groupWithPrevious, boolean nextFieldGrouped) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {

            final List<Field> fields = new ArrayList(pForm.getFormFields());
            Collections.sort(fields, new Field.Comparator());

            boolean promote = destPos < fieldPos;

            boolean checkGroup = false;
            Boolean wasGrouped = false;

            for (Field formField : fields) {
                int position = formField.getPosition();

                if (position == fieldPos) {
                    formField.setPosition(destPos);
                    checkGroup = true;
                    wasGrouped = formField.getGroupWithPrevious();
                    formField.setGroupWithPrevious(Boolean.valueOf(groupWithPrevious));
                } else if (promote) {
                    if (position >= destPos && position < fieldPos) {
                        formField.setPosition(formField.getPosition() + 1);
                        if (position == destPos) formField.setGroupWithPrevious(Boolean.valueOf(nextFieldGrouped));
                        if (checkGroup) {
                            if (formField.getGroupWithPrevious()) {
                                formField.setGroupWithPrevious(wasGrouped);
                            }
                        }
                    }
                } else {
                    if (position > fieldPos && position <= destPos) {
                        formField.setPosition(formField.getPosition() - 1);
                        if (position == destPos) formField.setGroupWithPrevious(Boolean.valueOf(nextFieldGrouped));
                        if (checkGroup) {
                            if (formField.getGroupWithPrevious()) {
                                formField.setGroupWithPrevious(wasGrouped);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Moves a field top in a form
     *
     * @param pForm    form to modify
     * @param fieldPos field position to move top
     * @throws Exception in case of error
     */
    public void moveTop(Form pForm, int fieldPos) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            Set<Field> fields = pForm.getFormFields();
            for (Field formField : fields) {
                if (formField.getPosition() == fieldPos) {
                    formField.setPosition(0);
                    formField.setGroupWithPrevious(Boolean.FALSE);
                } else if (formField.getPosition() < fieldPos) {
                    formField.setPosition(formField.getPosition() + 1);
                }
            }
        }
    }

    /**
     * Moves a field bottom in a form
     *
     * @param pForm    form to modify
     * @param fieldPos field position to move down
     * @throws Exception in case of error
     */
    public void moveBottom(Form pForm, int fieldPos) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            Set<Field> fields = pForm.getFormFields();
            for (Field formField : fields) {
                if (formField.getPosition() == fieldPos) {
                    formField.setPosition(fields.size() - 1);
                    formField.setGroupWithPrevious(Boolean.FALSE);
                } else if (formField.getPosition() > fieldPos) {
                    formField.setPosition(formField.getPosition() - 1);
                }
            }
        }
    }

    /**
     * Moves a field up in a form
     *
     * @param pForm    form to modify
     * @param fieldPos field position to move up
     * @throws Exception in case of error
     */
    public boolean moveUp(Form pForm, int fieldPos) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            Set<Field> fields = pForm.getFormFields();
            if (fieldPos < 1 || fieldPos >= fields.size()) {
                logError("Cannot move up field in position " + fieldPos);
                return false;
            } else {
                for (Field formField : fields) {
                    if (formField.getPosition() == fieldPos) {
                        formField.setPosition(fieldPos - 1);
                    } else if (formField.getPosition() == fieldPos - 1) {
                        formField.setPosition(fieldPos);
                    }
                }
                return true;
            }
        }
    }

    /**
     * Moves a field up in a form
     *
     * @param pForm    form to modify
     * @param fieldPos field position to move up
     * @throws Exception in case of error
     */
    public void groupWithPrevious(Form pForm, int fieldPos, boolean value) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            Set<Field> fields = pForm.getFormFields();
            if (fieldPos < 1 || fieldPos >= fields.size()) {
                logError("Cannot change field in position " + fieldPos);
            } else {
                for (Field formField : fields) {
                    if (formField.getPosition() == fieldPos) {
                        formField.setGroupWithPrevious(Boolean.valueOf(value));
                    }
                }
            }
        }
    }

    /**
     * Moves a field down in a form
     *
     * @param pForm    form to modify
     * @param fieldPos field position to move down
     * @throws Exception in case of error
     */
    public boolean moveDown(Form pForm, int fieldPos) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            Set<Field> fields = pForm.getFormFields();
            if (fieldPos < 0 || fieldPos >= fields.size() - 1) {
                logError("Cannot move down field in position " + fieldPos);
                return false;
            } else {
                for (Field formField : fields) {
                    if (formField.getPosition() == fieldPos) {
                        formField.setPosition(fieldPos + 1);
                    } else if (formField.getPosition() == fieldPos + 1) {
                        formField.setPosition(fieldPos);
                    }
                }
                return true;
            }
        }
    }

    public void deleteField(Form pForm, int fieldPos) throws Exception {
        synchronized (pForm.getSynchronizationObject()) {
            Set fields = pForm.getFormFields();
            if (fieldPos < 0 || fieldPos >= fields.size()) {
                logError("Cannot delete field in position " + fieldPos);
            } else {
                for (Iterator iterator = fields.iterator(); iterator.hasNext(); ) {
                    Field formField = (Field) iterator.next();
                    if (formField.getPosition() == fieldPos) {
                        iterator.remove();
                    } else if (formField.getPosition() > fieldPos) {
                        formField.setPosition(formField.getPosition() - 1);
                    }
                }
            }
        }
    }

    public void saveTemplateForForm(Long formId, String templateContent) throws Exception {
        Form form = getFormById(formId);
        form.setFormTemplate(templateContent);
    }

    public String getUniqueIdentifiersPreffix() {
        return "uid_";
    }

    public String getUniqueIdentifier(Form form, String namespace, Field field, String fieldName) {
        if (field != null)
            return field.getFieldType().getUniqueIdentifier(getUniqueIdentifiersPreffix(), namespace, form, field, fieldName);
        return getUniqueIdentifiersPreffix() + "_" + fieldName;
    }

    /**
     * Determine if a form is deleteable, which means it is not in use in other core structures.
     *
     * @param form Form to check
     * @return true if the form is not deleteable.
     */
    public boolean isDeleteable(Form form) {
        return true;
    }

    protected void logWarn(String message) {
        log.warn(message);
    }

    protected void logError(String message) {
        log.error(message);
    }

    @Override
    public void addDataHolderToForm(Form form, String holderType, String id,String inputId, String outId, String color, String value, String path) throws Exception {
        DataHolderBuildConfig config = new DataHolderBuildConfig(id, inputId, outId, color, value);
        config.addAttribute("path", path);

        addDataHolderToForm(form, dataHolderManager.createDataHolderByType(holderType, config));

    }

    @Override
    public void addDataHolderToForm(Form form, DataHolder holder) {
        if (holder != null) form.setDataHolder(holder);
    }

    @Override
    public void removeDataHolderFromForm(Form form, String holderId) {
        if ((holderId != null)) {
            form.removeDataHolder(holderId);
        }
    }

    @Override
    public void addAllDataHolderFieldsToForm(Form form, String holderId) {
        if (holderId != null) {

            DataHolder holder = form.getDataHolderById(holderId);

            addDataHolderFields(form, holder, true);
        }
    }

    @Override
    public void addAllDataHolderFieldsToForm(Form form, DataHolder holder) {
        addDataHolderFields(form, holder, false);
    }

    protected void addDataHolderFields(Form form, DataHolder holder, boolean existing) {
        if (holder != null) {
            if (!existing) {
                if(form.containsHolder(holder)) return;
                else form.setDataHolder(holder);
            }
            if (!form.containsHolder(holder)) return;
            Set<DataFieldHolder> holderFields = holder.getFieldHolders();
            for (DataFieldHolder dataFieldHolder : holderFields) {
                String holderId = holder.getUniqeId();
                if (!form.isFieldBinded(holder, dataFieldHolder.getId())) addDataFieldHolder(form, holderId, dataFieldHolder.getId(), dataFieldHolder.getClassName());
            }
        }
    }

    public void addDataFieldHolder(Form form, String bindingId, String fieldName, String fieldClass) {
        I18nSet label = new I18nSet();
        String defaultLang = LocaleManager.lookup().getDefaultLang();
        DataHolder holder = form.getDataHolderById(bindingId);

        if (holder == null) return;

        String dataHolderId = holder.getUniqeId();
        label.setValue(defaultLang, fieldName + " (" + dataHolderId + ")");

        String inputBinging = holder.getInputBinding(fieldName);
        String outputBinding = holder.getOuputBinding(fieldName);

        FieldType fieldType = null;
        fieldType = fieldTypeManager.getTypeByClass(fieldClass);
        String fName=fieldName;
        if (!holder.canHaveChildren()){
             fName=holder.getUniqeId();
        }else{
            fName=holder.getUniqeId() + "_" + fieldName;
        }

        int i = 0;
        String tmpFName = fName;
        while(form.getField(tmpFName)!=null){
            tmpFName = fName + "_" + i;
        }
        fName=tmpFName;

        addFieldToForm(form, fName, fieldType, fieldClass, label, inputBinging, outputBinding);
    }

    @Override
    public void addSystemForm(Form form) {
        forms.add(form);
    }
}
