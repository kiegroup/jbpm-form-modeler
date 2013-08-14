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

import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.jbpm.formModeler.service.bb.mvc.components.RedirectionHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.*;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.RedirectToURLResponse;
import org.jbpm.formModeler.service.bb.mvc.taglib.ContextTag;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles multipart requests, building wrapper around the request object
 * in order to support files uploading.
 */
public class MultipartProcessor implements RequestChainProcessor {

    private static transient Logger log = LoggerFactory.getLogger(MultipartProcessor.class);

    private String errorRedirectPage = "/formModeler/fileTooBig.jsp";

    public boolean processRequest(CommandRequest request) throws Exception {
        HTTPSettings httpSettings = HTTPSettings.lookup();
        HttpServletRequest httpReq = request.getRequestObject();
        HttpServletResponse httpRes = request.getResponseObject();
        String contentType = httpReq.getContentType();
        String method = httpReq.getMethod();
        if ("POST".equalsIgnoreCase(method) && contentType != null && contentType.startsWith("multipart") && httpSettings.isMultipartProcessing()) {
            log.debug("Found multipart request. Building wrapper");

            String tmpDir = SessionTmpDirFactory.getTmpDir(httpReq);
            if (log.isDebugEnabled())
                log.debug("Extracting to dir " + tmpDir);

            int maxSize = httpSettings.getMaxPostSize() * 1024;
            if (log.isDebugEnabled()) {
                log.debug("Max post size is : " + maxSize + " bytes");
                log.debug("Framework encoding is: " + httpSettings.getEncoding());
            }

            try {
                RequestMultipartWrapper wrap = new RequestMultipartWrapper(httpReq, tmpDir, maxSize, httpSettings.getEncoding());
                log.debug("Multipart request parsed: ");
                log.debug("getting files from request");
                ControllerServletHelper.lookup().initThreadLocal(wrap, httpRes);
            }
            catch (IOException ioe) {
                log.warn("IOException processing multipart ", ioe);
                log.warn("Invalid " + method + ": URL=" + httpReq.getRequestURL() + ". QueryString=" + httpReq.getQueryString());
                URLMarkupGenerator markupGenerator = URLMarkupGenerator.lookup();
                if (markupGenerator != null) {
                    Map paramsMap = new HashMap();
                    paramsMap.put(RedirectionHandler.PARAM_PAGE_TO_REDIRECT, errorRedirectPage);
                    String uri = ContextTag.getContextPath(markupGenerator.getMarkup("org.jbpm.formModeler.service.mvc.components.RedirectionHandler", "redirectToSection", paramsMap), httpReq);
                    uri = StringEscapeUtils.unescapeHtml(uri);
                    ControllerStatus.lookup().setResponse(new RedirectToURLResponse(uri, !uri.startsWith(httpReq.getContextPath())));
                }
                return false;
            }
        }
        return true;
    }
}
