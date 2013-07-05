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


import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;

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

    Field addFieldToForm(Form pForm, String fieldName, FieldType fieldType, I18nSet label, String inputBindingString, String outputBindingString) throws Exception;

    Field addFieldToForm(Form form, String s, FieldType fieldType, String fieldClass, I18nSet label, String inputBinging, String outputBinding);

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
