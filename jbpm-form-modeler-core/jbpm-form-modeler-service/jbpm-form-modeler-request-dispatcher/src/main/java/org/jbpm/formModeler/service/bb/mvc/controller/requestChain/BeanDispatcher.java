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

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.components.FactoryURL;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;


/**
 * Dispatch the request to the specified UIBeanHandler.
 */
public class BeanDispatcher implements RequestChainProcessor {

    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BeanDispatcher.class);

    public boolean processRequest(CommandRequest request) throws Exception {
        CommandResponse response = handleRequest(request);
        if (request.getRequestObject().getServletPath().indexOf("/" + URLMarkupGenerator.COMMAND_RUNNER) != -1) {
            ControllerStatus.lookup().consumeURIPart(ControllerStatus.lookup().getURIToBeConsumed());
        }

        ControllerStatus.lookup().setResponse(response);

        return true;
    }

    public CommandResponse handleRequest(CommandRequest request) throws Exception {
        String beanName = request.getRequestObject().getParameter(FactoryURL.PARAMETER_BEAN);
        String beanAction = request.getRequestObject().getParameter(FactoryURL.PARAMETER_PROPERTY);
        if (!StringUtils.isEmpty(beanName) && !StringUtils.isEmpty(beanAction)) {
            try {
                BeanHandler bean = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(beanName);
                if (bean != null) {
                    return bean.handle(request, beanAction);
                } else {
                    log.error("Unexistant bean specified for request handling: " + beanName);
                }
            } catch (ClassCastException cce) {
                log.error("Bean " + beanName + " is not a BeanHandler.");
            } catch (Exception e){
                log.error("Exception ",e);
            }
        }
        return null;
    }
}
