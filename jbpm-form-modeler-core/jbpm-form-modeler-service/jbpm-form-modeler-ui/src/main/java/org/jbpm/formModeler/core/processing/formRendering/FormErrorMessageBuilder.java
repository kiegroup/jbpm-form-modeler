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

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.service.LocaleManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.processing.FormStatusData;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Builds form error messages
 */
public class FormErrorMessageBuilder {

    @Inject
    private Log log;
    
    private ResourceBundle bundle;
    private String requiredMessage;

    @PostConstruct
    protected void init() {
        bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.formRendering.messages", LocaleManager.currentLocale());
        requiredMessage = bundle.getString("errorMessages.required");
    }

    public List getWrongFormErrors(String namespace, Form formulary) {
        List errors = new ArrayList();
        if (namespace != null && formulary != null) {
            try {
                
                FormStatusData statusData = FormProcessingServices.lookup().getFormProcessor().read(formulary.getId(), namespace);

                for (int i = 0; i < statusData.getWrongFields().size(); i++) {
                    Field field = formulary.getField((String) statusData.getWrongFields().get(i));
                    Boolean fieldIsRequired = field.getFieldRequired();
                    boolean fieldRequired = fieldIsRequired != null && fieldIsRequired.booleanValue() && !Form.RENDER_MODE_DISPLAY.equals(fieldIsRequired);
                    String currentNamespace = namespace + FormProcessor.NAMESPACE_SEPARATOR + formulary.getId().intValue() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
                    String currentValue = statusData.getCurrentInputValue(currentNamespace);
                    if (!statusData.hasErrorMessage(field.getFieldName())) {
                        if (fieldRequired && StringUtils.isEmpty(currentValue)) {
                            if (!errors.contains(requiredMessage)) errors.add(0, requiredMessage);
                        }
                    } else errors.addAll(getErrorMessages(statusData.getErrorMessages(field.getFieldName()), field));

                }
            } catch (Exception e) {
                log.error("Error getting error messages for form " + formulary.getId() + ": ", e);
            }
        }
        return errors;
    }
    
    protected List getErrorMessages(List msgs, Field field) {
        if (CollectionUtils.isEmpty(msgs)) return Collections.EMPTY_LIST;
        
        List result = new ArrayList();
        for (Object msg : msgs) {
            result.add(getErrorMessage((String) msg, field));
        }
        return result;
    }
    
    protected String getErrorMessage(String msg, Field field) {
        if (StringUtils.isEmpty(msg)) return "";
        
        StringBuffer result = new StringBuffer();
        String label = field.getFieldName().indexOf(":decorator") > -1 ? (String) field.getLabel().get(LocaleManager.currentLang()) : field.getFieldName();
        result.append(bundle.getString("error.start")).append(label).append(bundle.getString("error.end")).append(msg);
        
        return result.toString();
    }
}
