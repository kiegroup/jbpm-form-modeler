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

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.FormDisplayInfo;
import org.jbpm.formModeler.api.util.helpers.EditorHelper;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;
import org.jbpm.formModeler.service.LocaleManager;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FormManagerImpl implements FormManager {

    @Inject
    private EditorHelper editorHelper;

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
            List allFormsForField = getFormsBySubject(Field.class.getName());
            // Literal search
            for (int i = 0; i < allFormsForField.size(); i++) {
                Form form = (Form) allFormsForField.get(i);
                if (form.getName() != null && form.getName().equals(code))
                    return form;
            }
            // Pattern search
            for (int i = 0; i < allFormsForField.size(); i++) {
                Form form = (Form) allFormsForField.get(i);
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

        forms.add(pForm);

        return pForm;
    }

    /**
     * Create an empty form with given parameters
     *
     * @param duplicateId Form id to duplicate
     * @param name        Form name
     * @param displayMode Form display mode
     * @param status      Form status
     * @return The created form
     * @throws Exception in case of error
     */
    public Form duplicateForm(Long duplicateId, String name, String displayMode, Long status) throws Exception {
        Form sourceForm = getFormById(duplicateId);
        if (sourceForm != null) {
            final Form createdForm = createForm(sourceForm.getSubject(), name, displayMode, status);
            copyStructure(sourceForm, createdForm);
            return createdForm;
        } else {
            logWarn("Cannot duplicate form with id " + duplicateId + " as it doesn't exist.");
            return null;
        }
    }

    /**
     * Clones the form identified by the given id
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Form cloneForm(Long id) {
        return cloneForm(getFormById(id));
    }

    /**
     * Clones the given form
     *
     * @param source
     * @return
     * @throws Exception
     */
    public Form cloneForm(Form source) {
        if (source == null) return null;

        Form destination = new Form();

        copyStructure(source, destination);

        destination.setId(generateUniqueId());
        destination.setStatus(new Long(FORMSTATUS_HIDDEN));

        forms.add(destination);

        return destination;
    }

    /**
     * Copy form structure from one form to another.
     */
    protected void copyStructure(Form sourceForm, Form destinationForm) {

        SortedSet<Field> sourceFields = new TreeSet<Field>(new Field.Comparator());
        sourceFields.addAll(sourceForm.getFormFields());

        for (Field field : sourceFields) {
            Field addedField = addFieldToForm(destinationForm, field.getFieldName(), field.getFieldType(), field.getLabel());
            addedField.putAll(field);
        }

        // Copy properties
        destinationForm.setName(sourceForm.getName());
        destinationForm.setSubject(sourceForm.getSubject());
        destinationForm.setFormTemplate(sourceForm.getFormTemplate());
        destinationForm.setStatus(sourceForm.getStatus());
        destinationForm.setShowMode(sourceForm.getShowMode());
        destinationForm.setLabelMode(sourceForm.getLabelMode());
        destinationForm.setDisplayMode(sourceForm.getDisplayMode());
        destinationForm.setFormDisplayInfos(cloneDisplayInfos(sourceForm.getFormDisplayInfos()));
    }

    protected Set<FormDisplayInfo> cloneDisplayInfos(Set<FormDisplayInfo> source) {
        Set<FormDisplayInfo> result = new TreeSet<FormDisplayInfo>();
        if (source != null) {
            for (FormDisplayInfo info : source) {
                FormDisplayInfo res = new FormDisplayInfo();
                res.setDisplayData(info.getDisplayData());
                res.setDisplayMode(info.getDisplayMode());
                res.setDisplayModifier(info.getDisplayModifier());

                result.add(res);
            }
        }
        return  result;
    }

    protected boolean validateFieldsOrder(final Form pForm) throws Exception {
        boolean anyErrorsFound = false;
        Set<Field> fields = pForm.getFormFields();
        if (fields != null) {
            TreeSet<Field> sortedFields = new TreeSet(new Field.Comparator());
            sortedFields.addAll(fields);
            int index = 0;
            for (Field field : sortedFields) {
                if (field.getPosition() != index) {
                    logWarn("Field in position " + index + " has position " + field.getPosition() + ". Fixing it.");
                    field.setPosition(index);
                    anyErrorsFound = true;
                }
                index++;
            }
        }
        return anyErrorsFound;
    }

    public void replaceForm(Long sourceId, Form dest) {
        replaceForm(getFormById(sourceId), dest);
    }

    public void replaceForm(Form source, Form dest) {
        if (source == null || dest == null) {
            logWarn("Error trying to replace source form: " + source + " with form: " + dest);
            return;
        }
        deleteForm(source);
        deleteForm(dest);
        dest.setId(source.getId());
        dest.setStatus(new Long(FORMSTATUS_NORMAL));
        forms.add(dest);
    }

    /**
     * Deletes a form
     *
     * @param pForm form to delete
     * @throws Exception in case of error
     */
    public void deleteForm(Form pForm) {
        if (pForm == null) return;
        forms.remove(pForm);
    }

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
    public Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label) {
        return addFieldToForm(pForm, fieldName, fieldType,label,"");
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
    public Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label,String bindingExpresion) {
        synchronized (pForm.getSynchronizationObject()) {
            Set<Field> fields = pForm.getFormFields();

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
            field.setFieldType(fieldType);
            field.setBindingStr(bindingExpresion);
            field.setForm(pForm);
            field.setPosition(pForm.getFormFields().size());


            if (label != null){
                String currentLang = LocaleManager.lookup().getDefaultLang();
                if(label.getValue(currentLang)==null) {
                    label.setValue(currentLang, fieldType.getCode()+field.getPosition());
                }
                field.setLabel(label);
            }

            pForm.getFormFields().add(field);

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
        Logger.getLogger(FormManagerImpl.class.getName()).log(Level.WARNING, message);
    }

    protected void logError(String message) {
        Logger.getLogger(FormManagerImpl.class.getName()).log(Level.SEVERE, message);
    }
}
