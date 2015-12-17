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
package org.jbpm.formModeler.service.bb.mvc.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.formModeler.service.Application;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.ShowCurrentScreenResponse;
import org.jbpm.formModeler.service.error.ErrorManager;
import org.jbpm.formModeler.service.error.ErrorReport;
import org.jbpm.formModeler.service.error.ErrorReportHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Application end point for HTTP requests. It provides the following features:<ul>
 * <li> Perform application initialization and ending.
 * <li> Analyze the requests received and dispatch to the proper execution method.</ul>
 */
public class ControllerServlet extends HttpServlet {

    private static transient Logger log = LoggerFactory.getLogger(ControllerServlet.class);

    public final static String INIT_PARAM_CFG_DIR = "base.cfg.dir";
    public final static String INIT_PARAM_APP_DIR = "base.app.dir";

    private static Application theApp;
    private static boolean initSuccess = true;
    private static Throwable initException;

    protected void initError() {
        // Write some data to file, allowing external checking of what went wrong.
        File outputFile = new File(Application.lookup().getBaseAppDirectory() + "/ControllerError.txt");
        FileWriter writer = null;
        try {
            StringWriter sw = new StringWriter();
            initException.printStackTrace(new PrintWriter(sw));
            writer = new FileWriter(outputFile);
            writer.write(initException.getMessage() + "\n" + sw.toString());
            outputFile.deleteOnExit();
            sw.close();
        } catch (IOException e1) {
            log.error("Error writing to log file: ", e1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e2) {
                    log.error("Error closing log file: ", e2);
                }
            }
        }
    }

    protected void initApp() throws ServletException {
        try {
            String baseAppDir = getInitParameter(INIT_PARAM_APP_DIR);
            if (baseAppDir == null) {
                baseAppDir = new File(getServletContext().getRealPath("/")).getPath();
                baseAppDir = StringUtils.replace(baseAppDir, "\\", "/");
                log.info("Application Directory: " + baseAppDir);
            }

            String baseCfgDir = getInitParameter(INIT_PARAM_CFG_DIR);
            if (baseCfgDir == null) {
                baseCfgDir = baseAppDir + "/WEB-INF/etc";
                log.info("Application Config Directory: " + baseCfgDir);
            }

            theApp = Application.lookup();
            theApp.setBaseAppDirectory(baseAppDir);
            theApp.setBaseCfgDirectory(baseCfgDir);
            theApp.start();
            initSuccess = true;
        } catch (Throwable e) {
            log.error("Error initializing application. Marking it as uninitialized ", e);
            initException = e;
            initSuccess = false;
            initError();
        }
    }

    /**
     * Process incoming HTTP requests
     *
     * @param request  Object that encapsulates the request to the servlet.
     * @param response Object that encapsulates the response from the servlet.
     */
    public final void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (theApp == null) initApp();
        if (initSuccess) {
            try {
                HTTPSettings settings = HTTPSettings.lookup();
                request.setCharacterEncoding(settings.getEncoding());
            } catch (UnsupportedEncodingException e) {
                log.error("Error: ", e);
            }

            // Init the request context.
            ControllerServletHelper helper = ControllerServletHelper.lookup();
            synchronized (helper) {
                CommandRequest cmdRq = helper.initThreadLocal(request, response);
                ControllerStatus.lookup().setRequest(cmdRq);
                try {
                    // Process the request (control layer)
                    processTheRequest(cmdRq);

                    // Render the view (presentation layer)
                    processTheView(cmdRq);
                } finally {

                    // Clear the request context.
                    helper.clearThreadLocal(request, response);
                }
            }
        } else {
            log.error("Received request, but application servlet hasn't been properly initialized. Ignoring.");
            response.sendError(500, "Application incorrectly initialized.");
        }
    }

    protected void processTheRequest(CommandRequest request) {
        try {
            // Process the request.
            RequestProcessor.lookup().run();

            // Ensure GETs URIs are fully processed.
            if ("GET".equalsIgnoreCase(request.getRequestObject().getMethod())) {
                ControllerStatus.lookup().compareConsumedUri();
            }
        } catch (Throwable e) {
            // Display the error.
            displayTheError(e);
        }
    }

    protected void processTheView(CommandRequest request) {
        try {
            if (log.isDebugEnabled()) log.debug("Rendering response. Id=" + Thread.currentThread().getName());

            CommandResponse cmdResponse = ControllerStatus.lookup().getResponse();
            cmdResponse.execute(request);
        } catch (Throwable e) {
            log.error("Error painting response. User might have seen something ugly in the browser if he is still there.", e);
        }
    }

    protected void displayTheError(Throwable t) {
        // Get the error generated during the thread's execution.
        ErrorReport report = ErrorManager.lookup().getErrorReport();

        // Initialize the error handler bean.
        ErrorReportHandler errorHandler = ErrorReportHandler.lookup();
        errorHandler.setWidth(1000);
        errorHandler.setHeight(400);
        errorHandler.setErrorReport(report);

        // Force the current screen to be refreshed so the error report will be displayed.
        CurrentComponentRenderer.lookup().setCurrentComponent(errorHandler);
        ControllerStatus controllerStatus = ControllerStatus.lookup();
        controllerStatus.setResponse(new ShowCurrentScreenResponse());
    }
}