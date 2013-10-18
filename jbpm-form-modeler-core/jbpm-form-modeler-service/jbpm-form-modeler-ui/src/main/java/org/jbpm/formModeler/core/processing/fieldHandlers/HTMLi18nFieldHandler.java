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

import org.jbpm.formModeler.core.processing.DefaultFieldHandler;
import org.jbpm.formModeler.core.wrappers.HTMLi18n;
import org.jbpm.formModeler.api.model.wrappers.I18nEntry;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.service.LocaleManager;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handler for text area with HTML i18n
 */
@ApplicationScoped
public class HTMLi18nFieldHandler extends DefaultFieldHandler {

    public static final String DIV_INPUT_NAME_PREFFIX = "Div__";

    public String[] getCompatibleClassNames() {
        return new String[]{HTMLi18n.class.getName()};
    }

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        HTMLi18n set = new HTMLi18n();

        String[] locales = LocaleManager.lookup().getInstalledLocaleIds();
        String[] value = (String[]) parametersMap.get(inputName);
        String defaultValue ="";
        if(value!=null){
            defaultValue =value[0];
        }

        for (String locale : locales) { //we put the same value to every locale because the jsp hasn't locale selector
            set.setValue(locale, defaultValue);
        }

        return set.isEmpty() ? null : set;
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
        HTMLi18n value = (HTMLi18n) objectValue;
        for (Iterator it = value.iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            m.put(inputName + "_" + entry.getLang(), new String[]{(String) entry.getValue()});
        }
        m.put(inputName, new String[]{});
        return m;
    }

    public boolean isEmpty(Object value) {
        if (value == null || ((HTMLi18n) value).isEmpty())
            return true;
        for (Iterator it = ((HTMLi18n) value).iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            if (entry.getValue() != null && !"".equals(entry.getValue()))
                return false;
        }
        return true;
    }
}