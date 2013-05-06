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
public class FormRenderingServiceImpl implements FormRenderingService, MessageCallback {

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

    @Override
    public FormRenderContextTO launchTest() {

        try {
            InputStreamReader is = new InputStreamReader(this.getClass().getResourceAsStream("test/testInvoice.form"));
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while(read != null) {
                sb.append(read);
                read = br.readLine();
            }

            sb.toString();

            Form form = formSerializationManager.loadFormFromXML(sb.toString());

            Invoice invoice = new Invoice();

            invoice.setName("Ned Stark");
            invoice.setCity("Winterfall");

            Map<String, Object> bindingData = new HashMap<String, Object>();
            bindingData.put("invoice", invoice);

            return startRendering(form, bindingData, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
