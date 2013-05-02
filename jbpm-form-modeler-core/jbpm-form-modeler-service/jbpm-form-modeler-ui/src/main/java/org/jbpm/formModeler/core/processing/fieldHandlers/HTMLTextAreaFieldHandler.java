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

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.jbpm.formModeler.core.wrappers.HTMLString;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.service.annotation.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for HTML text area
 */
public class HTMLTextAreaFieldHandler extends DefaultFieldHandler {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HTMLTextAreaFieldHandler.class.getName());

    public String[] getCompatibleClassNames() {
        return new String[]{"HTMLEditor"};
    }

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        String[] pValues = (String[]) parametersMap.get(inputName);
        return pValues != null ? new HTMLString(pValues[0]) : null;
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
            else if (objectValue instanceof HTMLString)
                m.put(inputName, new String[]{((HTMLString) objectValue).getValue()});
            else
                log.error("Unknown value type to convert to parameter: " + objectValue.getClass());
        }
        return m;
    }

    public boolean isEmpty(Object value) {
        String textContent = null;
        HTMLString html = (HTMLString) value;
        try {
            Reader reader = new StringReader(html.getValue().replaceAll("&nbsp;", ""));
            StringWriter sb = new StringWriter();
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                sb.write(buffer, 0, length);
            }
            reader.close();
            textContent = sb.toString().trim();
            sb.close();
        } catch (IOException e) {
            log.warn("Error: ", e);
        }
        return value == null || "".equals(html.getValue()) || html.getValue() == null || textContent.length() == 0;
    }

    public boolean acceptsPropertyName(String propName) {
        return true;
    }
}
