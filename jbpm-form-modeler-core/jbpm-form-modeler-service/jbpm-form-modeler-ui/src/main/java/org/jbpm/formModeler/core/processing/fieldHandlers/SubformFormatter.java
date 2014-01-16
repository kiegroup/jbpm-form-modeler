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

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.checkers.FormCheckResult;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.checkers.SubformChecker;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

@ApplicationScoped
@Named("SubformFormatter")
public class SubformFormatter extends DefaultFieldHandlerFormatter {
    private static transient Logger log = LoggerFactory.getLogger(SubformFormatter.class);

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(httpServletRequest);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        Object value = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();
        int position = paramsReader.getCurrentPosition();
        String namespace = paramsReader.getCurrentNamespace();

        Boolean isReadonly = paramsReader.isFieldReadonly();

        SubformFieldHandler fieldHandler = (SubformFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());

        Form enterDataForm = fieldHandler.getEnterDataForm(namespace, field);
        Set checkers = fieldHandler.getSubformCheckers();
        for(Iterator it = checkers.iterator(); it.hasNext();) {
            SubformChecker checker = (SubformChecker) it.next();
            FormCheckResult result = checker.checkForm(enterDataForm);
            if (!result.isValid()) {
                setAttribute("error", result.getMessageKey());
                renderFragment("renderError");
                return;
            }
        }

        if (!fieldHandler.checkSubformDepthAllowed(form, namespace)) return;

        setDefaultAttributes(field, form, namespace);
        setAttribute("valueObject", value);
        setAttribute("position", position);
        setAttribute("name", fieldName);
        setAttribute("uid", getFormManager().getUniqueIdentifier(form, namespace, field, fieldName));
        String height = (field.getHeight() != null && !"".equals(field.getHeight())) ? field.getHeight() : "100";
        setAttribute("heightDesired", height);
        renderFragment("outputStart");

        String renderMode = paramsReader.getCurrentRenderMode();
        setAttribute("form", enterDataForm);
        setAttribute("namespace", namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName());
        setAttribute("uid", getFormManager().getUniqueIdentifier(form, namespace, field, fieldName));
        setAttribute("name", fieldName);
        setAttribute("renderMode", renderMode);
        // Override the field's own disabled and readonly values with the ones coming from a parent formatter
        // that contains it if they're set to true.
        if (isReadonly) setAttribute("readonly", isReadonly);
        if (value != null) {
            setAttribute("formValues", value);
        }
        renderFragment("outputForm");

        renderFragment("outputEnd");

    }

}
