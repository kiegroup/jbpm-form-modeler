package org.jbpm.formModeler.api.config;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.i18n.I18nSet;

import java.io.Serializable;
import java.util.List;

/**
 * Manager interface form Forms.
 * <p/>It provides the common operations over Forms (creation, edit, delete) and distinct search methods.
 */
public interface FormManager extends Serializable {

    public static final int FORMSTATUS_NORMAL = 0;
    public static final int FORMSTATUS_HIDDEN = 1;

    List<Form> getFormsBySubject(String subject) throws Exception;

    List<Form> getFormsBySubjectAndName(String subject, String name) throws Exception;

    Form[] getAllForms();

    Form getFormForFieldEdition(FieldType fieldType) throws Exception;

    Form getFormById(Long id);

    Form createForm(String name);

    Form createForm(String subject, String name, String displayMode, Long status);

    Form cloneForm(Long id);

    Form cloneForm(Form source) throws Exception;

    Form duplicateForm(Long duplicateId, String name, String displayMode, Long status) throws Exception;

    void replaceForm(Long sourceId, Form dest);

    void replaceForm(Form source, Form dest);

    void deleteForm(Form pForm) throws Exception;

    Field addFieldToForm(Form pForm, FieldType fieldType) throws Exception;

    Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label) throws Exception;

    Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label,String bindingStr) throws Exception;

    void promoteField(Form pForm, int fieldPos, int destPos, boolean groupWithPrevious, boolean nextFieldGrouped) throws Exception;

    void degradeField(Form pForm, int fieldPos, int destPos, boolean groupWithPrevious, boolean nextFieldGrouped) throws Exception;

    void changeFieldPosition(Form pForm, int fieldPos, int destPos, boolean groupWithPrevious, boolean nextFieldGrouped) throws Exception;

    void moveTop(Form pForm, int fieldPos) throws Exception;

    void moveBottom(Form pForm, int fieldPos) throws Exception;

    boolean moveUp(Form pForm, int fieldPos) throws Exception;

    void groupWithPrevious(Form pForm, int fieldPos, boolean value) throws Exception;

    boolean moveDown(Form pForm, int fieldPos) throws Exception;

    void deleteField(Form pForm, int fieldPos) throws Exception;

    void saveTemplateForForm(Long formId, String templateContent) throws Exception;

    String getUniqueIdentifiersPreffix();

    String getUniqueIdentifier(Form form, String namespace, Field field, String fieldName);
}
