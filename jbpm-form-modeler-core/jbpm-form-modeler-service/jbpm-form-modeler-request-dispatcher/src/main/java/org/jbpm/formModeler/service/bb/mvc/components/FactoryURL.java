/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.formModeler.service.bb.mvc.components;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;

public class FactoryURL {

    public static final String SCHEMA = "factory";
    public static final String NAME_FORMAT = SCHEMA + "://" + "{0}" + "/" + "{1}";
    protected static final MessageFormat msgf = new MessageFormat(NAME_FORMAT);

    public static final String PARAMETER_BEAN = "_fb";
    public static final String PARAMETER_PROPERTY = "_fp";
    public static final String DISPATCH_ACTION = "pAction";

    private String componentName;
    private String propertyName;

    public FactoryURL(String componentName, String propertyName) {
        this.componentName = componentName;
        this.propertyName = propertyName;
    }

    public static FactoryURL getURL(String value) throws ParseException {
        ParsePosition pPos = new ParsePosition(0);
        Object[] o = msgf.parse(value, pPos);
        if (o == null)
            throw new ParseException("Cannot parse " + value + ". Error at position " + pPos.getErrorIndex(), pPos.getErrorIndex());
        String componentName = StringEscapeUtils.unescapeHtml4((String) o[0]);
        String propertyName = StringEscapeUtils.unescapeHtml4((String) o[1]);
        return new FactoryURL(componentName, propertyName);
    }

    public String getComponentName() {
        return componentName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(SCHEMA);
        sb.append("://");
        sb.append(StringEscapeUtils.escapeHtml4(componentName));
        sb.append("/");
        sb.append(StringEscapeUtils.escapeHtml4(propertyName));
        return sb.toString();
    }
}
