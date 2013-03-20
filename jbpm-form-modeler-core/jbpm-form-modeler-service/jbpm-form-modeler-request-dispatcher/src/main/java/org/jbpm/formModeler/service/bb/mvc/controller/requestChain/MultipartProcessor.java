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

import org.jbpm.formModeler.service.bb.mvc.Framework;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.jbpm.formModeler.service.bb.mvc.components.RedirectionHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.ControllerServletHelper;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestMultipartWrapper;
import org.jbpm.formModeler.service.bb.mvc.controller.SessionTmpDirFactory;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.RedirectToURLResponse;
import org.jbpm.formModeler.service.bb.mvc.taglib.ContextTag;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Handles multipart requests, building wrapper around the request object
 * in order to support files uploading.
 */
public class MultipartProcessor extends RequestChainProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(MultipartProcessor.class.getName());

    private String errorRedirectPage = "/formModeler/fileTooBig.jsp";
    private ControllerServletHelper controllerServletHelper;
    private Framework framework;

    public ControllerServletHelper getControllerServletHelper() {
        return controllerServletHelper;
    }

    public void setControllerServletHelper(ControllerServletHelper controllerServletHelper) {
        this.controllerServletHelper = controllerServletHelper;
    }

    public String getErrorRedirectPage() {
        return errorRedirectPage;
    }

    public void setErrorRedirectPage(String errorRedirectPage) {
        this.errorRedirectPage = errorRedirectPage;
    }

    /**
     * Make required processing of request.
     *
     * @return true if processing must continue, false otherwise.
     */
    protected boolean processRequest() throws Exception {
        /*
        *  Hack for handling multipart requests.
        */
        String contentType = getRequest().getContentType();
        String method = getRequest().getMethod();
        if ("POST".equalsIgnoreCase(method) && contentType != null && contentType.startsWith("multipart") && framework.isMultipartProcessing()) {
            log.debug("Found multipart request. Building wrapper");

            String tmpDir = SessionTmpDirFactory.getTmpDir(getRequest());
            if (log.isDebugEnabled())
                log.debug("Extracting to dir " + tmpDir);

            int maxSize = framework.getMaxPostSize() * 1024;
            if (log.isDebugEnabled()) {
                log.debug("Max post size is : " + maxSize + " bytes");
                log.debug("Framework encoding is: " + framework.getFrameworkEncoding());
            }

            try {
                RequestMultipartWrapper wrap = new RequestMultipartWrapper(getRequest(), tmpDir, maxSize, framework.getFrameworkEncoding());
                log.debug("Multipart request parsed: ");
                log.debug("getting files from request");
                controllerServletHelper.initThreadLocal(wrap, getResponse());
            }
            catch (IOException ioe) {
                log.warn("IOException processing multipart ", ioe);
                log.warn("Invalid " + method + ": URL=" + getRequest().getRequestURL() + ". QueryString=" + getRequest().getQueryString());
                URLMarkupGenerator markupGenerator = (URLMarkupGenerator) Factory.lookup("org.jbpm.formModeler.service.mvc.components.URLMarkupGenerator");
                if (markupGenerator != null) {
                    Map paramsMap = new HashMap();
                    paramsMap.put(RedirectionHandler.PARAM_PAGE_TO_REDIRECT, getErrorRedirectPage());
                    String uri = ContextTag.getContextPath(markupGenerator.getMarkup("org.jbpm.formModeler.service.mvc.components.RedirectionHandler", "redirectToSection", paramsMap), getRequest());
                    uri = StringEscapeUtils.unescapeHtml(uri);
                    getControllerStatus().setResponse(new RedirectToURLResponse(uri, !uri.startsWith(getRequest().getContextPath())));
                }
                return false;
            }
        }
        return true;
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }
}
