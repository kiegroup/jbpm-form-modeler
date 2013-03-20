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
package org.jbpm.formModeler.service.bb.commons;

import org.jbpm.formModeler.service.bb.commons.config.ConfigurationManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Component;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.ComponentsTree;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Properties;

/**
 * The application manager contains the application status and some parameters like the installation directory.
 */
public class ApplicationManager extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ApplicationManager.class.getName());

    public static final String MODIFICATIONS_FILE = "/data/etc/userInstallationInput.properties";

    private String installDir;

    private boolean upAndRunning = false;

    public boolean isUpAndRunning() {
        return upAndRunning;
    }

    public void setUpAndRunning(boolean upAndRunning) {
        this.upAndRunning = upAndRunning;
    }

    public String getInstallDir() {
        return installDir;
    }

    public void setInstallDir(String installDir) {
        this.installDir = installDir;
    }

    /**
     * Determine if application is installed following the installation procedure
     *
     * @return true if application is installed following the installation procedure
     */
    public boolean isApplicationInstalled() {
        if (StringUtils.isEmpty(installDir))
            return false;
        File installDirFile = new File(installDir);
        if (!installDirFile.exists() || !installDirFile.isDirectory()) {
            return false;
        }
        return true;
    }

    /**
     * Determine if application configuration can be modified.
     *
     * @return true if application configuration can be modified.
     */
    public boolean canModifyConfiguration() {
        if (!isApplicationInstalled()) return false;
        File modificationFile = getConfigModificationFile();
        return modificationFile != null && modificationFile.exists() && modificationFile.canWrite();
    }

    protected File getConfigModificationFile() {
        if (isApplicationInstalled()) {
            return new File(installDir + MODIFICATIONS_FILE);
        }
        return null;
    }

    public int getHttpPort(){
        return getConfiguredPort("httpPort");
    }

    public int getAjpPort(){
        return getConfiguredPort("ajpPort");
    }

    protected int getConfiguredPort(String protocolParam) {
        File confFile = getConfigModificationFile();
        if (confFile != null) {
            Properties prop = new Properties();
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(confFile));
                prop.load(bis);
                bis.close();
                return Integer.parseInt(prop.getProperty("tomcat." + protocolParam, "-1"));
            } catch (IOException e) {
                log.error("Error: ", e);
            }
        }
        return -1;
    }
}
