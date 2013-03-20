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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class StringArrayValueConverter extends DefaultValueConverter implements ValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(StringArrayValueConverter.class.getName());


    public Class getAcceptedClass() {
        return String[].class;
    }

    public List writeValue(Object value) {
        if (value == null) return Collections.EMPTY_LIST;
        String[] sValue = (String[]) value;
        ArrayList l = new ArrayList();
        for (int i = 0; i < sValue.length; i++) {
            String s = sValue[i];
            DynValue val = new DynValue();
            val.setStringValue(s);
            val.setSelector(String.valueOf(i));
            l.add(val);
        }
        return l;
    }

    public Object readValue(List values) {
        if (values == null)
            return null;
        if (values.isEmpty())
            return new String[0];
        String[] valToReturn = new String[values.size()];
        boolean[] marks = new boolean[values.size()];

        for (int i = 0; i < values.size(); i++) {
            DynValue dynValue = (DynValue) values.get(i);
            String selector = dynValue.getSelector();
            int index = Integer.parseInt(selector);
            valToReturn[index] = dynValue.getStringValue();
            if (marks[index]) {
                log.error("Duplicated index " + index + " for String[] property id " + dynValue.getPropertyId() + ". This could only happen if you edited the database manually.");
            }
            marks[index] = true;
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
        String[] sval = (String[]) value;
        return encodeStringArray(sval);
    }

    /**
     * Given a text value, which is a representation for an object, convert it
     * to the original object.
     *
     * @param textValue
     * @return an object read from a String value
     */
    public Object readTextValue(String textValue) {
        if (textValue == null || "".equals(textValue))
            return null;
        return decodeStringArray(textValue);
    }

    /**
     * Convert a value of accepted class into a flat String value
     *
     * @param value
     * @return a String representation for this object to be used in search indexes
     */
    public String flatValue(Object value, String lang) {
        String[] s = (String[]) value;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length; i++) {
            String str = s[i];
            sb.append(" ").append(StringUtils.defaultString(str));
        }
        return sb.toString();
    }
}
