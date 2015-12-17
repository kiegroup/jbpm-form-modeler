/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * Sends a error code response as a error
 */
public class SendErrorResponse implements CommandResponse {

    /**
     * Error code
     */
    private int error = -1;

    /**
     * Constructor
     */
    public SendErrorResponse(int errorCode) {
        super();

        this.error = errorCode;
    }

    /**
     * Constructor
     */
    public SendErrorResponse(String errorCode) {
        super();

        this.error = Integer.parseInt(errorCode);
    }

    /**
     * Executes this response object. It typically will be one of the response types
     * that are provided.
     *
     * @param cmdReq Object encapsulating the request information.
     * @return boolean if the execution has been successfuly executed, false otherwise.
     */
    public boolean execute(CommandRequest cmdReq) throws Exception {
        HttpServletResponse res = cmdReq.getResponseObject();

        res.sendError(error);

        return true;
    }

    /**
     * toString implementation
     */
    public String toString() {
        return "SendErrorResponse " + error;
    }
}