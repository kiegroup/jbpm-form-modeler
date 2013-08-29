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
package org.jbpm.formModeler.api.model.wrappers;

import java.io.Serializable;
import java.util.*;

/**
 *  Wrapper class for Multilanguage Strings
 */
public class I18nSet extends AbstractMap implements Serializable, Comparable {

    // Set of I18nEntry objects
    private TreeSet entrySet = new TreeSet(new I18nEntryComparator());

    public I18nSet() {
        super();
    }

    public Set entrySet() {
        return entrySet;
    }

    public I18nSet(Set s) {
        entrySet.addAll(s == null ? Collections.EMPTY_SET : s);
    }

    public I18nSet(Map m) {
        super();
        for (Iterator it = m.keySet().iterator(); it.hasNext();) {
            final String lang = (String) it.next();
            final String value = (String) m.get(lang);
            entrySet.add(new I18nEntryImpl(lang, value));
        }
    }


    public boolean add(Object o) {
        if (o == null || o instanceof I18nEntry)
            return entrySet.add(o);
        return false;
    }

    public String getValue(String lang) {
        for (Iterator it = entrySet.iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            if (lang.equals(entry.getLang())) {
                return (String) entry.getValue();
            }
        }
        return null;
    }

    public void setValue(final String lang, final String value) {
        for (Iterator it = entrySet.iterator(); it.hasNext();) {
            I18nEntry entry = (I18nEntry) it.next();
            if (lang.equals(entry.getLang())) {
                it.remove();
            }
        }
        add(new I18nEntryImpl(lang, value));
    }

    public int compareTo(Object o) {
        if (o == null) return -1;
        I18nSet other = (I18nSet) o;

        for (Iterator it = keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            String value = getValue(key);
            String otherValue = other.getValue(key);

            int result = value.compareTo(otherValue);

            if (result!= 0) return result;
        }

        return 0;
    }

    /**
     * @return this object
     * @deprecated
     */
    public Map asMap() {
        return this;
    }

    public Iterator iterator() {
        return entrySet.iterator();
    }
}
class I18nEntryComparator implements Comparator<I18nEntry>, Serializable {
    @Override
    public int compare(I18nEntry o1, I18nEntry o2) {
        return o1.getLang().compareTo(o2.getLang());
    }
}
