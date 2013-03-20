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

/**
 * Exception thrown when a parameter requested to the ConfigurationManager is not found.
 */
public class ParameterNotFoundException extends Exception {

    /**
     * log
     */
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ParameterNotFoundException.class);

    protected String paramName;

    /**
     * @deprecated use ParameterNotFoundException(String message, String paramName)
     */
    public ParameterNotFoundException() {
        super();
    }

    /**
     * @deprecated use ParameterNotFoundException(String message, String paramName)
     */
    public ParameterNotFoundException(String s) {
        super(s);
    }

    public ParameterNotFoundException(String message, String paramName) {
        super(message);
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
