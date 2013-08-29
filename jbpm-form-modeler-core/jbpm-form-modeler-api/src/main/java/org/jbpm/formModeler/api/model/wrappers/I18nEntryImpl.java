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
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of I18nEntry
 */
public class I18nEntryImpl extends AbstractMap implements Serializable, I18nEntry {

    private String lng;
    private Object val;

    public I18nEntryImpl(String lng, Object val) {
        this.lng = lng;
        this.val = val;
    }

    public String getLang() {
        return lng;
    }

    public Object getKey() {
        return getLang();
    }

    public Object getValue() {
        return val;
    }

    public void setLang(String s) {
        lng = s;
    }


    public Object setValue(Object s) {
        Object oldVal = val;
        val = s;
        return oldVal;
    }

    public Set entrySet() {
        Set s = new HashSet();
        s.add(new Entry() {
            public Object getKey() {
                return "lang";
            }

            public Object getValue() {
                return getLang();
            }

            public Object setValue(Object value) {
                String oldValue = getLang();
                setLang((String) value);
                return oldValue;
            }
        });
        s.add(new Entry() {
            public Object getKey() {
                return "value";
            }

            public Object getValue() {
                return I18nEntryImpl.this.getValue();
            }

            public Object setValue(Object value) {
                Object oldValue = I18nEntryImpl.this.getValue();
                I18nEntryImpl.this.setValue(value);
                return oldValue;
            }
        });
        return s;
    }

}
