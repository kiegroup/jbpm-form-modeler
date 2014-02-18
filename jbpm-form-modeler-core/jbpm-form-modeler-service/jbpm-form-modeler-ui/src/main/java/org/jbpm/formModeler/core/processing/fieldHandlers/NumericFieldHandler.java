
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
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.api.model.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.*;

/**
 * Handler for numeric text input
 */
@Named("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler")
public class NumericFieldHandler extends DefaultFieldHandler {
    public static final boolean DEFAULT_MAX_VALUE = true;

    private static transient Logger log = LoggerFactory.getLogger(NumericFieldHandler.class);

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        String[] paramValue = (String[]) parametersMap.get(inputName);
        return getTheValue(field, paramValue, desiredClassName);

    }

    public Object getTheValue(Field field, String[] paramValue, String desiredClassName) throws Exception {
        if (paramValue == null || paramValue.length == 0)
            return null;

        if (desiredClassName.equals("byte")) {
            if (StringUtils.isEmpty(paramValue[0])) return new Byte( (byte)0 );
            else return Byte.decode(paramValue[0]);
        } else if (desiredClassName.equals("short")) {
            if (StringUtils.isEmpty(paramValue[0])) return new Short( (short)0 );
            else return Short.decode(paramValue[0]);
        } else if (desiredClassName.equals("int")) {
            if (StringUtils.isEmpty(paramValue[0])) return new Integer(0);
            else return Integer.decode(paramValue[0]);
        } else if (desiredClassName.equals("long")) {
            if (StringUtils.isEmpty(paramValue[0])) return new Long(0L);
            else return Long.decode(paramValue[0]);
        } else if (desiredClassName.equals(Byte.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Byte.decode(paramValue[0]);
        } else if (desiredClassName.equals(Short.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Short.decode(paramValue[0]);

        } else if (desiredClassName.equals(Integer.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Integer.decode(paramValue[0]);

        } else if (desiredClassName.equals(Long.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Long.decode(paramValue[0]);

        } else if (desiredClassName.equals(Double.class.getName()) || desiredClassName.equals("double") ||
                   desiredClassName.equals(Float.class.getName())  || desiredClassName.equals("float") ||
                   desiredClassName.equals(BigDecimal.class.getName())) {

            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();

            DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(new Locale(LocaleManager.currentLang()));
            if (desiredClassName.equals(BigDecimal.class.getName())) df.setParseBigDecimal(true);
            String pattern = field.getFieldPattern();
            if (pattern != null && !"".equals(pattern)) {
                df.applyPattern(pattern);
            } else {
                df.applyPattern("###.##");
            }
            ParsePosition pp = new ParsePosition(0);
            Number num = df.parse(paramValue[0], pp);            
            if (paramValue[0].length() != pp.getIndex() || num == null) {
                log.debug("Error on parsing value");
                throw new ParseException("Error parsing value", pp.getIndex());
            }

            if (desiredClassName.equals(BigDecimal.class.getName())) {
                return num;
            } else if (desiredClassName.equals(Float.class.getName()) || desiredClassName.equals("float")) {
                return new Float(num.floatValue());
            } else if (desiredClassName.equals(Double.class.getName()) || desiredClassName.equals("double")) {
                return new Double(num.doubleValue());
            }
        }  else if (desiredClassName.equals(BigInteger.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return new BigInteger(paramValue[0]);

        }
        throw new IllegalArgumentException("Invalid class for NumericFieldHandler: " + desiredClassName);
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
        if (objectValue == null) return Collections.EMPTY_MAP;
        Map m = new HashMap();
        m.put(inputName, buildParamValue(objectValue, pattern));
        return m;
    }

    /**
     * Builds a correct paramValue for a given inputValue.
     *
     * @param value   Object value to build its paramValue
     * @param pattern pattern to apply if any
     * @return a String[] with the paramValue on it or null if the value received is null
     */
    protected String[] buildParamValue(Object value, String pattern) {
        String[] result = null;
        if (value != null && Arrays.asList(getCompatibleClassNames()).contains(value.getClass().getName())) {
            if (pattern != null && !"".equals(pattern)) { // Float and Double fields type always have pattern
                try {
                    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(new Locale(LocaleManager.currentLang()));
                    if (value instanceof BigDecimal) df.setParseBigDecimal(true);
                    df.applyPattern(pattern);

                    if (value instanceof Float) {
                        value = df.format((((Float) value).floatValue()));
                    } else if (value instanceof BigDecimal) {
                        value = df.format(value);
                    } else {
                        value = df.format((((Double) value)).doubleValue());
                    }
                } catch (Exception e) {
                    if (value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
                        return buildParamValue(value, null);
                    }
                    return result;
                }
            }
            result = new String[]{value.toString()};
        }
        return result;
    }

    /**
     * Determine the list of class types this field can generate. That is, normally,
     * a field can generate multiple outputs (an input text can generate Strings,
     * Integers, ...)
     *
     * @return the set of class types that can be generated by this handler.
     */
    public String[] getCompatibleClassNames() {
        return new String[]{String.class.getName(), Byte.class.getName(), Short.class.getName(), Integer.class.getName(),
                Long.class.getName(), Float.class.getName(), Double.class.getName(), BigDecimal.class.getName(),
                BigInteger.class.getName()};
    }

    public boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof Object[]) {
            Object[] values = (Object[]) value;
            for (int i = 0; i < values.length; i++) {
                Object _value = values[i];
                if (_value != null) return false;
            }
            return true;
        }

        return value != null && "".equals(value);
    }

    public class EmptyNumberException extends Exception {
    }
}
