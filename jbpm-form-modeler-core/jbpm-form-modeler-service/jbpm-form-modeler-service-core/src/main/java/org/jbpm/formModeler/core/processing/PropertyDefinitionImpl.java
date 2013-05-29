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
package org.jbpm.formModeler.core.processing;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PropertyDefinitionImpl implements PropertyDefinition {

    private Class propertyClass;
    private Map propertyAttributes = new HashMap();
    private boolean required;

    public Map getPropertyAttributes() {
        return propertyAttributes;
    }

    public void setPropertyAttributes(Map propertyAttributes) {
        this.propertyAttributes = propertyAttributes;
    }

    public void setAttribute(String name, String val) {
        setAttribute(name, null, val);
    }

    public void setAttribute(String name, String lang, String val) {
        if (lang == null || "".equals(lang.trim())) {
            propertyAttributes.put(name, val);
        } else {
            Map m = (Map) propertyAttributes.get(name);
            if (m == null) propertyAttributes.put(name, m = new HashMap());
            m.put(lang, val);
        }
    }

    public Class getPropertyClass() {
        return propertyClass;
    }

    public String getPropertyClassName() {
        if (propertyClass.isArray()) {
            return propertyClass.getComponentType().getName() + "[]";
        } else {
            return propertyClass.getName();
        }
    }

    public String getId() {
        return getPropertyClassName();
    }

    public void setPropertyClass(Class propertyType) {
        this.propertyClass = propertyType;
    }


    public boolean isIdentifier() {
        return "true".equals(propertyAttributes.get(PROP_IDENTIFIER));
    }

    public boolean is(String flagName) {
        return "true".equals(propertyAttributes.get(flagName));        
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return propertyClass + " " + propertyAttributes;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
