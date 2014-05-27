package org.jbpm.formModeler.kie.services.form.provider;

import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.slf4j.Logger;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.form.FormProvider;
import org.kie.api.task.model.Task;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.kie.internal.task.api.model.InternalTask;
import org.slf4j.LoggerFactory;

public class FormModelerFormProvider implements FormProvider {

    protected Logger log = LoggerFactory.getLogger(FormModelerFormProvider.class);

    @Inject
    private RuntimeDataService dataService;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private FormRenderContentMarshallerManager formRenderContentMarshaller;

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String render(String name, ProcessAssetDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        if (process.getForms().containsKey(process.getId())) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId()).getBytes());
        } else if (process.getForms().containsKey(process.getId() + "-taskform.form")) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId() + "-taskform.form").getBytes());
        }

        if (template == null) return null;

        return renderProcessForm(process, template, renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessAssetDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        if(task != null && process != null){
            String lookupName = "";
            String formName = ((InternalTask)task).getFormName();
            if(formName != null && !formName.equals("")){
                lookupName = formName;
            }else{
                lookupName = task.getNames().get(0).getText();
            }
            if (process.getForms().containsKey(lookupName)) {
                template = new ByteArrayInputStream(process.getForms().get(lookupName).getBytes());
            } else if (process.getForms().containsKey(lookupName.replace(" ", "")+ "-taskform.form")) {
                template = new ByteArrayInputStream(process.getForms().get(lookupName.replace(" ", "") + "-taskform.form").getBytes());
            }
        }

        if (template == null) return null;

        return renderTaskForm(task, template, renderContext);
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
            FormRenderContext context = formRenderContextManager.newContext(form, inputs, outputs, buildContextForms(task));
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

    protected String renderProcessForm(ProcessAssetDesc process, InputStream template, Map<String, Object> renderContext) {
        String result = null;
        try {
            Form form = formSerializationManager.loadFormFromXML(template);

            Map ctx = new HashMap();

            ctx.put("process", process);

            // Adding forms to context while forms are'nt available on marshaller classloader
            FormRenderContext context = formRenderContextManager.newContext(form, ctx, new HashMap<String, Object>(), buildContextForms(process));
            formRenderContentMarshaller.addContentMarshaller(context.getUID(), (ContentMarshallerContext) renderContext.get("marshallerContext"));

            result = context.getUID();
        } catch (Exception e) {
            log.warn("Error rendering form: ", e);
        }

        return result;
    }

    protected Map<String, Object> buildContextForms(Task task) {
        ProcessAssetDesc processDesc = dataService.getProcessesByDeploymentIdProcessId(task.getTaskData().getDeploymentId(), task.getTaskData().getProcessId());
        return buildContextForms(processDesc);
    }

    protected Map<String, Object> buildContextForms(ProcessAssetDesc process) {
        Map<String, String> forms = process.getForms();

        Map<String, Object> ctxForms = new HashMap<String, Object>();


        for (Iterator it = forms.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (!key.endsWith(".form")) continue;
            String value = forms.get(key);
            ctxForms.put(key, value);
        }
        return ctxForms;
    }
}
