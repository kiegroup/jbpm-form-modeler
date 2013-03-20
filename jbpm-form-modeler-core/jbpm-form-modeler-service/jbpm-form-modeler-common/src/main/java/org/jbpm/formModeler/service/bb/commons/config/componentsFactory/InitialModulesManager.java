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
package org.jbpm.formModeler.service.bb.commons.config.componentsFactory;

/**
 * A core factory component that manages the loading and execution of all the registered InitialModule components
 * at start-up time.
 */
public class InitialModulesManager extends BasicFactoryElement {
    public static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(InitialModulesManager.class.getName());

    private InitialModule modules[];


    public InitialModule[] getModules() {
        return modules;
    }

    public void setModules(InitialModule[] modules) {
        this.modules = modules;
    }

    public void start() throws Exception {
        if (modules != null) {
            for (final InitialModule module : modules) {
                if (module.doTheInstall()) {
                    if (log.isDebugEnabled()) log.debug("Installed module " + module.getName() + " version " + module.getVersion());
                } else {
                    log.warn("Error installing module " + module.getName() + " version " + module.getVersion());
                }
            }
        }
    }
}
