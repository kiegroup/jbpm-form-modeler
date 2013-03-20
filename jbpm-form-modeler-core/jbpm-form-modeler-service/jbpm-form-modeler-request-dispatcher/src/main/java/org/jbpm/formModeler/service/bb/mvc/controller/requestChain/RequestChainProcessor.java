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
package org.jbpm.formModeler.service.bb.mvc.controller.requestChain;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Inspired on the pattern chain-of-responsability
 */
public abstract class RequestChainProcessor extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(RequestChainProcessor.class.getName());

    private RequestChainProcessor nextStep;

    public ControllerStatus getControllerStatus() {
        return (ControllerStatus) Factory.lookup("org.jbpm.formModeler.service.mvc.controller.ControllerStatus");
    }

    public RequestChainProcessor getNextStep() {
        return nextStep;
    }

    public void setNextStep(RequestChainProcessor nextStep) {
        this.nextStep = nextStep;
    }

    public HttpServletRequest getRequest() {
        return RequestContext.getCurrentContext().getRequest().getRequestObject();
    }

    public HttpServletResponse getResponse() {
        return RequestContext.getCurrentContext().getRequest().getResponseObject();
    }

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected abstract boolean processRequest() throws Exception;

    public final void doRequestProcessing() throws Exception {
        boolean continueProcessing = processRequest();
        if (continueProcessing && nextStep != null) {
            nextStep.doRequestProcessing();
        }
    }
}
