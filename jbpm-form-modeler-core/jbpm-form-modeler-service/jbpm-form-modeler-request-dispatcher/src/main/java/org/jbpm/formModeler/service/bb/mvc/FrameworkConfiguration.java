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
package org.jbpm.formModeler.service.bb.mvc;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestContext;


/**
 * Configuration parameters for framework retrieved from the generic
 * Configuration class.
 *
 * @deprecated You should use Factory component org.jbpm.formModeler.service.mvc.Framework instead.
 */
public class FrameworkConfiguration {
    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FrameworkConfiguration.class.getName());
    private static String FRAMEWORK_PATH = "org.jbpm.formModeler.service.mvc.Framework";


    /**
     * Gets the controllerMapping attribute of the FrameworkConfiguration class
     *
     * @return The controllerMapping value
     */
    public static String getControllerMapping() {
        return ((Framework) Factory.lookup(FRAMEWORK_PATH)).getControllerMapping();
    }


    /**
     * Gets the web application context attribute of the FrameworkConfiguration class
     *
     * @return The controllerMapping value
     * @deprecated You can use request.getContextPath() instead !!
     */
    public static String getAppContext() {
        RequestContext reqCtx = RequestContext.getCurrentContext();
        return reqCtx.getRequest().getRequestObject().getContextPath();
    }

    /**
     * Gets the web application base URL attribute of the FrameworkConfiguration class
     *
     * @return The controllerMapping value
     */
    public static String getAppBaseURL() {
        return ((Framework) Factory.lookup(FRAMEWORK_PATH)).getParamWebAppUrl();
    }

    /**
     * Gets the maxPostSize attribute of the FrameworkConfiguration class
     *
     * @return The maxPostSize value
     */
    public static int getMaxPostSize() {
        return ((Framework) Factory.lookup(FRAMEWORK_PATH)).getMaxPostSize();
    }


    /**
     * Gets the multipartProcessing attribute of the FrameworkConfiguration class
     *
     * @return The multipartProcessing value
     */
    public static boolean getMultipartProcessing() {
        return ((Framework) Factory.lookup(FRAMEWORK_PATH)).isMultipartProcessing();
    }


    /**
     * Gets the paramDownloadDir attribute of the FrameworkConfiguration class
     *
     * @return The paramDownloadDir value
     */
    public static String getParamDownloadDir() {
        return ((Framework) Factory.lookup(FRAMEWORK_PATH)).getDownloadDir();
    }

}
