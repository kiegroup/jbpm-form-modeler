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
package org.jbpm.formModeler.service.bb.commons.config;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.DynamicPropertyHandler;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.FactoryWork;

import java.util.*;

/**
 * Cross-platform class containing some useful information regarding the application like
 * the application install directory or information about the release build number.
 */

public class ConfigurationManager {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ConfigurationManager.class.getName());

    /*
	 *  Home, config and application base directories.
	 */
    private String baseAppDirectory = null;

    private String baseCfgDirectory = null;

    private Factory globalFactory = null;

    /*
	 * Singleton object
	 */
    private static ConfigurationManager theManager = new ConfigurationManager();

    /*
	 *  Map holding all values under the [section].[value] key.
	 */
    private Map values = new HashMap();


    /**
     * Constructor for the ConfigurationManager object
     */
    private ConfigurationManager() {
        super();
    }

    /**
     * Description of the Method
     */
    public void clear() {
        values.clear();
    }


    /**
     * Gets the baseAppDirectory attribute of the ConfigurationManager object
     *
     * @return The baseAppDirectory value
     */
    public String getBaseAppDirectory() {
        return baseAppDirectory;
    }


    /**
     * Gets the baseCfgDirectory attribute of the ConfigurationManager object
     *
     * @return The baseCfgDirectory value
     */
    public String getBaseCfgDirectory() {
        return baseCfgDirectory;
    }


    public Object getParameter(String sectionId, String param) throws ParameterNotFoundException {
        String propertyName = sectionId + "." + param;
        Object value = values.get(propertyName);
        if (!propertyName.startsWith("license.") && getGlobalFactory() != null) {
            DynamicPropertyHandler handler = (DynamicPropertyHandler) Factory.lookup("org.jbpmGlobalConfigurationInterceptor");
            if (handler != null) {
                Object handlerValue = handler.getProperty(propertyName);
                if (handlerValue != null) {
                    if (handlerValue instanceof String) {
                        value = handlerValue;
                    } else if (handlerValue instanceof String[]) {
                        String[] s = (String[]) handlerValue;
                        if (s.length != 1) {
                            log.error("Refusing to overwrite property " + propertyName + " with String[] with length " + s.length);
                        } else {
                            value = s[0];
                        }
                    } else {
                        log.error("Refusing to overwrite property " + propertyName + " with " + value);
                    }
                }
            }
        }
        if (value == null) {
            throw new ParameterNotFoundException("Parameter " + param + " not found", param);
        }
        return value;
    }

    /**
     * Description of the Method
     *
     * @param loader Description of the Parameter
     * @return Description of the Return Value
     * @throws Exception Description of the Exception
     */
    public synchronized boolean load(ConfigurationLoader loader) throws Exception {
        Map newProperties = loader.loadConfiguration();
        if (newProperties != null) {
            setValues(newProperties);
        }
        return true;
    }


    /**
     * Sets the baseAppDirectory attribute of the ConfigurationManager object
     *
     * @param newBaseAppDirectory The new baseAppDirectory value
     */
    public synchronized void setBaseAppDirectory(String newBaseAppDirectory) {
        baseAppDirectory = newBaseAppDirectory;
    }


    /**
     * Sets the baseCfgDirectory attribute of the ConfigurationManager object
     *
     * @param newBaseCfgDirectory The new baseCfgDirectory value
     */
    public synchronized void setBaseCfgDirectory(String newBaseCfgDirectory) {
        baseCfgDirectory = newBaseCfgDirectory;
    }


    /**
     * Sets the values attribute of the ConfigurationManager object
     *
     * @param newValues The new values value
     */
    private void setValues(Map newValues) {
        values = newValues;
    }


    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public static ConfigurationManager singleton() {
        return theManager;
    }

    public Factory getGlobalFactory() {
        return globalFactory;
    }

    public void setGlobalFactory(Factory globalFactory) {
        if (this.globalFactory != null) {
            Factory.doWork(new FactoryWork() {
                public void doWork() {
                    ConfigurationManager.this.globalFactory.destroy();
                }
            });
        }
        this.globalFactory = globalFactory;
    }

}
