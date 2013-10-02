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


import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.formRendering.FieldI18nResourceObtainer;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Named("AvailableTemplateElementsFormatter")
public class AvailableTemplateElementsFormatter extends Formatter {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AvailableTemplateElementsFormatter.class.getName());

    @Inject
    private FormTemplateEditor formTemplateEditor;

    @Inject
    private FieldI18nResourceObtainer fieldI18nResourceObtainer;

    public FormTemplateEditor getFormTemplateEditor() {
        return formTemplateEditor;
    }


    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        String typeOfItems = (String) getParameter("type"); //Either Field or Label
        try {
            Form form = getFormTemplateEditor().getForm();
            List fieldsToAdd = new ArrayList();
            Set fields = form.getFormFields();
            for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
                Field formularyField = (Field) iterator.next();
                fieldsToAdd.add(formularyField);
            }
            renderItemsToAdd(fieldsToAdd, typeOfItems);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    protected void renderItemsToAdd(List fieldsToAdd, String type) {
        if (fieldsToAdd != null && !fieldsToAdd.isEmpty()) {
            renderFragment("outputStartItemsToAdd");
            String label="";
            for (int i = 0; i < fieldsToAdd.size(); i++) {
                Field field = (Field) fieldsToAdd.get(i);
                label = fieldI18nResourceObtainer.getFieldLabel(field);
                setAttribute("key", ("Field".equals(type) ? Form.TEMPLATE_FIELD : Form.TEMPLATE_LABEL)
                        + "{" + field.getFieldName() + "}");
                setAttribute("val",
                        field.getFieldName()
                                + (label !=null? " - " + label :"")
                );
                renderFragment("outputItemToAdd");
            }
            renderFragment("outputEndItemsToAdd");
        }
    }


}
