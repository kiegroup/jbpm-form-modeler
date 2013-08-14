/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.service.bb.mvc.components.handling;

import org.slf4j.Logger;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ResourceBundle;

@Named("MessagesComponentFormatter")
public class MessagesComponentFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(MessagesComponentFormatter.class);

    @Inject @Config("5")
    private int maxVisibleErrors;

    @Inject @Config("/formModeler/components/messages/images/32x32/info.gif")
    private String messagesImg;

    @Inject @Config("/formModeler/components/messages/images/32x32/warning.gif")
    private String warningsImg;

    @Inject @Config("/formModeler/components/messages/images/32x32/error.gif")
    private String errorsImg;

    @Inject @Config("5")
    private String classForMessages;

    @Inject @Config("skn-error")
    private String classForWarnings;

    @Inject @Config("skn-error")
    private String classForErrors;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        MessagesComponentHandler messagesComponentHandler = MessagesComponentHandler.lookup();
        if (messagesComponentHandler.getErrorsToDisplay() != null && messagesComponentHandler.getErrorsToDisplay().size() > 0) {
            renderMessages(messagesComponentHandler.getErrorsToDisplay(), messagesComponentHandler.getErrorsParameters(), errorsImg, classForErrors);
        } else if (messagesComponentHandler.getWarningsToDisplay() != null && messagesComponentHandler.getWarningsToDisplay().size() > 0) {
            renderMessages(messagesComponentHandler.getWarningsToDisplay(), messagesComponentHandler.getWarningsParameters(), warningsImg, classForWarnings);
        } else if (messagesComponentHandler.getMessagesToDisplay() != null && messagesComponentHandler.getMessagesToDisplay().size() > 0) {
            renderMessages(messagesComponentHandler.getMessagesToDisplay(), messagesComponentHandler.getMessagesParameters(), messagesImg, classForMessages);
        }
    }

    protected void renderMessages(List messages, List params, String img, String className) {
        MessagesComponentHandler messagesComponentHandler = MessagesComponentHandler.lookup();
        while (messages.size() > params.size()) {
            params.add(null);
        }
        long id = System.currentTimeMillis();
        boolean maxRised = false;

        setAttribute("image", img);
        setAttribute("bundle", messagesComponentHandler.getI18nBundle());
        renderFragment("outputStart");

        renderFragment("outputVisibleMessagesStart");

        for (int i = 0; i < messages.size(); i++) {
            if (i == maxVisibleErrors) {
                renderFragment("outputMessagesEnd");
                renderFragment("outputNewLine");
                setAttribute("id", id);
                renderFragment("outputHiddenMessagesStart");
                maxRised = true;
            }
            setAttribute("bundle", messagesComponentHandler.getI18nBundle());
            setAttribute("msg", messages.get(i));
            setAttribute("params", params.get(i));
            setAttribute("className", className);
            renderFragment("outputMessage");
        }
        renderFragment("outputMessagesEnd");
        if (maxRised) {
            renderFragment("outputNewLine");
            setAttribute("id", id);
            renderFragment("outputDisplayLinks");
        }
        renderFragment("outputEnd");
        if (messagesComponentHandler.isClearAfterRender()) messagesComponentHandler.clearAll();
    }

    protected String localizeMessage(String message) {
        MessagesComponentHandler messagesComponentHandler = MessagesComponentHandler.lookup();
        try {
            if (messagesComponentHandler.getI18nBundle() != null) {
                ResourceBundle bundle = ResourceBundle.getBundle(messagesComponentHandler.getI18nBundle(), getLocale());
                message = bundle.getString(message);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("Error trying to get message '" + message + "' from bundle '" + messagesComponentHandler.getI18nBundle());
        }
        return message;
    }
}
