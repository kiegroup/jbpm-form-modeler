
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.*;

/**
 * Handler for numeric text input
 */
@Named("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler")
public class NumericFieldHandler extends DefaultFieldHandler {

    public static final String NUMERIC_FROM_SUFFIX = "_from";
    public static final String NUMERIC_TO_SUFFIX = "_to";
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
        String[] numberFrom = (String[]) parametersMap.get(inputName + NUMERIC_FROM_SUFFIX);
        String[] numberTo = (String[]) parametersMap.get(inputName + NUMERIC_TO_SUFFIX);

        if (numberFrom != null || numberTo != null) {
            Object from = null;
            try {
                from = getTheValue(field, numberFrom, desiredClassName);
            } catch (EmptyNumberException e) { }

            Object to = null;
            try {
                to = getTheValue(field, numberTo, desiredClassName);
            } catch (Exception e) {}

            return new Object[]{from, to};
        }

        String[] paramValue = (String[]) parametersMap.get(inputName);
        return getTheValue(field, paramValue, desiredClassName);

    }

    public Object getTheValue(Field field, String[] paramValue, String desiredClassName) throws Exception {
        if (paramValue == null || paramValue.length == 0)
            return null;

        if (desiredClassName.equals(Short.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Short.decode(paramValue[0]);

        } else if (desiredClassName.equals(Integer.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Integer.decode(paramValue[0]);

        } else if (desiredClassName.equals(Long.class.getName())) {
            if (StringUtils.isEmpty(paramValue[0])) throw new EmptyNumberException();
            return Long.decode(paramValue[0]);

        } else if (desiredClassName.equals(Double.class.getName()) || desiredClassName.equals(Float.class.getName())  ||
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
            } else if (desiredClassName.equals(Float.class.getName())) {
                return new Float(num.floatValue());
            } else {
                return new Double(num.doubleValue());
            }
        }
        throw new IllegalArgumentException("Invalid class for InputTextFieldHandler: " + desiredClassName);
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

        if (objectValue.getClass().isArray()) {
            Object[] values = (Object[]) objectValue;
            if (values.length > 0 && values[0] != null) {
                m.put(inputName + NUMERIC_FROM_SUFFIX, buildParamValue(values[0], pattern));
            }
            if (values.length > 1 && values[1] != null) {
                m.put(inputName + NUMERIC_TO_SUFFIX, buildParamValue(values[1], pattern));
            }
        } else {
            m.put(inputName, buildParamValue(objectValue, pattern));
        }

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
                    if (value instanceof Short || value instanceof Integer || value instanceof Long) {
                        log.warn("error building paramValue for value " + value + " with pattern " + pattern + ", let's try again without pattern...");
                        return buildParamValue(value, null);
                    }
                    return result;
                }
            }
            result = new String[]{value.toString()};
        }
        return result;
    }

    public boolean acceptsPropertyName(String propName) {
        return true;
    }

    /**
     * Determine the list of class types this field can generate. That is, normally,
     * a field can generate multiple outputs (an input text can generate Strings,
     * Integers, ...)
     *
     * @return the set of class types that can be generated by this handler.
     */
    public String[] getCompatibleClassNames() {
        return new String[]{String.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), BigDecimal.class.getName()};
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
