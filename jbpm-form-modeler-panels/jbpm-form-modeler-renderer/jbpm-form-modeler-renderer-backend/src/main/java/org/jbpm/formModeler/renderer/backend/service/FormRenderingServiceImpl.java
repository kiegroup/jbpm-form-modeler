package org.jbpm.formModeler.renderer.backend.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.api.events.*;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;
import org.jbpm.formModeler.renderer.service.FormRenderingService;
import org.apache.commons.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Service
@ApplicationScoped
@Named ("FormRenderingService")
public class FormRenderingServiceImpl implements FormRenderingService {
    @Inject
    private Log log;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormProcessor formProcessor;

    @Inject
    private Event<FormSubmitFailEvent> formSubmitFailEvent;

    @Inject
    private Event<FormSubmittedEvent> formSubmittedEvent;

    protected Map<String, FormRenderContext> formRenderContextMap = new HashMap<String, FormRenderContext>();

    @Override
    public FormRenderContextTO startRendering(Long formId, Map<String, Object> bindingData) {
        Form form = formManager.getFormById(formId);

        return startRendering(form, bindingData);
    }

    @Override
    public FormRenderContextTO startRendering(Form form, Map<String, Object> bindingData) {
        if (form != null) {

            FormRenderContext ctx = newContext(form, bindingData);

            return ctx.getFormRenderingContextTO();
        }

        return null;
    }

    @Override
    public void persistContext(FormRenderContext ctx) throws Exception {
        if (ctx == null) throw new IllegalArgumentException("Unable to persist null context");
        formProcessor.persist(ctx);
    }

    @Override
    public void persistContext(String ctxUID) throws Exception {
       persistContext(getFormRenderContext(ctxUID));
    }

    @Override
    public void removeContext(String ctxUID) {
        removeContext(getFormRenderContext(ctxUID));
    }

    @Override
    public void removeContext(FormRenderContext context) {
        formProcessor.clear(context);
    }

    @Override
    public FormRenderContext newContext(Form form, Map<String, Object> bindingData) {
        String uid = "formRenderCtx_" + form.getId() + "_" + System.currentTimeMillis();
        FormRenderContext ctx = new FormRenderContext(uid, form, bindingData);
        formRenderContextMap.put(uid, ctx);
        formProcessor.read(ctx.getUID());
        return ctx;
    }

    @Override
    public FormRenderContext getFormRenderContext(String UID) {
        return formRenderContextMap.get(UID);
    }

    @Override
    public void fireContextSubmitError(FormSubmitFailEvent event) {
        if (event != null) formSubmitFailEvent.fire(event);
    }

    @Override
    public void fireContextSubmit(FormSubmittedEvent event) {
        if (event != null) formSubmittedEvent.fire(event);
    }
}
