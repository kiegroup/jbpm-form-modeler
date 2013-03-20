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
package org.jbpm.formModeler.service.bb.mvc.components;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.RedirectToURLResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.ShowScreenResponse;
import org.apache.commons.lang.StringUtils;

/**
 */
public class ControllerStatus extends BasicFactoryElement {

    public static ControllerStatus lookup() {
        return (ControllerStatus) Factory.lookup("org.jbpm.formModeler.service.mvc.controller.ControllerStatus");
    }

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ControllerStatus.class.getName());

    private Throwable exception;
    //By default, this is the response that will be executed.
    private CommandResponse response;
    private String showPage = "/formModeler/controllerResponse.jsp";
    private String currentComponentPage;
    private String requestURI;
    private StringBuffer consumedRequestURI;
    private CommandRequest request;

    public String getShowPage() {
        return showPage;
    }

    public void setShowPage(String showPage) {
        this.showPage = showPage;
    }

    public void start() throws Exception {
        super.start();
        response = new ShowScreenResponse(showPage);
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public CommandResponse getResponse() {
        return response;
    }

    public void setResponse(CommandResponse response) {
        this.response = response;
    }

    protected StringBuffer getConsumedRequestURI() {
        return consumedRequestURI;
    }

    public void setURIToBeConsumed(String requestURI) {
        this.requestURI = StringUtils.replace(requestURI, "//", "/");
        consumedRequestURI = new StringBuffer();
    }

    public String getURIToBeConsumed() {
        return requestURI;
    }

    public void consumeURIPart(String uriPart) {
        consumedRequestURI.append(uriPart);
    }

    public void compareConsumedUri() {
        String consumedUri = StringUtils.replace(consumedRequestURI.toString(), "//", "/");
        if (consumedUri.endsWith("/"))
            consumedUri = consumedUri.substring(0, consumedUri.length() - 1);
        if (!requestURI.equals(consumedUri)) {
            if (log.isDebugEnabled()) {
                log.debug("Received URI: " + requestURI);
                log.debug("Consumed URI: " + consumedUri);
            }
            if (StringUtils.isEmpty(consumedUri)) {
                log.error("No part of the received uri " + requestURI + " has been consumed. Trying to serve it as good as possible.");
            } else if (requestURI.startsWith(consumedUri)) {
                String uriToForward = requestURI.substring(consumedUri.length());
                setResponse(new RedirectToURLResponse(uriToForward, !uriToForward.startsWith(request.getRequestObject().getContextPath())));
                log.warn("Redirecting to static uri: " + uriToForward);
            } else {
                log.error("Consumed uri " + consumedUri + " is not even part of request uri: " + requestURI +
                        ". Trying to serve it as good as possible.");
            }
        }
    }

    public void setRequest(CommandRequest commandRequest) {
        this.request = commandRequest;
        setURIToBeConsumed(request.getRequestObject().getRequestURI().substring(request.getRequestObject().getContextPath().length()));
    }

    public CommandRequest getRequest() {
        return request;
    }

    public String getCurrentComponentPage() {
        return currentComponentPage;
    }

    public void setCurrentComponentPage(String currentComponentPage) {
        this.currentComponentPage = currentComponentPage;
    }
}
