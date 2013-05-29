package org.jbpm.formModeler.kie.services.form.provider;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.kie.services.impl.form.FormProvider;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.task.model.Task;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class FormModelerFormProvider implements FormProvider {
    @Inject
    protected Log log;

    @Inject
    FormSerializationManager formSerializationManager;

    @Inject
    FormRenderContextManager formRenderContextManager;

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String render(String name, ProcessDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        if (process.getForms().containsKey(process.getId())) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId()).getBytes());
        } else if (process.getForms().containsKey(process.getId() + "-taskform.form")) {
            template = new ByteArrayInputStream(process.getForms().get(process.getId() + "-taskform.form").getBytes());
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessDesc process, Map<String, Object> renderContext) {
        InputStream template = null;
        if(task != null && process != null){
            String taskName = task.getNames().get(0).getText();
            if (process.getForms().containsKey(taskName)) {
                template = new ByteArrayInputStream(process.getForms().get(taskName).getBytes());
            } else if (process.getForms().containsKey(taskName.replace(" ", "")+ "-taskform.form")) {
                template = new ByteArrayInputStream(process.getForms().get(taskName.replace(" ", "") + "-taskform.form").getBytes());
            }
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    protected String render(String name, InputStream template, Map<String, Object> renderContext) {
        String result = null;
        try {
            Form form = formSerializationManager.loadFormFromXML(template);

            result = formRenderContextManager.newContext(form, renderContext).getUID();

        } catch (Exception e) {
            log.warn("Error rendering form: ", e);
        }

        return result;
    }
}
