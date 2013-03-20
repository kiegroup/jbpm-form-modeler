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
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.BindingManagerImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class FieldAvailableTypesFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FieldAvailableTypesFormatter.class.getName());

    private WysiwygFormEditor editor;

    public WysiwygFormEditor getEditor() {
        return editor;
    }

    public void setEditor(WysiwygFormEditor editor) {
        this.editor = editor;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Field field = getEditor().getCurrentEditField();
            Form form = getEditor().getCurrentEditForm();
            String propertyName = field.getFieldName();


            // TODO: fix that to load properties from pojos!
            List suitableFieldTypes = null;
            if (!"void".equals(field.getFieldType().getFieldClass())) {
                if (!StringUtils.isEmpty(form.getSubject())) suitableFieldTypes = editor.getFieldTypesManager().getSuitableFieldTypes(propertyName, BindingManagerImpl.lookup().getPropertyDefinition(propertyName, form.getSubject()));
                else suitableFieldTypes = editor.getFieldTypesManager().getSuitableFieldTypes(propertyName, BindingManagerImpl.lookup().getPropertyDefinition(field.getFieldType()));
            }


            if (suitableFieldTypes != null && !suitableFieldTypes.isEmpty()) {
                renderFragment("outputStart");

                String currentType = editor.getFieldTypeToView();
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
