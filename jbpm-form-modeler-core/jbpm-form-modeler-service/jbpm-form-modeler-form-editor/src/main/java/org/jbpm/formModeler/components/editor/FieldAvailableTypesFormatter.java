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
import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.processing.BindingManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Named("FieldAvailableTypesFormatter")
public class FieldAvailableTypesFormatter extends Formatter {

    @Inject
    private Log log;

    public WysiwygFormEditor getEditor() {
        return WysiwygFormEditor.lookup();
    }
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Field field = getEditor().getCurrentEditField();
            Form form = getEditor().getCurrentForm();
            String propertyName = field.getFieldName();


            // TODO: fix that to load properties from pojos!
            List suitableFieldTypes = null;
            if (!"void".equals(field.getFieldType().getFieldClass())) {
                BindingManager bindingManager = FormCoreServices.lookup().getBindingManager();
                if (!StringUtils.isEmpty(form.getSubject())) suitableFieldTypes = getEditor().getFieldTypesManager().getSuitableFieldTypes(propertyName, bindingManager.getPropertyDefinition(propertyName, form.getSubject()));
                else suitableFieldTypes = getEditor().getFieldTypesManager().getSuitableFieldTypes(propertyName, bindingManager.getPropertyDefinition(field.getFieldType()));
            }


            if (suitableFieldTypes != null && !suitableFieldTypes.isEmpty()) {
                renderFragment("outputStart");

                String currentType = getEditor().getFieldTypeToView();
                currentType = currentType == null ? field.getFieldType().getCode() : currentType;

                for (int i = 0; i < suitableFieldTypes.size(); i++) {
                    FieldType type = (FieldType) suitableFieldTypes.get(i);
                    setAttribute("id", type.getCode());
                    if (currentType.equals(type.getCode())) {
                        renderFragment("outputSelected");
                    } else {
                        renderFragment("output");
                    }
                }
                renderFragment("outputEnd");
            } else {
                setAttribute("id", field.getFieldType().getCode());
                renderFragment("empty");
            }

        } catch (Exception e) {
            log.error("Error:", e);
        }
    }
}
