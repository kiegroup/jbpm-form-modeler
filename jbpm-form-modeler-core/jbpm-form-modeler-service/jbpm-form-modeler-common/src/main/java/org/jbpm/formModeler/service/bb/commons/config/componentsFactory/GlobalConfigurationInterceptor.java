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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 */
public class GlobalConfigurationInterceptor implements DynamicPropertyHandler {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(GlobalConfigurationInterceptor.class.getName());
    protected Hashtable mappings = new Hashtable();

    public void setProperty(String name, Object value) {
        if (value != null)
            mappings.put(name, value);
        else
            mappings.remove(name);
    }

    public Object getProperty(String name) {
        return mappings.get(name);
    }

    public Enumeration getProperties() {
        return mappings.keys();
    }
}
