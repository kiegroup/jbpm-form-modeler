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

import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.jbpm.formModeler.service.bb.mvc.components.handling.FactoryRequestHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestContext;

/**
 *
 */
public class FactoryActionRequestExecutor extends RequestChainProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FactoryActionRequestExecutor.class.getName());

    private FactoryRequestHandler factoryRequestHandler;

    public FactoryRequestHandler getFactoryRequestHandler() {
        return factoryRequestHandler;
    }

    public void setFactoryRequestHandler(FactoryRequestHandler factoryRequestHandler) {
        this.factoryRequestHandler = factoryRequestHandler;
    }

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected boolean processRequest() throws Exception {
        final CommandRequest cmdReq = RequestContext.getCurrentContext().getRequest();
        CommandResponse response = factoryRequestHandler.handleRequest(cmdReq);
        if (getRequest().getServletPath().indexOf("/" + URLMarkupGenerator.COMMAND_RUNNER) != -1) {
            getControllerStatus().consumeURIPart(getControllerStatus().getURIToBeConsumed());
        }
        if (response != null) {
            getControllerStatus().setResponse(response);
        }
        return true;
    }

}
