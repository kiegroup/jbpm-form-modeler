/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.kie.services.form.provider;

import org.jbpm.formModeler.kie.services.form.FormManagerService;
import org.jbpm.formModeler.kie.services.form.FormProvider;
import org.jbpm.formModeler.kie.services.form.TaskDefinition;


public abstract class AbstractFormProvider implements FormProvider {

    protected FormManagerService formManagerService;

    public void setFormManagerService(FormManagerService formManagerService){
        this.formManagerService = formManagerService;
    }

    protected String getFormSuffix() {
        return "-taskform" + getFormExtension();
    }

    protected String getTaskFormName(TaskDefinition task) {
        String formName = task.getFormName();
        if (formName != null && !formName.equals("")) {
            // if the form name has extension it
            if ( formName.endsWith( getFormExtension() ) ) return formName;
            return formName + getFormSuffix();
        } else {
            if (task.getName() != null && !task.getName().isEmpty()) {
                formName = task.getName();
                if (formName != null) return formName.replace(" ", "") + getFormSuffix();
            }
        }
        return null;
    }

    protected String getFormExtension() {
        return "";
    }
}
