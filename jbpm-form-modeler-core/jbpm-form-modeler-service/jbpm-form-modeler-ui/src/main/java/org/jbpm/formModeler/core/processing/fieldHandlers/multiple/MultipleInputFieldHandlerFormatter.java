/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.formModeler.core.processing.fieldHandlers.multiple;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Named("MultipleInputFieldHandlerFormatter")
public class MultipleInputFieldHandlerFormatter extends DefaultFieldHandlerFormatter {
    public static final String PARAM_MODE = "show_mode";
    public static final String MODE_SHOW = "show";
    public static final String MODE_INPUT = "input";

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String mode = (String) getParameter(PARAM_MODE);

        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        String currentNamespace = paramsReader.getCurrentNamespace();
        Object fieldValue = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();

        boolean readOnly = MODE_SHOW.equals(mode) || field.getReadonly() || paramsReader.isFieldReadonly();

        List values = (List) fieldValue;

        FieldType bagType = getFieldTypeManager().getTypeByClass(field.getBag());

        if (bagType == null) return;

        FieldHandler handler = getFieldHandlersManager().getHandler(bagType);

        String uid = namespaceManager.squashInputName(fieldName);
        setAttribute("uid", uid);
        setAttribute("formId", form.getId());
        setAttribute("namespace", currentNamespace);
        setAttribute("fieldName", fieldName);
        renderFragment("outputStart");

        if (!CollectionUtils.isEmpty(values)) {
            renderFragment("tableStart");
            renderFragment("startHeader");
            if (!readOnly) renderFragment("actionsColumn");
            renderFragment("itemsColumn");
            renderFragment("endHeader");
            for (int i = 0; i < values.size(); i++) {
                renderFragment("startRow");
                if (!readOnly) {
                    setAttribute("uid", uid);
                    setAttribute("fieldName", fieldName);
                    setAttribute("index", i);
                    setAttribute("readOnly", readOnly);
                    renderFragment("rowAction");
                }

                renderFragment("inputRow");

                setRenderingAttributes(field, fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i, currentNamespace, values.get(i), readOnly, paramsReader.isWrongField());
                if (readOnly) includePage(handler.getPageToIncludeForDisplaying());
                else includePage(handler.getPageToIncludeForRendering());

                renderFragment("endRow");
            }
            renderFragment("tableEnd");
        }

        renderFragment("beforeEnd");

        if (!readOnly) {
            renderFragment("startAdd");
            setRenderingAttributes(field, fieldName, currentNamespace, null, readOnly, paramsReader.isWrongField());
            includePage(handler.getPageToIncludeForRendering());
            String addItemButtonText = field.getAddItemText().getValue(getLocaleManager().getCurrentLang());
            if (StringUtils.isEmpty(addItemButtonText)) addItemButtonText = "Add new item";
            setAttribute("readOnly", readOnly);
            setAttribute("addItemButtonText", addItemButtonText);
            setAttribute("fieldName", fieldName);
            setAttribute("uid", uid);
            renderFragment("endAdd");
        }
        renderFragment("outputEnd");

    }

    protected void setRenderingAttributes(Field field, String fieldName, String namespace, Object value, boolean isReadOnly, boolean isWrongField) {
        setAttribute(FormRenderingFormatter.ATTR_FIELD, field);
        setAttribute(FormRenderingFormatter.ATTR_VALUE, value);
        setAttribute(FormRenderingFormatter.ATTR_INPUT_VALUE, value != null ? value.toString() : "");
        setAttribute(FormRenderingFormatter.ATTR_FIELD_IS_WRONG, isWrongField);
        setAttribute(FormRenderingFormatter.ATTR_NAMESPACE, namespace);
        setAttribute(FormRenderingFormatter.ATTR_NAME, fieldName);
        setAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY, isReadOnly);
    }
}
