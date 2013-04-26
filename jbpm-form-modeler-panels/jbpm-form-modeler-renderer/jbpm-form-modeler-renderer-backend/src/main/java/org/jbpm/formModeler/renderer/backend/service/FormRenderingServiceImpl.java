package org.jbpm.formModeler.renderer.backend.service;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.builder.MessageBuildSubject;
import org.jboss.errai.bus.client.api.builder.MessageReplySendable;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.api.util.helpers.RenderHelper;
import org.jbpm.formModeler.renderer.FormRenderContext;
import org.jbpm.formModeler.renderer.FormRenderListener;
import org.jbpm.formModeler.renderer.service.FormRenderingService;
import org.jbpm.formModeler.renderer.validation.FormValidationResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ApplicationScoped
@Named ("FormRenderingService")
public class FormRenderingServiceImpl implements FormRenderingService, MessageCallback {

    @Inject
    private FormManager formManager;

    protected Map<String, FormRenderContext> formRenderContextMap = new HashMap<String, FormRenderContext>();

    @Override
    public FormRenderContext startRendering(Long formId, Map<String, Object> bindingData, FormRenderListener formRenderListener) {
        Form form = formManager.getFormById(formId);

        if (form != null) {
            String uid = "formRenderCtx_" + formId + "_" + System.currentTimeMillis();
            FormRenderContext ctx = new FormRenderContext(uid, formId, bindingData, formRenderListener);
            formRenderContextMap.put(uid, ctx);
            return ctx;
        }

        return null;
    }

    @Override
    public FormRenderContext getFormRenderContext(String UID) {
        return formRenderContextMap.get(UID);
    }

    private Map conversations = new HashMap<String, MessageBuildSubject<MessageReplySendable>>();



    @Override
    public List<FormTO> getAllForms() {
        List<FormTO> result = new ArrayList<FormTO>();
        for (Form form : formManager.getAllForms()) {
            result.add(new FormTO(form.getId(), form.getName()));
        }
        return result;
    }

    @Override
    public void loadForm(Long id, Long formId) {
        RenderHelper helper = getHelper();

        helper.setForm(formManager.getFormById(formId));
        helper.setSessionId(id);

    }

    protected RenderHelper getHelper() {
        RenderHelper helper = (RenderHelper) RpcContext.getHttpSession().getAttribute("RenderHelper");

        if (helper == null) helper = new RenderHelper();

        RpcContext.getHttpSession().setAttribute("RenderHelper", helper);

        return helper;
    }

    public void callback(Message message) {

        String id = (String) message.getParts().get("id");

        if (!StringUtils.isEmpty(id)) {
            conversations.put(id, MessageBuilder.createConversation(message));
        }

        System.out.println(message.getParts().get("message"));
    }




    public void notiyValidation(String id, FormValidationResult result) {
        if (!StringUtils.isEmpty(id)) {
            MessageBuildSubject<MessageReplySendable> conversation = (MessageBuildSubject<MessageReplySendable>) conversations.get(id);

            conversation.subjectProvided()
                    .signalling()
                    .with("message", "Evaluat form!")
                    .with("result", result)
                    .noErrorHandling()
                    .reply();
            conversations.remove(id);
        }
    }
}
