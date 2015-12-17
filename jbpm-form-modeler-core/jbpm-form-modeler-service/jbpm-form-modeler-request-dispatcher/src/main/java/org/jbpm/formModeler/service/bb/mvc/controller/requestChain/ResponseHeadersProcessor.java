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
package org.jbpm.formModeler.service.bb.mvc.controller.requestChain;

import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.HTTPSettings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResponseHeadersProcessor implements RequestChainProcessor {

    private boolean useRefreshHeader = false;
    private String responseContentType = "text/html";

    public boolean processRequest(CommandRequest request) {
        HttpServletResponse response = request.getResponseObject();
        if (responseContentType != null && !"".equals(responseContentType)) {
            response.setContentType(responseContentType);
            response.setHeader("Content-Type", responseContentType + "; charset=" + HTTPSettings.lookup().getEncoding());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        response.setHeader("Expires", "Mon, 06 Jan 2003 21:29:02 GMT");
        response.setHeader("Last-Modified", sdf.format(new Date()) + " GMT");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        if (useRefreshHeader) {
            response.setHeader("Refresh", String.valueOf(request.getSessionObject().getMaxInactiveInterval() + 61));
        }
        return true;
    }
}
