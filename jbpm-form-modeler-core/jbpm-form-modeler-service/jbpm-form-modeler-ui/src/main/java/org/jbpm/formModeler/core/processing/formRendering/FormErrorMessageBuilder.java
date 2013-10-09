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
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.core.processing.fieldHandlers.SubformFieldHandler;
import org.jbpm.formModeler.service.LocaleManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Builds form error messages
 */
public class FormErrorMessageBuilder {

    private Logger log = LoggerFactory.getLogger(FormErrorMessageBuilder.class);

    @Inject
    protected FieldI18nResourceObtainer fieldI18nResourceObtainer;

    @Inject
    protected LocaleManager localeManager;

    public List getWrongFormErrors(String namespace, Form form) {
        return getWrongFormErrors(namespace, form, new ArrayList<String>());
    }

    public List getWrongFormErrors(String namespace, Form form, List<String> errors) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.formRendering.messages", localeManager.currentLocale());
        String requiredMessage = bundle.getString("errorMessages.required");

        if (namespace != null && form != null) {
            try {
                
                FormStatusData statusData = FormProcessingServices.lookup().getFormProcessor().read(form, namespace);

                for (int i = 0; i < statusData.getWrongFields().size(); i++) {
                    Field field = form.getField((String) statusData.getWrongFields().get(i));
                    FieldHandler fieldHanlder = (FieldHandler) CDIBeanLocator.getBeanByNameOrType(field.getFieldType().getManagerClass());
                    boolean isSubform = fieldHanlder instanceof SubformFieldHandler;
                    Boolean fieldIsRequired = field.getFieldRequired();
                    boolean fieldRequired = fieldIsRequired != null && fieldIsRequired.booleanValue() && !Form.RENDER_MODE_DISPLAY.equals(fieldIsRequired);
                    String currentNamespace = namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId().intValue() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
                    String currentValue = statusData.getCurrentInputValue(currentNamespace);
                    if (isSubform) {
                        ((SubformFieldHandler) fieldHanlder).addWrongChildFieldErrors(currentNamespace, field, errors);
                    } else {
                        if (!statusData.hasErrorMessage(field.getFieldName())) {
                            if (fieldRequired && StringUtils.isEmpty(currentValue)) {
                                if (!errors.contains(requiredMessage)) errors.add(0, requiredMessage);
                            } else {
                                String error = fieldI18nResourceObtainer.getFieldErrorMessage(field);
                                addErrorMessage(getErrorMessage(error, field, bundle), errors);
                            }
                        } else addErrorMessages(statusData.getErrorMessages(field.getFieldName()), field, bundle, errors);
                    }
                }
            } catch (Exception e) {
                log.error("Error getting error messages for form " + form.getId() + ": ", e);
            }
        }
        return errors;
    }
    
    protected void addErrorMessages(List msgs, Field field, ResourceBundle bundle, List<String> errors) {
        if (CollectionUtils.isEmpty(msgs)) return;

        for (Object msg : msgs) {
            String error = getErrorMessage((String) msg, field, bundle);

            addErrorMessage(error, errors);
        }
    }

    protected void addErrorMessage(String error, List<String> errors) {
        if (!StringUtils.isEmpty(error) && !errors.contains(error)) errors.add(error);
    }

    protected String getErrorMessage(String msg, Field field, ResourceBundle bundle) {
        if (StringUtils.isEmpty(msg)) return "";
        
        StringBuffer result = new StringBuffer();
        String label = StringUtils.defaultString(fieldI18nResourceObtainer.getFieldLabel(field), field.getFieldName());
        result.append(bundle.getString("error.start")).append(label).append(bundle.getString("error.end")).append(msg);
        
        return result.toString();
    }
}
