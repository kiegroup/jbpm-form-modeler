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

import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.api.model.i18n.I18nEntry;
import org.jbpm.formModeler.api.model.i18n.I18nEntryImpl;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 *
 */
public class I18nSetValueConverter extends DefaultValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(I18nSetValueConverter.class.getName());

    /**
     * Get the class accepted by this converter
     *
     * @return Accepted class
     */
    public Class getAcceptedClass() {
        return I18nSet.class;
    }

    /**
     * Given a value, whose class must be the one accepted by the converter,
     * convert it to a list of DynValue objects.
     *
     * @param value Value to convert
     * @return a List representation for this value
     */
    public List writeValue(Object value) {
        if (value == null) return Collections.EMPTY_LIST;
        I18nSet set = (I18nSet) value;
        ArrayList l = new ArrayList();
        for (Iterator it = set.iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            String key = entry.getLang();
            String val = String.valueOf(entry.getValue());
            DynValue pdval = new DynValue();
            pdval.setSelector(key);
            pdval.setStringValue(val);
            l.add(pdval);
        }
        return l;
    }

    /**
     * Given a list of DynValue objects, convert it to a Object of accepted
     * class.
     *
     * @param values Values to convert
     * @return Object read from values
     */
    public Object readValue(List values) {
        if (values == null)
            return null;
        if (values.isEmpty())
            return new I18nSet();
        I18nSet valToReturn = new I18nSet();

        for (int i = 0; i < values.size(); i++) {
            DynValue dynValue = (DynValue) values.get(i);
            String selector = dynValue.getSelector();
            valToReturn.setValue(selector, dynValue.getStringValue());
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
        I18nSet svalue = (I18nSet) value;
        LocaleManager localeManager = (LocaleManager) factoryLookup("org.jbpm.formModeler.service.LocaleManager");
        String[] langs = localeManager.getPlatformAvailableLangs(); //Write values in deterministic order
        Arrays.sort(langs);
        for (int i = 0; i < langs.length; i++) {
            String lang = langs[i];
            if (svalue.containsKey(lang)) {
                String val = svalue.getValue(lang);
                values.add(lang);
                values.add(val);
            }
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
        I18nSet set = new I18nSet();
        for (int i = 0; i < s.length - 1; i++) {
            final String lang = s[i];
            final String value = s[++i];
            set.add(new I18nEntryImpl(lang, value));
        }
        return set;
    }

    /**
     * Convert a value of accepted class into a flat String value
     *
     * @param value
     * @return a String representation for this object to be used in search indexes
     */
    public String flatValue(Object value, String lang) {
        String result = ((I18nSet) value).getValue(lang);
        if (result == null) {
            LocaleManager localeManager = (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");
            result = StringUtils.defaultString(((I18nSet) value).getValue(localeManager.getDefaultLang()));
        }
        return result;
    }

    public boolean isTokenizable() {
        return true;
    }
}
