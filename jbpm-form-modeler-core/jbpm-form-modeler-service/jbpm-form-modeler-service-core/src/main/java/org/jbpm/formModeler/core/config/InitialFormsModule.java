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

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;
import org.jbpm.formModeler.core.FormTemplate;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.InitialModule;
import org.jbpm.formModeler.core.FormTemplate;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class InitialFormsModule extends InitialModule {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(InitialFormsModule.class.getName());

    private FormTemplate[] formTemplates;

    protected boolean install() {
        if (formTemplates != null) {
            Set usedCodes = new HashSet();

            FormManagerImpl formManagerImpl = FormManagerImpl.lookup();

            for (int i = 0; i < formTemplates.length; i++) {
                FormTemplate template = formTemplates[i];
                if (!usedCodes.add(new String[]{template.getSubject(), template.getName()})) {
                    log.error("Repeated form template named " + template.getName() + " for entity " +
                            template.getSubject() + ". Ignoring occurrence.");
                } else try {
                    if (template.deploy(formManagerImpl)) {
                        log.info("Creating or updating form based in template: " + template.getFactoryPath());
                    }
                } catch (Exception e) {
                    log.error("Error deploying template: ", e);
                    return false;
                }
            }
            try {
                FormSerializationManagerImpl serializer = (FormSerializationManagerImpl) CDIHelper.getBeanByType(FormSerializationManagerImpl.class);
                for (Form form: formManagerImpl.getAllForms()) {
                    File file = new File("/home/pefernan/forms/" + form.getName() + ".form");

                    if (file.exists()) file.delete();
                    file.createNewFile();
                    PrintWriter pw = new PrintWriter(file);
                    pw.print(serializer.generateFormXML(form));
                    pw.close();
                }
            } catch (Exception e) {
                log.error("Error serializing forms: ", e);
            }
        }

        return true;
    }

    protected boolean upgrade(long l) {
        return install();
    }

    public FormTemplate[] getFormTemplates() {
        return formTemplates;
    }

    public void setFormTemplates(FormTemplate[] formTemplates) {
        this.formTemplates = formTemplates;
    }
}
