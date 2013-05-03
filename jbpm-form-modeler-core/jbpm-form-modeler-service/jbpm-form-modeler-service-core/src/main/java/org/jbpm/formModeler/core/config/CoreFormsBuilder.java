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
package org.jbpm.formModeler.core.config;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.service.annotation.Priority;
import org.jbpm.formModeler.service.annotation.Startable;
import org.jbpm.formModeler.service.annotation.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.InputStream;

/**
 * Class that builds the forms modeler's core forms.
 */
@ApplicationScoped
public class CoreFormsBuilder implements Startable {

    @Inject
    private Log log;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject @Config("default,CheckBox,Date,FreeText,HTMLEditor,HTMLLabel,I18nHTMLText,I18nText,I18nTextArea,InputText," +
                    "InputTextArea,InputTextCCC,InputTextCP,InputTextDouble,InputTextEmail,InputTextIBAN,InputTextInteger," +
                    "InputTextLong,InputTextPhone,Link,Separator")
    protected String[] coreFormNames;

    public Priority getPriority() {
        return Priority.HIGH;
    }

    public String getFormPath(String formName) {
        return "org/jbpm/formModeler/core/forms/" + formName + ".form";
    }

    public void start() {
        for (String formName : coreFormNames) {
            String formPath = getFormPath(formName);
            try {
                // Form is read, deserialized and added to the form manager.
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(formPath);
                formSerializationManager.loadFormFromXML(is);
            } catch (Exception e) {
                log.error("Error reading core form file: " + formPath, e);
            }
        }
    }
}
