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
package org.jbpm.formModeler.core.processing.fieldHandlers;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.jbpm.formModeler.api.model.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for HTML text area
 */
public class HTMLTextAreaFieldHandler extends DefaultFieldHandler {
    private static transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HTMLTextAreaFieldHandler.class.getName());

    /** The suffix for the value <code>div</code> element. */
    public static final String VALUE_SUFFIX = "_value";

    public String[] getCompatibleClassNames() {
        return new String[]{String.class.getName()};
    }

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        String[] pValues = (String[]) parametersMap.get(inputName + VALUE_SUFFIX);
        return pValues != null ? StringUtils.defaultString(pValues[0]) : null;
    }

    /**
     * Determine the value as a parameter map for a given input value. This is like the inverse operation of getValue()
     *
     * @param objectValue Object value to represent
     * @param pattern     Pattern to apply if any
     * @return a Map representing the parameter values expected inside a request that would cause the form
     *         to generate given object value as a result.
     */
    public Map getParamValue(String inputName, Object objectValue, String pattern) {
        Map m = new HashMap();
        if (objectValue != null) {
            if (objectValue instanceof String)
                m.put(inputName, new String[]{(String) objectValue});
            else
                log.error("Unknown value type to convert to parameter: " + objectValue.getClass());
        }
        return m;
    }

    public boolean isEmpty(Object value) {
        String html = (String) value;
        return value == null || StringUtils.isEmpty(html);
    }
}
