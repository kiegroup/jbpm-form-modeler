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
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Named("FormFieldEditionFormatter")
public class FormFieldEditionFormatter extends FormRenderingFormatter {
    private Logger log = LoggerFactory.getLogger(FormFieldEditionFormatter.class);

    public WysiwygFormEditor getEditor() {
        return WysiwygFormEditor.lookup();
    }

    public FieldTypeManager getFieldTypesManager() {
        return FormCoreServices.lookup().getFieldTypeManager();
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Field field = getEditor(). getCurrentEditField();
            if (field == null) {
                renderFragment("empty");
            } else {
                Form formToEdit = getFormForFieldEdition(field);
                if (formToEdit != null) {
                    renderFieldUsingForm(field, formToEdit);
                } else {
                    renderField(field);
                }
            }
        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    public Form getFormForFieldEdition(Field field) throws Exception {
        if (getEditor() != null) {
            return getEditor(). getFormForFieldEdition(field);
        }
        return null;
    }

    protected void renderFieldUsingForm(Field field, Form formToEdit) throws Exception {
        String fieldName = field.getFieldName();
        boolean isDecorator = field.getFieldName().startsWith(":");
        if (isDecorator) fieldName = "{" + fieldName + "}";
        if(field.getLabel()!=null && (field.getLabel().getValue(getLocaleManager().getDefaultLang())!=null)){
           fieldName =field.getLabel().getValue(getLocaleManager().getDefaultLang());
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
            type = getFieldTypesManager().getTypeByCode(memoryType);
        }
        setAttribute("fieldType", type);
        renderFragment("fieldCustomForm");
        setAttribute("fieldName", fieldName);
        renderFragment("outputEnd");
    }

    protected String getFieldTypeToView() {
        return getEditor(). getFieldTypeToView();
    }

    protected void removeHiddenProperties(Set parametersNames) {
        parametersNames.remove("fieldClass");
        parametersNames.remove("fieldName");
        parametersNames.remove("name");
        parametersNames.remove("tabindex");
        parametersNames.remove("accesskey");
    }

    protected void renderField(Field field) throws Exception {
        FieldType type = field.getFieldType();
        Set parameterNames = new TreeSet();
        parameterNames.addAll(type.getPropertyNames());
        parameterNames.addAll(field.getPropertyNames());
        removeHiddenProperties(parameterNames);

        List parametersNames = new ArrayList();
        parametersNames.addAll(parameterNames);
        String[] desiredOrder = new String[]{
                "label",
                "title",
                "alt",
                "styleclass",
                "cssStyle",
                "size",
                "height",
                "maxlength",
                "fieldRequired",
                "disabled",
                "readonly",
                "formula",
                "rangeFormula",
                "pattern",
                "groupWithPrevious",
                "accesskey",
                "tabindex"};
        final List desiredOrderList = Arrays.asList(desiredOrder);
        Collections.sort(parametersNames, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1;
                String s2 = (String) o2;
                int pos1 = desiredOrderList.indexOf(s1);
                if (pos1 == -1) pos1 = Integer.MAX_VALUE;
                int pos2 = desiredOrderList.indexOf(s2);
                if (pos2 == -1) pos2 = Integer.MAX_VALUE;
                if (pos1 == pos2) {
                    return s1.compareTo(s2);
                }
                return pos1 - pos2;
            }
        });


        if (parametersNames.isEmpty()) {
            renderFragment("noFields");
        } else {
            String fieldName = field.getFieldName();
            boolean isDecorator = field.getFieldName().startsWith(":");
            if (isDecorator) fieldName = "{" + fieldName + "}";
            setAttribute("fieldName", fieldName);
            setAttribute("isDecorator", isDecorator);
            renderFragment("outputStart");
            int index = 0;
            for (Iterator iterator = parametersNames.iterator(); iterator.hasNext(); index++) {
                String paramName = (String) iterator.next();
                boolean canShowInput = false;
                setAttribute("index", index);
                setAttribute("name", paramName);
                renderFragment("outputName");

                List suitableTypes = getFieldTypesManager().getSuitableFieldTypes(paramName, "");
                if (suitableTypes != null && suitableTypes.size() > 0) {
                    FieldType pFtype = (FieldType) suitableTypes.get(0);
                    FieldHandler fieldHandler = getFieldHandlersManager().getHandler(pFtype);
                    String renderPage = fieldHandler.getPageToIncludeForRendering();
                    String displayPage = fieldHandler.getPageToIncludeForDisplaying();
                    renderFragment("beforeDefaultValue");
                    //setAttribute(ATTR_VALUE, hasDefaultValue ? type.getPropertyValue(paramName) : null);
                    setAttribute(ATTR_VALUE, "");
                    setAttribute(ATTR_NAME, "_$default_" + paramName);
                    includePage(displayPage);
                    renderFragment("afterDefaultValue");
                    if (canShowInput) {
                        renderFragment("beforeInput");
                        //setAttribute(ATTR_VALUE, field.getPropertyValue(paramName));
                        setAttribute(ATTR_VALUE, "");
                        setAttribute(ATTR_NAME, "_$" + paramName);
                        setAttribute("styleclass", "skn-input");
                        includePage(renderPage);
                        renderFragment("afterInput");
                    } else {
                        renderFragment("cantShowInput");
                    }
                } else {
                    log.error("Cannot show input for field property named " + paramName);
                    renderFragment("errorShowingInput");
                }

                renderFragment("outputNameEnd");
            }
            renderFragment("outputEnd");
        }
    }
}
