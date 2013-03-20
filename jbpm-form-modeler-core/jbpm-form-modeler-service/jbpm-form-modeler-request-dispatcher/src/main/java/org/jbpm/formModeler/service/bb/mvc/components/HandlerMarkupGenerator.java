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
package org.jbpm.formModeler.service.bb.mvc.components;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.components.handling.HandlerFactoryElement;
import org.apache.commons.lang.StringEscapeUtils;

public class HandlerMarkupGenerator extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HandlerMarkupGenerator.class.getName());

    public String getMarkup(String bean, String property) {
        StringBuffer sb = new StringBuffer();
        String alias = Factory.getAlias(bean);
        sb.append(getHiddenMarkup(FactoryURL.PARAMETER_BEAN, alias != null ? alias : bean));
        sb.append(getHiddenMarkup(FactoryURL.PARAMETER_PROPERTY, property));
        try {
            HandlerFactoryElement element = (HandlerFactoryElement) Factory.lookup(bean);
            element.setEnabledForActionHandling(true);
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a HandlerFactoryElement.");
        }        
        return sb.toString();
    }

    protected String getHiddenMarkup(String name, String value) {
        name = StringEscapeUtils.escapeHtml(name);
        value = StringEscapeUtils.escapeHtml(value);
        return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\">";
    }

}
