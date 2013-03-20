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

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.service.bb.commons.ApplicationManager;
import org.jbpm.formModeler.service.bb.commons.config.ConfigurationManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Component;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.ComponentsContextManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.FactoryWork;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.controller.impl.GenericPathResolver;
import org.jbpm.formModeler.service.bb.mvc.controller.requestChain.RequestChainProcessor;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.ShowCurrentScreenResponse;
import org.jbpm.formModeler.service.error.ErrorManager;
import org.jbpm.formModeler.service.error.ErrorReport;
import org.jbpm.formModeler.service.error.ErrorReportHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * This is the servlet that performs application control. It's a generic
 * controller servlet that provides the following features:
 * <li> Horizontal services to the whole application, such as login and
 * permission checking.
 * <li> Analyze the requests received and dispatch to the proper execution
 * method.
 * <li> Perform application initialization and ending.
 * <li> Provide an interface to session events.
 */
public class ControllerServlet extends javax.servlet.http.HttpServlet {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ControllerServlet.class.getName());

    /**
     * MVC version.
     */
    public final static String MVC_VERSION = "1.8.07";

    /**
     * Directory configuration files
     */
    public final static String FRAMEWORK_CONFIG_DIR = "config";

    /**
     * Directory configuration files
     */
    public final static String FACTORY_CONFIG_DIR = "factory";

    /**
     * Description of the Field
     */
    public final static String FILE_LOG4J_CONFIG = "log4j.cfg";

    /**
     * Description of the Field
     */
    public final static String FILE_VIEWS_CONFIG = "views.xml";

    /**
     * File to configure velocity templates engine
     */
    public final static String FILE_VELOCITY_CONFIG = "appdata/velocity/velocity.cfg";

    /**
     * Init parameters to pass to this servlet
     */
    public final static String INIT_PARAM_CFG_DIR = "base.cfg.dir";

    /**
     * Description of the Field
     */
    public final static String INIT_PARAM_INITIALIZER_CLASS = "init.class";

    /**
     * Description of the Field
     */
    public final static String INIT_PARAM_PATHRESOLVER_CLASS = "path.resolver.class";

    /**
     * Description of the Field
     */
    public final static String INIT_PARAM_APP_DIR = "base.app.dir";

    private static boolean initSuccess = true;
    private static Throwable startupException;

    /**
     * Initializes the servlet.
     *
     * @throws javax.servlet.ServletException Description of the Exception
     */
    public void init() throws ServletException {
        try {
            initServletDirectories();
            initFactory();
            Factory.doWork(new FactoryWork() {
                public void doWork() {
                    Factory.lookup("org.jbpm.formModeler.service.Initial"); //Start initial components (if they are not already).
                    ApplicationManager apm = (ApplicationManager) Factory.lookup("org.jbpm.formModeler.service.ApplicationManager");
                    apm.setUpAndRunning(true);
                }
            });
        } catch (Throwable e) {
            log.error("Error initializing application. Marking it as uninitialized ", e);
            startupException = e;
            initSuccess = false;
        }
        if (!initSuccess) {
            // Write some data to file, allowing external chacking of what went wrong
            File outputFile = new File(ConfigurationManager.singleton().getBaseAppDirectory() + "/ControllerError.txt");
            FileWriter writer = null;
            try {
                writer = new FileWriter(outputFile);
                if (startupException != null) writer.write(startupException.getMessage() + "\n");
                writer.close();
                outputFile.deleteOnExit();
            } catch (IOException e1) {
                log.error("Error writing to log file: ", e1);
            }
            // Destroy the Factory configuration.
            ConfigurationManager.singleton().setGlobalFactory(null);
        }
    }

    protected void initServletDirectories() throws ServletException {
        String baseConfigDir = getInitParameter(INIT_PARAM_CFG_DIR);
        String baseAppDir = getInitParameter(INIT_PARAM_APP_DIR);
        //String initializerClass = getInitParameter(INIT_PARAM_INITIALIZER_CLASS);
        String pathResolverClass = getInitParameter(INIT_PARAM_PATHRESOLVER_CLASS);
        ApplicationPathResolver pathResolver = new GenericPathResolver();
        try {
            //Instantiate the Application Path Resolver Class
            if (pathResolverClass != null) {
                Class initClass = this.getClass().getClassLoader().loadClass(pathResolverClass);
                pathResolver = (ApplicationPathResolver) initClass.newInstance();
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new ServletException(e);
        }

        try {
            if (baseAppDir == null) {
                baseAppDir = new File(pathResolver.resolvePath(getServletContext())).getAbsolutePath();
                baseAppDir = StringUtils.replace(baseAppDir, "\\", "/");
                log.info("Application Directory: " + baseAppDir);
            }
            if (baseConfigDir == null) {
                baseConfigDir = baseAppDir + "/WEB-INF/etc";
                log.info("Application Config Directory: " + baseConfigDir);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new ServletException(e);
        }

        ConfigurationManager.singleton().setBaseAppDirectory(baseAppDir);
        ConfigurationManager.singleton().setBaseCfgDirectory(baseConfigDir);

        log.info("Base app dir : " + baseAppDir);
        log.info("Base cfg dir : " + baseConfigDir);
    }

    /**
     * Init the advanced configuration subsystem based in the Factory trees.
     */
    protected void initFactory() {
        if (ConfigurationManager.singleton().getGlobalFactory() == null) {
            String factoryCfgDir = ConfigurationManager.singleton().getBaseCfgDirectory() + "/" + FACTORY_CONFIG_DIR;
            Factory factory = null;
            factory = Factory.getFactory(new File(factoryCfgDir));
            if (factory != null) ConfigurationManager.singleton().setGlobalFactory(factory);
        }
        addCustomScopesToFactory();
    }

    protected void addCustomScopesToFactory() {
        ComponentsContextManager.addComponentStorage(Component.SCOPE_REQUEST, new RequestComponentsStorage());
        ComponentsContextManager.addComponentStorage(Component.SCOPE_SESSION, new SessionComponentsStorage());
    }

    /**
     * Process incoming HTTP requests
     *
     * @param request  Object that encapsulates the request to the servlet.
     * @param response Object that encapsulates the response from the servlet.
     */
    public final void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (initSuccess) {
            Factory.doWork(new FactoryWork() {
                public void doWork() {
                    // Save the request star time.
                    ControllerServletHelper helper = (ControllerServletHelper) Factory.lookup("org.jbpm.formModeler.service.mvc.controller.ControllerServletHelper");

                    if (helper.framework.getFrameworkEncoding() != null) {
                        try {
                            request.setCharacterEncoding(helper.framework.getFrameworkEncoding());
                        } catch (UnsupportedEncodingException e) {
                            log.error("Error: ", e);
                        }
                    }

                    long timeStart = System.currentTimeMillis();

                    // Init the request context.
                    CommandRequest cmdRq = helper.initThreadLocal(request, response);
                    helper.getStatus().setRequest(cmdRq);

                    try {
                        // Process the request (control layer)
                        processTheRequest(request, response, helper);

                        // Render the view (presentation layer)
                        processTheView(request, response, helper);
                    } finally {

                        // Clear the request context.
                        helper.clearThreadLocal(request, response);
                    }
                }
            });
        } else {
            log.error("Received request, but application servlet hasn't been properly initialized. Ignoring.");
            response.sendError(500, "Application incorrectly initialized.");
        }
    }

    protected void processTheRequest(final HttpServletRequest request, final HttpServletResponse response, final ControllerServletHelper helper) {
        try {
            RequestChainProcessor requestProcessor = (RequestChainProcessor) Factory.lookup("org.jbpm.formModeler.service.mvc.controller.requestChain.StartingProcessor");
            requestProcessor.doRequestProcessing();

            // Ensure GETs URIs are fully processed.
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                helper.getStatus().compareConsumedUri();
            }
        } catch (Throwable e) {
            // Display the error.
            displayTheError(e);
        }
    }


    protected void processTheView(final HttpServletRequest request, final HttpServletResponse response, final ControllerServletHelper helper) {
        try {
            if (log.isDebugEnabled()) log.debug("Rendering response. Id=" + Thread.currentThread().getName());
            RequestChainProcessor renderingProcessor = (RequestChainProcessor) Factory.lookup("org.jbpm.formModeler.service.mvc.controller.requestChain.StartingRenderer");
            renderingProcessor.doRequestProcessing();
        } catch (Throwable e) {
            log.error("Error painting response. User might have seen something ugly in the browser if he is still there.", e);
        }
    }

    protected void displayTheError(Throwable t) {
        // Get the error has been generated during the thread's execution.
        ErrorReport report = ErrorManager.lookup().getErrorReport();

        // Initialize the error handler bean.
        ErrorReportHandler errorHandler = (ErrorReportHandler) Factory.lookup("org.jbpm.formModeler.service.error.ErrorReportHandler");
        errorHandler.setWidth(1000);
        errorHandler.setHeight(400);
        errorHandler.setErrorReport(report);

        CurrentComponentRenderer.lookup().setCurrentComponent(errorHandler);

        // Force the current screen to be refreshed so the error report will be displayed.
        ControllerStatus controllerStatus = ControllerStatus.lookup();
        controllerStatus.setResponse(new ShowCurrentScreenResponse());
    }

    /**
     * Called when it's destroyed.
     */
    public void destroy() {
        // Destroy the Factory configuration.
        ConfigurationManager.singleton().setGlobalFactory(null);
        log.debug("Destroying controller servlet");
    }
}