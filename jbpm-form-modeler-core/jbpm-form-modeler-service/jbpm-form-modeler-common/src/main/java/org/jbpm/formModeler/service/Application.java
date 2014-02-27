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
package org.jbpm.formModeler.service;

import org.jbpm.formModeler.service.annotation.StartableProcessor;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Class that defines some application set-up parameters.
 */
@ApplicationScoped
public class Application {

    public static Application lookup() {
        return (Application) CDIBeanLocator.getBeanByType(Application.class);
    }

    @Inject
    protected StartableProcessor startupProcessor;

    protected String baseCfgDirectory = null;
    protected String baseAppDirectory = null;

    public String getBaseAppDirectory() {
        return baseAppDirectory;
    }

    public String getBaseCfgDirectory() {
        return baseCfgDirectory;
    }

    public void setBaseAppDirectory(String newBaseAppDirectory) {
        baseAppDirectory = newBaseAppDirectory;
    }

    public void setBaseCfgDirectory(String newBaseCfgDirectory) {
        baseCfgDirectory = newBaseCfgDirectory;
    }

    public void start() throws Exception {
        startupProcessor.wakeUpStartableBeans();
    }
}
