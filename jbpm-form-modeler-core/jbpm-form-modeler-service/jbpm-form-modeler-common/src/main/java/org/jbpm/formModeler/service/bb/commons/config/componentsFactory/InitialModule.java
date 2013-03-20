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
 * A core factory component addressed to initialize or update application data at system start-up. 
 */
public abstract class InitialModule extends BasicFactoryElement {
    public static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(InitialModule.class.getName());

    private String name;
    private long version = 1;

    public String getName() {
        return name != null ? name : getComponentName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean doTheInstall() {
        return install();
    }

    /**
     * Install this module
     *
     * @return true on success
     */
    protected abstract boolean install();
}
