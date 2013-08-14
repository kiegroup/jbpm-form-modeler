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

import org.slf4j.Logger;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Named("WysiwygFieldsToAddFormatter")
public class WysiwygFieldsToAddFormatter extends Formatter {
    private Logger log = LoggerFactory.getLogger(WysiwygFieldsToAddFormatter.class);

    private void renderFieldsToAdd(Form form) throws Exception {
        renderFragment("fieldsToAddStart");
        Set propertiesToShow = getPropertiesToShow(form);
        if (propertiesToShow.isEmpty()) {
            renderFragment("empty");
        } else {
            renderFragment("outputFieldsToAddStart");

            int index = 0;
            for (Iterator iterator = propertiesToShow.iterator(); iterator.hasNext(); index++) {
                String propertyName = (String) iterator.next();
                //Class propertyType = editor.getDdmManager().getPropertyType(propertyName, form.getSubject()).getPropertyClass();
                List suitableFieldTypes = new ArrayList();//editor.getFieldTypesManager().getSuitableFieldTypes(propertyName, editor.getDdmManager().getPropertyType(propertyName, form.getSubject()));

                if (suitableFieldTypes.isEmpty()) {
                    log.warn("No field type valid for property " + propertyName + " with no type ");
                    continue;
                }

                setAttribute("name", propertyName);
                setAttribute("position", index);
                renderFragment("outputPropertyStart");
                setAttribute("name", propertyName);
                setAttribute("position", index);
                renderFragment("outputProperty");

                renderFragment("startFieldTypes");
                for (int i = 0; i < suitableFieldTypes.size(); i++) {
                    FieldType fieldType = (FieldType) suitableFieldTypes.get(i);
                    setAttribute("id", fieldType.getCode());
                    renderFragment("outputFieldType");
                }
                renderFragment("endFieldTypes");

                setAttribute("name", propertyName);
                setAttribute("position", index);
                renderFragment("outputPropertyEnd");
            }

            renderFragment("outputFieldsToAddEnd");
        }
        //renderDecorators();
        renderFragment("fieldsToAddEnd");
    }

    /**
     * Calculate properties to show for a given form and entity
     *
     * @param form
     * @return properties to show
     */
    protected Set getPropertiesToShow(Form form) {
        Set propertiesToShow = new TreeSet();

        // TODO: falta cargar propiedades del pojo
        Set entityPropertyNames = new TreeSet();
        //editor.getDdmManager().getPropertyNames(form.getSubject());
        propertiesToShow.addAll(entityPropertyNames);
        if (form.getFormFields() != null)
            for (Field formField :  form.getFormFields()) {
                propertiesToShow.remove(formField.getFieldName());
            }
        return propertiesToShow;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Form form = WysiwygFormEditor.lookup().getCurrentForm();
            if (form != null) {
                //Fields are no longer added from the top:
                renderFieldsToAdd(form);
            }
        }
        catch (Exception e) {
            log.error("Error: ", e);
            throw new FormatterException(e);
        }
    }
}
