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
package org.jbpm.formModeler.components.editor;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.processing.formRendering.FieldI18nResourceObtainer;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.slf4j.Logger;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("FieldEditionFormatter")
public class FieldEditionFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(FieldEditionFormatter.class);

    @Inject
    private FieldTypeManager fieldTypeManager;

    @Inject
    private WysiwygFormEditor wysiwygFormEditor;

    @Inject
    protected FieldI18nResourceObtainer fieldI18nResourceObtainer;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Field field = wysiwygFormEditor.getCurrentEditField();
            if (field == null) return;

            Form formToEdit = wysiwygFormEditor.getFormForFieldEdition(field);
            if (formToEdit != null) renderFieldUsingForm(field, formToEdit);

        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    protected void renderFieldUsingForm(Field field, Form formToEdit) throws Exception {
        String fieldName = field.getFieldName();
        boolean isDecorator = field.getFieldName().startsWith(":");
        if (isDecorator) fieldName = "{" + fieldName + "}";

        String label =  fieldI18nResourceObtainer.getFieldLabel(field);
        if(StringUtils.isEmpty(label)){
            fieldName = label;
        }
        setAttribute("fieldName", fieldName);
        setAttribute("isDecorator", isDecorator);
        renderFragment("outputStart");
        setAttribute("namespace", getParameter("namespace"));
        setAttribute("form", formToEdit);
        setAttribute("editClass", Field.class.getName());
        setAttribute("editId", field.getId());
        FieldType type = field.getFieldType();
        String memoryType = getFieldTypeToView();
        if (memoryType != null) {
            type = fieldTypeManager.getTypeByCode(memoryType);
        }
        setAttribute("fieldType", type);
        renderFragment("fieldCustomForm");
        setAttribute("fieldName", fieldName);
        renderFragment("outputEnd");
    }

    protected String getFieldTypeToView() {
        return wysiwygFormEditor.getFieldTypeToView();
    }
}
