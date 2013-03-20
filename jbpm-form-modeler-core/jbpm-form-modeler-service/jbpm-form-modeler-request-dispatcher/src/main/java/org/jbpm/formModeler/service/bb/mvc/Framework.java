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

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;

/**
 *
 */
public class Framework extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Framework.class.getName());

    public static final String AJAX_AREA_PREFFIX = "AJAX_area_for_";

    private String controllerMapping = "/Controller";
    private String paramWebAppUrl = "";
    private String downloadDir = "WEB-INF/tmp";
    private int maxPostSize = 10240;
    private boolean multipartProcessing = true;
    private String frameworkEncoding = "UTF-8";

    public String getControllerMapping() {
        return controllerMapping;
    }

    public void setControllerMapping(String controllerMapping) {
        this.controllerMapping = controllerMapping;
    }

    public String getParamWebAppUrl() {
        return paramWebAppUrl;
    }

    public void setParamWebAppUrl(String paramWebAppUrl) {
        this.paramWebAppUrl = paramWebAppUrl;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

    public int getMaxPostSize() {
        return maxPostSize;
    }

    public void setMaxPostSize(int maxPostSize) {
        this.maxPostSize = maxPostSize;
    }

    public boolean isMultipartProcessing() {
        return multipartProcessing;
    }

    public void setMultipartProcessing(boolean multipartProcessing) {
        this.multipartProcessing = multipartProcessing;
    }

    public String getFrameworkEncoding() {
        return frameworkEncoding;
    }

    public void setFrameworkEncoding(String frameworkEncoding) {
        this.frameworkEncoding = frameworkEncoding;
    }
}
