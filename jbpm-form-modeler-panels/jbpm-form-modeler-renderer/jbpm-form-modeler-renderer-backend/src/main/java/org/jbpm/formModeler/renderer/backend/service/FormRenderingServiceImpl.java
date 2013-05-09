package org.jbpm.formModeler.renderer.backend.service;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.builder.MessageBuildSubject;
import org.jboss.errai.bus.client.api.builder.MessageReplySendable;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jbpm.formModeler.api.Invoice;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.util.helpers.RenderHelper;
import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;
import org.jbpm.formModeler.api.processing.FormRenderListener;
import org.jbpm.formModeler.renderer.service.FormRenderingService;
import org.jbpm.formModeler.renderer.validation.FormValidationResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ApplicationScoped
@Named ("FormRenderingService")
public class FormRenderingServiceImpl implements FormRenderingService {

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormProcessor formProcessor;

    protected Map<String, FormRenderContext> formRenderContextMap = new HashMap<String, FormRenderContext>();

    @Override
    public FormRenderContextTO startRendering(Long formId, Map<String, Object> bindingData, FormRenderListener formRenderListener) {
        Form form = formManager.getFormById(formId);

        return startRendering(form, bindingData, formRenderListener);
    }

    public FormRenderContextTO startRendering(Form form, Map<String, Object> bindingData, FormRenderListener formRenderListener) {
        if (form != null) {

            FormRenderContext ctx = newContext(form, bindingData, formRenderListener);

            return ctx.getFormRenderingContextTO();
        }

        return null;
    }

    @Override
    public FormRenderContext newContext(Form form, Map<String, Object> bindingData, FormRenderListener formRenderListener) {
        String uid = "formRenderCtx_" + form.getId() + "_" + System.currentTimeMillis();
        FormRenderContext ctx = new FormRenderContext(uid, form, bindingData, formRenderListener);
        formRenderContextMap.put(uid, ctx);
        formProcessor.read(ctx.getUID());
        return ctx;
    }

    @Override
    public FormRenderContext getFormRenderContext(String UID) {
        return formRenderContextMap.get(UID);
    }
}
