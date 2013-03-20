package org.jboss.modeler.form.backend;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.builder.MessageBuildSubject;
import org.jboss.errai.bus.client.api.builder.MessageReplySendable;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.modeler.form.client.validation.FormValidationResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Service
@ApplicationScoped
@Named ("FormService")
public class FormService implements MessageCallback {

    private Map conversations = new HashMap<String, MessageBuildSubject<MessageReplySendable>>();

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
