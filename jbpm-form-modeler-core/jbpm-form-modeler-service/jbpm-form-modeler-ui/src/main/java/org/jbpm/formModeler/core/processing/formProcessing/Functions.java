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
package org.jbpm.formModeler.core.processing.formProcessing;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.service.LocaleManager;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Util functions that can be used on field Formulas.
 */
@ApplicationScoped
public class Functions {

    private Logger log = LoggerFactory.getLogger(Functions.class);

    private static String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

    /**
     * This enables using StringUtils functions by using something like Functions.String.replace(...)
     */
    public static final StringUtils String = new StringUtils();

    /**
     * This enables using WordUtils functions by using something like Functions.String.replace(...)
     */
    public static final WordUtils Word = new WordUtils();

    public Functions() {
    }

    public Map getYearsBetween(int min, int max) throws Exception {
        if (max < min) throw new Exception("Error getting years bewtween " + min + " - " + max + ".");
        Map years = new TreeMap();

        int year = new GregorianCalendar().get(GregorianCalendar.YEAR);

        for (int i = year + min; i <= year + max; i++) {

            String value = java.lang.String.valueOf(i);
            years.put(value, value);
        }
        return years;
    }

    public Map getMonths() {
        Map months = new TreeMap();

        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.formProcessing.messages", LocaleManager.currentLocale());
        for (int i = 0; i < MONTHS.length; i++) {
            String key = java.lang.String.valueOf(i);
            if (key.length() == 1) key = "0" + key;
            months.put(key, bundle.getString("months." + MONTHS[i]));
        }

        return months;
    }

    public Map getValidDays() {
        return getValidDays(null);
    }

    public Map getValidDays(String value) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.DAY_OF_MONTH, 1);

        if (value == null || value.equals("") || value.startsWith("/")) {
            gc.set(GregorianCalendar.MONTH, 0);
        } else if (value.endsWith("/")) {
            int month = Integer.decode(value.substring(0, value.indexOf("/"))).intValue();
            gc.set(GregorianCalendar.MONTH, month);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            try {
                gc.setTime(sdf.parse(value));
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) + 1);
            } catch (Exception e) {
                log.warn("Error parsing date " + value + " : ", e);
            }
        }

        Map days = new TreeMap();

        int month = gc.get(GregorianCalendar.MONTH);
        while (gc.get(GregorianCalendar.MONTH) == month) {
            int intValue = gc.get(GregorianCalendar.DAY_OF_MONTH);
            String key = java.lang.String.valueOf(intValue);
            if (key.length() == 1) key = "0" + key;
            days.put(key, key);
            gc.set(GregorianCalendar.DAY_OF_MONTH, intValue + 1);
        }

        return days;
    }

    public Map getValidDays(String sMonth, String sYear) {

        int month = Integer.decode(sMonth).intValue();
        int year = Integer.decode(sYear).intValue();
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);

        Map days = new HashMap();

        while (gc.get(GregorianCalendar.MONTH) == month) {
            Integer value = new Integer(gc.get(GregorianCalendar.DAY_OF_MONTH));
            days.put(value, value.toString());
            gc.set(GregorianCalendar.DAY_OF_MONTH, value.intValue() + 1);
        }

        return days;
    }

    public Date getDateFromFields(String sDay, String sMonth, String sYear) {
        int day = Integer.decode(sDay).intValue();
        int month = Integer.decode(sMonth).intValue();
        int year = Integer.decode(sYear).intValue();
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        return gc.getTime();
    }


    /**
     * Return an empty string. This method, in combination with str(s) serve as default string inside formulas.
     *
     * @return an empty string
     */
    public String str() {
        return "";
    }

    /**
     * String given as argument
     *
     * @param s string to return
     * @return String given as argument
     */
    public String str(String s) {
        return s;
    }

}




