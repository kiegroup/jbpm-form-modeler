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

import org.slf4j.Logger;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class HandlerMarkupGenerator {

    public static HandlerMarkupGenerator lookup() {
        return (HandlerMarkupGenerator) CDIBeanLocator.getBeanByType(HandlerMarkupGenerator.class);
    }

    private Logger log = LoggerFactory.getLogger(HandlerMarkupGenerator.class);

    public String getMarkup(String bean, String property) {
        StringBuffer sb = new StringBuffer();
        sb.append(getHiddenMarkup(FactoryURL.PARAMETER_BEAN, bean));
        sb.append(getHiddenMarkup(FactoryURL.PARAMETER_PROPERTY, property));
        try {
            BeanHandler element = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(bean);
            element.setEnabledForActionHandling(true);
            Map<String, String> params = element.getExtraActionParams();
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    sb.append(getHiddenMarkup(key, params.get(key)));
                }
            }
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a BeanHandler.");
        }
        return sb.toString();
    }

    protected String getHiddenMarkup(String name, String value) {
        name = StringEscapeUtils.escapeHtml(name);
        value = StringEscapeUtils.escapeHtml(value);
        return "<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\">";
    }
}
