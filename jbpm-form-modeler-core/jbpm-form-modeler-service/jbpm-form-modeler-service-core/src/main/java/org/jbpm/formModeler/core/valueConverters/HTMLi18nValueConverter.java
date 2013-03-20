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

import org.jbpm.formModeler.core.wrappers.HTMLi18n;
import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.api.model.i18n.I18nEntry;
import org.jbpm.formModeler.api.model.i18n.I18nEntryImpl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class HTMLi18nValueConverter extends DefaultValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HTMLi18nValueConverter.class.getName());

    public Class getAcceptedClass() {
        return HTMLi18n.class;
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
        HTMLi18n set = (HTMLi18n) value;
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
            return new HTMLi18n();
        HTMLi18n valToReturn = new HTMLi18n();

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
        HTMLi18n svalue = (HTMLi18n) value;
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
        HTMLi18n set = new HTMLi18n();
        for (int i = 0; i < s.length - 1; i++) {
            final String lang = s[i++];
            final String value = s[i];
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
        String htmlText = ((HTMLi18n) value).getValue(lang);
        htmlText = htmlText == null ? "" : htmlText;
        try {
            Reader reader = new StringReader(htmlText);
            StringWriter sb = new StringWriter();
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                sb.write(buffer, 0, length);
            }
            return sb.toString();
        } catch (IOException e) {
            log.warn("Error: ", e);
        }
        return null;
    }

    public boolean isTokenizable() {
        return true;
    }
}

