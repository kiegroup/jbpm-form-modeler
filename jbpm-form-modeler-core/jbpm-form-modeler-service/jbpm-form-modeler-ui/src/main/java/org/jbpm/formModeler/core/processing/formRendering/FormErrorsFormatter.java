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
package org.jbpm.formModeler.core.processing.formRendering;

import org.slf4j.Logger;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Renders a dynamic form errors
 */
@Named("FormErrorsFormatter")
public class FormErrorsFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(FormErrorsFormatter.class);

    @Inject @Config("5")
    private int maxVisibleErrors;

    @Inject
    private SubformFinderService subformFinderService;

    public int getMaxVisibleErrors() {
        return maxVisibleErrors;
    }

    public void setMaxVisibleErrors(int maxVisibleErrors) {
        this.maxVisibleErrors = maxVisibleErrors;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {

        String namespace = httpServletRequest.getAttribute("namespace") != null ? (String) httpServletRequest.getAttribute("namespace") : "";
        Long formId = httpServletRequest.getAttribute("formId") != null ? (Long) httpServletRequest.getAttribute("formId") : null;

        List errorsToShow = getFormFieldErrors(namespace, formId);

        if (errorsToShow.size() > 0) {
            renderFragment("outputStart");
            renderFragment("outputErrorsStart");

            for (int i = 0; i < errorsToShow.size(); i++) {
                setAttribute("errorMsg", errorsToShow.get(i));
                setAttribute("namespace", namespace);
                setAttribute("index", i);
                setAttribute("display", i < getMaxVisibleErrors() ? "" : "none");
                renderFragment("outputError");
            }
            renderFragment("outputErrorsEnd");

            if (errorsToShow.size() > getMaxVisibleErrors()) {
                setAttribute("namespace", namespace);
                setAttribute("min", getMaxVisibleErrors());
                setAttribute("max", errorsToShow.size());
                renderFragment("outputDisplayLinks");
            }
            renderFragment("outputEnd");
        }
    }

    public List getFormFieldErrors(String namespace, Long formId) {
        List errorsToShow = new ArrayList();
        if (formId != null && namespace != null) {
            try {
                Form form = subformFinderService.getFormById(formId, namespace);
                FormStatusData statusData = FormProcessingServices.lookup().getFormProcessor().read(form, namespace);
                for (int i = 0; i < statusData.getWrongFields().size(); i++) {
                    Field field = form.getField((String) statusData.getWrongFields().get(i));
                    Boolean fieldIsRequired = field.getFieldRequired();
                    boolean fieldRequired = fieldIsRequired != null && fieldIsRequired.booleanValue() && !Form.RENDER_MODE_DISPLAY.equals(fieldIsRequired);
                    String currentValue = statusData.getCurrentInputValue(namespace + FormProcessor.NAMESPACE_SEPARATOR + formId.intValue() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName());
                    if (fieldRequired && (currentValue == null || currentValue.trim().equals(""))) {
                        errorsToShow.clear();
                        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.formRendering.messages", LocaleManager.currentLocale());
                        errorsToShow.add(bundle.getString("errorMessages.required"));
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("Error getting error messages for object " + formId + ": ", e);
            }
        }
        return errorsToShow;
    }
}
