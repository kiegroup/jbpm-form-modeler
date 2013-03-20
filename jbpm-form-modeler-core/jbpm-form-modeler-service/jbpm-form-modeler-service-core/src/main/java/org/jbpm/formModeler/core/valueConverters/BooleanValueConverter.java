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
/**
 * 
 */
package org.jbpm.formModeler.core.valueConverters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class BooleanValueConverter extends DefaultValueConverter implements ValueConverter {

    public Class getAcceptedClass() {
        return Boolean.class;
    }

    /*
    * (non-Javadoc)
    *
    * @see org.jbpm.formModeler.core.valueConverters.ValueConverter#writeValue(java.lang.Object)
    */
    public List writeValue(Object value) {
        if (value == null) return Collections.EMPTY_LIST;
        ArrayList l = new ArrayList();
        DynValue val = new DynValue();
        val.setNumberValue(((Boolean) value).booleanValue() ? new BigDecimal("1") : new BigDecimal("0"));
        l.add(val);
        return l;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jbpm.formModeler.core.valueConverters.ValueConverter#readValue(java.util.List)
     */
    public Object readValue(List values) {
        if (values == null || values.isEmpty())
            return null;
        return Boolean.valueOf(((DynValue) values.get(0)).getNumberValue().intValue() == 1);
    }

    /**
     * Given an Object belonging to an accepted class, convert it to a String
     * value representing it.
     *
     * @param value Value to convert
     * @return a String representation for this object.
     */
    public String getTextValue(Object value) {
        return value == null ? null : value.toString();
    }

    /**
     * Given a text value, which is a representation for an object, convert it
     * to the original object.
     *
     * @param textValue
     * @return an object read from a String value
     */
    public Object readTextValue(String textValue) {
        return (textValue == null || "".equals(textValue)) ? null : Boolean.valueOf(textValue);
    }

    /**
     * Convert a value of accepted class into a flat String value
     *
     * @param value
     * @return a String representation for this object to be used in search indexes
     */
    public String flatValue(Object value, String lang) {
        return value == null ? "" : String.valueOf((Boolean) value);
    }
}