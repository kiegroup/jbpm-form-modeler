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
package org.jbpm.formModeler.service.bb.mvc.controller.responses;

import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;

/**
 */
public class ShowScreenResponse implements CommandResponse {
    private static transient Logger log = LoggerFactory.getLogger(ShowScreenResponse.class.getName());

    private String jsp;

    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    public ShowScreenResponse(String jsp) {
        this.jsp = jsp;
    }

    public boolean execute(CommandRequest cmdReq) throws Exception {
        if (log.isDebugEnabled()) log.debug("ShowScreenResponse: " + jsp);                                        
        RequestDispatcher rd = cmdReq.getRequestObject().getRequestDispatcher(jsp);
        rd.include(cmdReq.getRequestObject(), cmdReq.getResponseObject());
        return true;
    }
}
