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
package org.jbpm.formModeler.core.validators;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.*;
import java.util.Locale;
import java.util.StringTokenizer;


public class NumericRangeValidator {
    private static transient Logger log = LoggerFactory.getLogger(NumericRangeValidator.class);

    private static final char RANGE_SYMBOL_EXCLUDE_START = '(';
    private static final char RANGE_SYMBOL_EXCLUDE_END = ')';
    private static final char RANGE_SYMBOL_INCLUDE_START = '[';
    private static final char RANGE_SYMBOL_INCLUDE_END = ']';
    private static final char RANGE_SYMBOL_SEPARATOR = ';';
    private static final char RANGE_SYMBOL_UNION = '+';

    private static final String[] formats = {RANGE_SYMBOL_EXCLUDE_START + "{0}" + RANGE_SYMBOL_SEPARATOR + "{1}" + RANGE_SYMBOL_EXCLUDE_END,
            RANGE_SYMBOL_INCLUDE_START + "{0}" + RANGE_SYMBOL_SEPARATOR + "{1}" + RANGE_SYMBOL_INCLUDE_END,
            RANGE_SYMBOL_EXCLUDE_START + "{0}" + RANGE_SYMBOL_SEPARATOR + "{1}" + RANGE_SYMBOL_INCLUDE_END,
            RANGE_SYMBOL_INCLUDE_START + "{0}" + RANGE_SYMBOL_SEPARATOR + "{1}" + RANGE_SYMBOL_EXCLUDE_END};

    private String pattern;

    private static final MessageFormat[] mfs = {new MessageFormat(formats[0]), new MessageFormat(formats[1]),
            new MessageFormat(formats[2]), new MessageFormat(formats[3])};

    public NumericRangeValidator(String pattern) throws IllegalArgumentException {
        if (!validatePattern(pattern)) throw new IllegalArgumentException("Pattern is not valid: '" + pattern + "'");
        this.pattern = pattern;

    }

    private boolean validatePattern(String pattern) {
        pattern = pattern.trim();
        MessageFormat mf;

        int error = 0;

        if ((pattern == null) || "".equals(pattern)) return true;

        StringTokenizer strtk = new StringTokenizer(pattern, String.valueOf(RANGE_SYMBOL_UNION));
        while (strtk.hasMoreElements()) {
            String token = strtk.nextToken();
            token = token.replace('.', ',');
            error = 0;
            for (int i = 0; i < formats.length; i++) {
                try {
                    mf = mfs[i];
                    Object[] results = mf.parse(token);
                    if (!mf.format(results, new StringBuffer(), new FieldPosition(0)).toString().equals(token)) error++;

                    NumberFormat nf1 = NumberFormat.getInstance(Locale.ENGLISH);

                    Number a = nf1.parse((String) results[0]);
                    Number b = nf1.parse((String) results[1]);


                    if (a.doubleValue() > b.doubleValue()) error++;
                } catch (ParseException pe) {
                    error++;
                }
            }

            if (error >= 4) {
                return false;
            }
        }

        return true;

    }


    public boolean isValid(double n) {
        boolean isValid = false;
        StringTokenizer strtk = new StringTokenizer(pattern, String.valueOf(RANGE_SYMBOL_UNION));
        MessageFormat mf;
        while (strtk.hasMoreElements()) {
            String token = strtk.nextToken();
            for (int i = 0; i < formats.length; i++)
                try {
                    mf = mfs[i];
                    Object[] results = mf.parse(token);

                    DecimalFormat nf1 = new DecimalFormat("###.##");

                    Number a = nf1.parse((String) results[0]);
                    Number b = nf1.parse((String) results[1]);

                    switch (i) {
                        case 0:
                            if ((n > a.doubleValue()) && (n < b.doubleValue())) isValid = true;
                            break;
                        case 1:
                            if ((n >= a.doubleValue()) && (n <= b.doubleValue())) isValid = true;
                            break;
                        case 2:
                            if ((n > a.doubleValue()) && (n <= b.doubleValue())) isValid = true;
                            break;
                        case 3:
                            if ((n >= a.doubleValue()) && (n < b.doubleValue())) isValid = true;
                            break;
                    }
                } catch (ParseException pe) {
                    if (i > 4) log.error("Error parsing number. Validation uncomplete");
                }

        }
        return isValid;
    }

    public boolean isValid(long n) {
        return isValid((double) n);
    }

    public boolean isValid(int n) {
        return isValid((double) n);
    }

    public boolean isValid(String str) {
        return isValid(Double.valueOf(str).doubleValue());
    }

    public boolean isValid(Integer i) {
        return isValid(i.intValue());
    }

    public boolean isValid(Double d) {
        return isValid(d.doubleValue());
    }

    public boolean isValid(Long l) {
        return isValid(l.longValue());
    }


}


