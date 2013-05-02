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
package org.jbpm.formModeler.service.bb.mvc.controller;

import org.jbpm.formModeler.service.bb.mvc.controller.requestChain.*;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RequestProcessor {

    public static RequestProcessor lookup() {
        return (RequestProcessor) CDIBeanLocator.getBeanByType(RequestProcessor.class);
    }

    protected List<RequestChainProcessor> requestProcessorChain = new ArrayList<RequestChainProcessor>();

    @PostConstruct
    protected void initChain() {
        requestProcessorChain.add(new ResponseHeadersProcessor());
        requestProcessorChain.add(new MultipartProcessor());
        requestProcessorChain.add(new FreeMemoryProcessor());
        requestProcessorChain.add(new SessionInitializer());
        requestProcessorChain.add(new BeanDispatcher());
    }

    public void run() throws Exception {
        for (RequestChainProcessor processor : requestProcessorChain) {
            RequestContext reqCtx = RequestContext.getCurrentContext();
            if (processor.processRequest(reqCtx.getRequest()) == false) {
                // Stop in case the processor has explicitly stopped the chain's processing.
                return;
            }
        }
    }
}
