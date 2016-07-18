/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.jbpm.formModeler.kie.services.form.ProcessDefinition;
import org.jbpm.formModeler.kie.services.form.FormManagerService;
import org.jbpm.formModeler.kie.services.form.TaskDefinition;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormModelerFormProvider extends AbstractFormProvider {

    protected Logger log = LoggerFactory.getLogger(FormModelerFormProvider.class);

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private FormRenderContentMarshallerManager formRenderContentMarshaller;

    private String formExtension = ".form";

    @Inject
    @Override
    public void setFormManagerService(FormManagerService formManagerService){
        super.setFormManagerService(formManagerService);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String render(String name, ProcessDefinition process, Map<String, Object> renderContext) {
        String templateString = formManagerService.getFormByKey(process.getDeploymentId(), process.getId() + getFormSuffix());

        if (templateString == null || templateString.isEmpty())
            return null;

        return renderProcessForm(process, new ByteArrayInputStream(templateString.getBytes()), renderContext);
    }

    @Override
    public String render(String name, TaskDefinition task, ProcessDefinition process, Map<String, Object> renderContext) {
        if (task == null) return null;

        String lookupName = getTaskFormName( task );

        if ( lookupName == null || lookupName.isEmpty() || !lookupName.endsWith( formExtension )) return null;

        String templateString = formManagerService.getFormByKey(task.getDeploymentId(), lookupName);

        if (templateString != null && !templateString.isEmpty())
            return renderTaskForm(task, new ByteArrayInputStream( templateString.getBytes() ), renderContext);

        return null;
    }

    protected String renderTaskForm(TaskDefinition task, InputStream template, Map<String, Object> renderContext) {
        String result = null;
        try {
            Form form = formSerializationManager.loadFormFromXML(template);

            Map inputs = new HashMap();

            Map outputs;
            if (!task.isOutputIncluded()){
                outputs = new HashMap();
            }
            else {
                outputs = (Map) renderContext.get("outputs");
            }

            Map m = (Map) renderContext.get("inputs");
            if (m != null) inputs.putAll(m);

            inputs.put("task", task);

            // Adding forms to context while forms are'nt available on marshaller classloader
            FormRenderContext context = formRenderContextManager.newContext(form, task.getDeploymentId(), inputs, outputs);
            formRenderContentMarshaller.addContentMarshaller(context.getUID(), (ContentMarshallerContext) renderContext.get("marshallerContext"));

            String status = task.getStatus();
            boolean readonly = !"InProgress".equals(status);
            context.setReadonly(readonly);
            result = context.getUID();

        } catch (Exception e) {
            log.warn("Error rendering form: ", e);
        }

        return result;
    }

    protected String renderProcessForm(ProcessDefinition process, InputStream template, Map<String, Object> renderContext) {
        String result = null;
        try {
            Form form = formSerializationManager.loadFormFromXML(template);

            Map ctx = new HashMap();

            ctx.put("process", process);

            // Adding forms to context while forms are'nt available on marshaller classloader
            FormRenderContext context = formRenderContextManager.newContext(form, process.getDeploymentId(), ctx, new HashMap<String, Object>());
            formRenderContentMarshaller.addContentMarshaller(context.getUID(), (ContentMarshallerContext) renderContext.get("marshallerContext"));

            result = context.getUID();
        } catch (Exception e) {
            log.warn("Error rendering form: ", e);
        }

        return result;
    }

    @Override
    protected String getFormExtension() {
        return formExtension;
    }
}
