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

import org.slf4j.Logger;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.RedirectToURLResponse;
import org.slf4j.LoggerFactory;

import javax.servlet.http.*;

/**
 * Takes care of initializing new sessions.
 */
public class SessionInitializer implements RequestChainProcessor {

    private static transient Logger log =  LoggerFactory.getLogger(SessionInitializer.class);

    private final static String SESSION_ATTRIBUTE_INITIALIZED = "controller.initialized";

    public boolean processRequest(CommandRequest request) throws Exception {
        // Retrieve session
        HttpServletRequest httpReq = request.getRequestObject();
        HttpSession session = httpReq.getSession(true);
        boolean isNewSession = !"true".equals(session.getAttribute(SESSION_ATTRIBUTE_INITIALIZED));

        // Catch new sessions.
        if (isNewSession) {
            initSession(httpReq);
        }

        // Check session expiration
        if (httpReq.getRequestedSessionId() != null && !httpReq.getRequestedSessionId().equals(session.getId())) {
            handleExpiration(httpReq);
            return false;
        }

        // Verify session integrity
        if (!verifySession(session)) {
            throw new Exception("Session verification failed.");
        }

        return true;
    }

    /**
     * Called when a new session is created.
     */
    protected void initSession(HttpServletRequest httpReq) {
        httpReq.getSession().setAttribute(SESSION_ATTRIBUTE_INITIALIZED, "true");
    }

    /**
     * Check that current session has all the required parameters, and issue warnings if not.
     */
    protected boolean verifySession(HttpSession session) {
        boolean error = false;
        Object initialized = session.getAttribute(SESSION_ATTRIBUTE_INITIALIZED);
        if (!"true".equals(initialized)) {
            log.error("Current session seems to be not initialized.");
            error = true;
        }
        return !error;
    }

    /**
     * Handles expiration of session
     */
    protected void handleExpiration(HttpServletRequest httpReq) {
        log.debug("Session expiration detected.");
        ControllerStatus controllerStatus = ControllerStatus.lookup();

        // Forward to the same URI, ignoring the request parameters.
        controllerStatus.setResponse(new RedirectToURLResponse(httpReq.getRequestURI()));
        controllerStatus.consumeURIPart(controllerStatus.getURIToBeConsumed());
    }
}
