/*
 * Copyright 2014 JBoss by Red Hat.
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

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.formModeler.kie.services.form.FormManagerService;
import org.jbpm.formModeler.kie.services.form.ProcessDefinition;
import org.jbpm.formModeler.kie.services.form.TaskDefinition;

public class InMemoryFormProvider extends FreemakerFormProvider {

    private static final String DEFAULT_PROCESS = "DefaultProcess";
    private static final String DEFAULT_TASK = "DefaultTask";

    @Override
    public String render(String name, ProcessDefinition process, Map<String, Object> renderContext) {

        String templateString = formManagerService.getFormByKey(process.getDeploymentId(), process.getId());
        if (templateString == null) {
            templateString = formManagerService.getFormByKey(process.getDeploymentId(), process.getId() + getFormSuffix());
        }

        if (templateString == null || templateString.isEmpty()) {
            return null;
        } else {
            return render(name, new ByteArrayInputStream(templateString.getBytes()), renderContext);
        }
    }

    @Override
    public String render(String name, TaskDefinition task, ProcessDefinition process, Map<String, Object> renderContext) {
        if (task == null) return null;

        String lookupName = getTaskFormName( task );

        if ( lookupName == null || lookupName.isEmpty()) return null;

        String templateString = formManagerService.getFormByKey(task.getDeploymentId(), lookupName);

        if (templateString == null || templateString.isEmpty()) {
            return null;
        } else {
            return render(name, new ByteArrayInputStream(templateString.getBytes()), renderContext);
        }
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    protected String getFormExtension() {
        return ".ftl";
    }

    @Inject
    @Override
    public void setFormManagerService(FormManagerService formManagerService){
        super.setFormManagerService(formManagerService);
    }
}
