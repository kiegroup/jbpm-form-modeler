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

import java.util.Map;

import org.jbpm.formModeler.kie.services.form.ProcessDefinition;
import org.jbpm.formModeler.kie.services.form.TaskDefinition;


public class ClasspathFormProvider extends FreemakerFormProvider {
    @Override
    public String render(String name, ProcessDefinition process, Map<String, Object> renderContext) {
        return render(name, this.getClass().getResourceAsStream("/forms/DefaultProcess.ftl"), renderContext);
    }

    @Override
    public String render(String name, TaskDefinition task, ProcessDefinition process, Map<String, Object> renderContext) {
        return render(name, this.getClass().getResourceAsStream("/forms/DefaultTask.ftl"), renderContext);
    }

    @Override
    public int getPriority() {
        return 1000;
    }

}