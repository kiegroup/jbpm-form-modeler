package org.jbpm.formModeler.kie.services.form.provider;

import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.jbpm.kie.services.impl.FormManagerService;
import org.jbpm.kie.services.impl.form.provider.AbstractFormProvider;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.slf4j.Logger;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.kie.api.task.model.Task;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.kie.internal.task.api.model.InternalTask;
import org.slf4j.LoggerFactory;

public class FormModelerFormProvider extends AbstractFormProvider {

    protected Logger log = LoggerFactory.getLogger(FormModelerFormProvider.class);

    @Inject
    private RuntimeDataService dataService;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private FormRenderContentMarshallerManager formRenderContentMarshaller;

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
        String templateString = formManagerService.getFormByKey(process.getDeploymentId(), process.getId() + "-taskform.form");

        if (templateString == null || templateString.isEmpty())
            return null;

        return renderProcessForm(process, new ByteArrayInputStream(templateString.getBytes()), renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessDefinition process, Map<String, Object> renderContext) {

        if(task != null && process != null){
            String lookupName = "";
            String formName = ((InternalTask)task).getFormName();
            if(formName != null && !formName.equals("")){
                lookupName = formName;
            }else{
                lookupName = task.getNames().get(0).getText();
            }

            String templateString = formManagerService.getFormByKey(process.getDeploymentId(), lookupName.replace( " ", "" )+ "-taskform.form");

            if (templateString != null && !templateString.isEmpty())
                return renderTaskForm(task, new ByteArrayInputStream( templateString.getBytes() ), renderContext);
        }

        return null;
    }

    protected String renderTaskForm(Task task, InputStream template, Map<String, Object> renderContext) {
        String result = null;
        try {
            Form form = formSerializationManager.loadFormFromXML(template);

            Map inputs = new HashMap();

            Map outputs;
            if (task.getTaskData().getOutputContentId() == -1) outputs = new HashMap();
            else outputs = (Map) renderContext.get("outputs");

            Map m = (Map) renderContext.get("inputs");
            if (m != null) inputs.putAll(m);

            inputs.put("task", task);

            // Adding forms to context while forms are'nt available on marshaller classloader
            FormRenderContext context = formRenderContextManager.newContext(form, task.getTaskData().getDeploymentId(), inputs, outputs);
            formRenderContentMarshaller.addContentMarshaller(context.getUID(), (ContentMarshallerContext) renderContext.get("marshallerContext"));

            String status = task.getTaskData().getStatus().name();
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
}
