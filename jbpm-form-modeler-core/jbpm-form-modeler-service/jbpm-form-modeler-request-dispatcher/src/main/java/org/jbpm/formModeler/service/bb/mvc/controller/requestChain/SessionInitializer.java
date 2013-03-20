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

import org.jbpm.formModeler.service.bb.mvc.controller.ControllerException;
import org.jbpm.formModeler.service.bb.mvc.controller.ControllerListener;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.ShowScreenResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.RedirectToURLResponse;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 *
 */
public class SessionInitializer extends RequestChainProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SessionInitializer.class.getName());

    private ControllerListener[] listeners;
    private String expiredUrl = "/expired.jsp";
    private String exceededUrl = "/exceeded.jsp";
    private boolean performExpiredRecovery = true;

    public boolean isPerformExpiredRecovery() {
        return performExpiredRecovery;
    }

    public void setPerformExpiredRecovery(boolean performExpiredRecovery) {
        this.performExpiredRecovery = performExpiredRecovery;
    }

    public String getExpiredUrl() {
        return expiredUrl;
    }

    public void setExpiredUrl(String expiredUrl) {
        this.expiredUrl = expiredUrl;
    }

    public String getExceededUrl() {
        return exceededUrl;
    }

    public void setExceededUrl(String exceededUrl) {
        this.exceededUrl = exceededUrl;
    }

    public ControllerListener[] getListeners() {
        return listeners;
    }

    public void setListeners(ControllerListener[] listeners) {
        this.listeners = listeners;
    }


    /**
     * Attributes to store in session
     */
    private final static String SESSION_ATTRIBUTE_INITIALIZED = "controller.initialized";
    /**
     * Attributes to store in session
     */
    private final static String SESSION_ATTRIBUTE_BIND_LISTENER = "controller.bind.listener";

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected boolean processRequest() throws Exception {
        //Retrieve session
        HttpSession session = getRequest().getSession(true);
        boolean isNewSession = !"true".equals(session.getAttribute(SESSION_ATTRIBUTE_INITIALIZED));

        if (isNewSession) initSession();

        //Check session expiration
        if (getRequest().getRequestedSessionId() != null && !getRequest().getRequestedSessionId().equals(session.getId())) {
            return handleExpiration();
        }

        //Verify session integrity
        if (!verifySession(session)) {
            throw new ControllerException("Session verification failed.");
        }

        return true;
    }

    /**
     * Called when a new session is created. Its default behaviour is notifying
     * the event to all the listeners registered.
     */
    protected void initSession() {
        log.debug("New session created. Firing event");
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ControllerListener listener = listeners[i];
                listener.initSession(getRequest(), getResponse());
            }
        }
        // Store a HttpBindingListener object to detect session expiration
        getRequest().getSession().setAttribute(SESSION_ATTRIBUTE_BIND_LISTENER, new HttpSessionBindingListener() {
            public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
            }

            public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
                if (listeners != null) {
                    for (int i = 0; i < listeners.length; i++) {
                        ControllerListener listener = listeners[i];
                        listener.expireSession(httpSessionBindingEvent.getSession());
                    }
                }
            }
        });
        getRequest().getSession().setAttribute(SESSION_ATTRIBUTE_INITIALIZED, "true");
    }

    /**
     * Check that current session has all the required parameters, and issue warnings if not.
     *
     * @param session
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
     *
     * @return false to halt processing
     */
    protected boolean handleExpiration() {
        log.debug("Session expiration detected.");
        if (isPerformExpiredRecovery()) {
            //Forward to the same uri, ignoring the request parameters
            handleExpirationRecovery();
        } else {
            if (expiredUrl != null) {
                getControllerStatus().setResponse(new ShowScreenResponse(expiredUrl));
            } else {
                try {
                    getResponse().sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                } catch (java.io.IOException e) {  // Too many errors => go to hell!!
                    log.error("I can't handle so many errors in a nice way", e);
                }
            }
        }
        getControllerStatus().consumeURIPart(getControllerStatus().getURIToBeConsumed());
        return false;
    }

    protected void handleExpirationRecovery() {
        getControllerStatus().setResponse(new RedirectToURLResponse(getExpirationRecoveryURL()));
    }

    protected String getExpirationRecoveryURL() {
        return getRequest().getRequestURI();
    }

    /**
     * Handles number of sessions exceeded
     */
    protected void handleSessionsExceeded() {
        log.debug("Sessions number exceeded.");
        if (exceededUrl != null) {

            try {
                log.debug("Redirecting to " + exceededUrl);
                getResponse().sendRedirect(getRequest().getContextPath() + "/" + exceededUrl);
            } catch (Exception ex) {
                log.error("Error redirecting", ex);
                try {
                    getResponse().getWriter().print("Session Count Exceeded");
                } catch (java.io.IOException e) { // Too many errors => go to hell
                    log.error("Error executing error response => too much for me", e);
                }
            }
        } else {
            try {
                getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (java.io.IOException e) {  // Too many errors => go to hell!!
                log.error("I can't handle so many errors in a nice way", e);
            }
        }
    }

}
