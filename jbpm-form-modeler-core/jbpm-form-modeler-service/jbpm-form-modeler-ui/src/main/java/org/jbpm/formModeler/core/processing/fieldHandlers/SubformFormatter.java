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
package org.jbpm.formModeler.core.processing.fieldHandlers;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Named("SubformFormatter")
public class SubformFormatter extends DefaultFieldHandlerFormatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SubformFormatter.class.getName());

    protected Boolean isDisabled;
    protected Boolean isReadonly;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(httpServletRequest);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        Object value = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();
        int position = paramsReader.getCurrentPosition();
        String namespace = paramsReader.getCurrentNamespace();

        isDisabled = paramsReader.isFieldDisabled();
        isReadonly = paramsReader.isFieldReadonly();

        //if (!isSubformDepthAllowed(form.getDbid(), namespace)) return;

        String formMode = (String) getParameter("formMode");
        formMode = formMode == null ? Form.RENDER_MODE_FORM : formMode;
        boolean valueIsNull = value == null;

        setDefaultAttributes(field, form, namespace);
        setAttribute("valueObject", value);
        setAttribute("position", position);
        setAttribute("name", fieldName);
        setAttribute("uid", getFormManager().getUniqueIdentifier(form, namespace, field, fieldName));
        String height = (field.getHeight() != null && !"".equals(field.getHeight())) ? field.getHeight() : "100";
        setAttribute("heightDesired", height);
        renderFragment("outputStart");

        if (Form.RENDER_MODE_FORM.equals(formMode)) {
            formMode = valueIsNull ? "create" : "edit";
        }

        String renderMode = paramsReader.getCurrentRenderMode();
        if (StringUtils.isEmpty(renderMode))
            renderMode = Form.RENDER_MODE_DISPLAY.equals(formMode) ? formMode : (Form.RENDER_MODE_SEARCH.equals(formMode) ? formMode : Form.RENDER_MODE_FORM);
        renderItemForm(form, field, namespace, fieldName, (Map) value, formMode, renderMode);

        renderFragment("outputEnd");

    }

    protected void renderItemForm(Form form, Field field, String currentNamespace, String fieldName, Map value, String mode, String renderMode) {
        SubformFieldHandler fieldHandler = (SubformFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());

        Form enterDataForm = fieldHandler.getEnterDataForm(mode, currentNamespace, field);
        if (enterDataForm == null) {
            setAttribute("errorMsg", "no" + StringUtils.capitalize(mode) + "Form");
            renderFragment("noFormError");
        } else {
            setAttribute("formId", enterDataForm.getId());
            setAttribute("namespace", currentNamespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName());
            setAttribute("uid", getFormManager().getUniqueIdentifier(form, currentNamespace, field, fieldName));
            setAttribute("name", fieldName);
            setAttribute("formMode", mode);
            setAttribute("renderMode", renderMode);
            // Override the field's own disabled and readonly values with the ones coming from a parent formatter
            // that contains it if they're set to true.
            if (isDisabled) setAttribute("disabled", isDisabled);
            if (isReadonly) setAttribute("readonly", isReadonly);
            if (value != null) {
                setAttribute("formValues", value);
            }
            renderFragment("outputForm");
        }
    }

}
