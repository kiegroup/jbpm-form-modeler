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
package org.jbpm.formModeler.core.valueConverters;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 *
 */
public class PropertiesValueConverter extends DefaultValueConverter implements ValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(PropertiesValueConverter.class.getName());

    public Class getAcceptedClass() {
        return Properties.class;
    }

    public List writeValue(Object value) {
        if (value == null) return Collections.EMPTY_LIST;
        Properties pValue = (Properties) value;
        ArrayList l = new ArrayList();
        for (Iterator it = pValue.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            String val = pValue.getProperty(key);
            DynValue pdval = new DynValue();
            pdval.setSelector(key);
            pdval.setStringValue(val);
            l.add(val);
        }
        return l;
    }

    public Object readValue(List values) {
        if (values == null)
            return null;
        if (values.isEmpty())
            return new Properties();
        Properties valToReturn = new Properties();
        for (int i = 0; i < values.size(); i++) {
            DynValue dynValue = (DynValue) values.get(i);
            String selector = dynValue.getSelector();
            valToReturn.setProperty(selector, dynValue.getStringValue());
        }
        return valToReturn;
    }

    /**
     * Given an Object belonging to an accepted class, convert it to a String
     * value representing it.
     *
     * @param value Value to convert
     * @return a String representation for this object.
     */
    public String getTextValue(Object value) {
        if (value == null)
            return null;
        List values = new ArrayList();
        Properties svalue = (Properties) value;
        for (Iterator iterator = svalue.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            String val = svalue.getProperty(key);
            values.add(key);
            values.add(val);
        }
        return encodeStringArray((String[]) values.toArray(new String[values.size()]));
    }

    /**
     * Given a text value, which is a representation for an object, convert it
     * to the original object.
     *
     * @param textValue
     * @return an object read from a String value
     */
    public Object readTextValue(String textValue) {
        if (textValue == null || "".equals(textValue)) {
            return null;
        }
        String[] s = decodeStringArray(textValue);
        Properties prop = new Properties();
        for (int i = 0; i < s.length - 1; i++) {
            final String key = s[i];
            final String value = s[i++];
            prop.setProperty(key, value);
        }
        return prop;
    }

    /**
     * Convert a value of accepted class into a flat String value
     *
     * @param value
     * @return a String representation for this object to be used in search indexes
     */
    public String flatValue(Object value, String lang) {
        Properties prop = (Properties) value;
        StringBuffer sb = new StringBuffer();
        for (Iterator it = prop.keySet().iterator(); it.hasNext();) {
            String propName = (String) it.next();
            sb.append(" ").append(StringUtils.defaultString(prop.getProperty(propName)));
        }
        return sb.toString();
    }
}
