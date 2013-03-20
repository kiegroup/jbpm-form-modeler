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
public class StringValueConverter extends DefaultValueConverter implements ValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(StringValueConverter.class.getName());

    public Class getAcceptedClass() {
        return String.class;
    }

    public List writeValue(Object value) {
        if (value == null || "".equals(value)) return Collections.EMPTY_LIST;
        ArrayList l = new ArrayList();
        DynValue val = new DynValue();
        val.setStringValue((String) value);
        l.add(val);
        return l;
    }

    public Object readValue(List values) {
        if (values == null || values.isEmpty())
            return null;
        return ((DynValue) values.get(0)).getStringValue();
    }

    /**
     * Given an Object belonging to an accepted class, convert it to a String
     * value representing it.
     *
     * @param value Value to convert
     * @return a String representation for this object.
     */
    public String getTextValue(Object value) {
        return (String) value;
    }

    /**
     * Given a text value, which is a representation for an object, convert it
     * to the original object.
     *
     * @param textValue
     * @return an object read from a String value
     */
    public Object readTextValue(String textValue) {
        return textValue;
    }

    /**
     * Convert a value of accepted class into a flat String value
     *
     * @param value
     * @return a String representation for this object to be used in search indexes
     */
    public String flatValue(Object value, String lang) {
        return StringUtils.defaultString((String) value);
    }

    public boolean isTokenizable() {
        return true;
    }
}
