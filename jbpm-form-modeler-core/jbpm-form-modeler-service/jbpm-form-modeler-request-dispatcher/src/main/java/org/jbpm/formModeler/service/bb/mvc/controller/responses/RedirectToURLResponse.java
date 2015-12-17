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
import org.jbpm.formModeler.service.bb.mvc.taglib.ContextTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServletResponse;

/**
 * Sends a redirect to an arbitrary URL.
 */
public class RedirectToURLResponse implements CommandResponse {

    /**
     * URL
     */
    private String URL = null;

    private boolean isRelative = false;

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(RedirectToURLResponse.class);


    public RedirectToURLResponse(String newURL, boolean relative) {
        super();
        this.URL = newURL;
        this.isRelative = relative;
    }

    public RedirectToURLResponse(String newURL) {
        this(newURL, false);
    }

    /**
     * Execute
     */
    public boolean execute(CommandRequest cmdReq) throws Exception {
        if (log.isDebugEnabled()) log.debug("RedirectToURLResponse: " + getURL());
        HttpServletResponse res = cmdReq.getResponseObject();
        if (res != null) {
            res.sendRedirect(normalize(getURL(), cmdReq, isRelative()));
            return true;
        } else {
            log.error("Response object is null");
            return false;
        }
    }

    protected String normalize(String url, CommandRequest cmdReq, boolean relative) {
        if (relative) {
            return ContextTag.getContextPath(url, cmdReq.getRequestObject());
        } else
            return url;
    }

    /**
     * Returns the URL
     */
    public String getURL() {
        return URL;
    }

    public boolean isRelative() {
        return isRelative;
    }

    /**
     * toString implementation
     */
    public String toString() {
        return "RedirectToURLResponse -> " + getURL();
    }
}