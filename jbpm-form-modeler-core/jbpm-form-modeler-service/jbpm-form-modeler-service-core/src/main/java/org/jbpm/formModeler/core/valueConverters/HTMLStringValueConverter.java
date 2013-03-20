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

import org.jbpm.formModeler.core.wrappers.HTMLString;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HTMLStringValueConverter extends DefaultValueConverter implements ValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HTMLStringValueConverter.class.getName());

    public Class getAcceptedClass() {
        return HTMLString.class;
    }

    public List writeValue(Object value) {
        if (value == null) return Collections.EMPTY_LIST;
        ArrayList l = new ArrayList();
        DynValue val = new DynValue();
        val.setStringValue(((HTMLString) value).getValue());
        l.add(val);
        return l;
    }

    public Object readValue(List values) {
        if (values == null || values.isEmpty())
            return null;
        return new HTMLString(((DynValue) values.get(0)).getStringValue());
    }

    public String getTextValue(Object value) {
        return ((HTMLString) value).getValue();
    }

    public Object readTextValue(String textValue) {
        return new HTMLString(textValue);
    }

    public String flatValue(Object value, String lang) {
        String htmlText = ((HTMLString) value).getValue();
        try {
            Reader reader = new StringReader(htmlText);
            StringWriter sb = new StringWriter();
            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                sb.write(buffer, 0, length);
            }
            reader.close();
            String s = sb.toString();
            sb.close();
            return s;
        } catch (IOException e) {
            log.warn("Error: ", e);
        }
        return null;
    }

    public boolean isTokenizable() {
        return true;
    }
}
