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
import org.jbpm.formModeler.service.bb.mvc.controller.HTTPSettings;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

@ApplicationScoped
public class URLMarkupGenerator {

    public static URLMarkupGenerator lookup() {
        return (URLMarkupGenerator) CDIBeanLocator.getBeanByType(URLMarkupGenerator.class);
    }

    private Logger log = LoggerFactory.getLogger(URLMarkupGenerator.class);

    public static final String COMMAND_RUNNER = "Controller";

    /**
     * Get a permanent link to a given action on a bean
     *
     * @param bean   Bean handler that will perform the action
     * @param action Bean's method that will be invoked
     * @param params Extra parameters for link
     * @return A link url to a bean action, independent on the page.
     */
    public String getPermanentLink(String bean, String action, Map params) {
        try {
            StringBuffer sb = new StringBuffer();
            String base = COMMAND_RUNNER;
            sb.append(base).append("?");
            params.put(FactoryURL.PARAMETER_BEAN, bean);
            params.put(FactoryURL.PARAMETER_PROPERTY, action);

            BeanHandler component = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(bean);
            if (component != null) {
                component.setEnabledForActionHandling(true);
                if (component.getExtraActionParams() != null) params.putAll(component.getExtraActionParams());
            }
            else log.debug("Bean @Named as '" + bean + "' not found.");

            sb.append(getParamsMarkup(params));

            return sb.toString();
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a BeanHandler.");
            return "#";
        }
    }

    /**
     * Get the base URI for any markup, that is, the base path plus the servlet mapping. Any uri
     * constructed on top of it will go to the Controller servlet
     *
     * @return the base URI for any markup
     */
    public String getServletMapping() {
        return COMMAND_RUNNER;
    }

    /**
     * Mapping to the controller servlet. By default it is Controller, but in case there is friendly url, can be
     * replaced by workspace/&lt;friendly_url&gt; It is NOT preceded by "/"
     *
     * @return Mapping to the controller servlet.
     */
    public String getBaseURI() {
        return getServletMapping();
    }

    /**
     * Get a link to executing an action on a bean.
     */
    public String getMarkup(String bean, String action, Map params) {
        try {
            StringBuffer sb = new StringBuffer();
            BeanHandler component = (BeanHandler) CDIBeanLocator.getBeanByNameOrType(bean);
            params.put(FactoryURL.PARAMETER_BEAN, bean);
            params.put(FactoryURL.PARAMETER_PROPERTY, component.getActionName(action));
            sb.append(getServletMapping()).append("?");
            if (component.getExtraActionParams() != null) params.putAll(component.getExtraActionParams());
            sb.append(getParamsMarkup(params));
            component.setEnabledForActionHandling(true);
            return sb.toString();
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a BeanHandler.");
            return "#";
        }
    }

    /**
     * Convert a parameter map (string->string) to its URL form name1=value1&name2=value2 ...
     *
     * @param params parameter map to process.
     * @return A String representation for the received parameter map
     */
    public String getParamsMarkup(Map params) {
        StringBuffer sb = new StringBuffer();
        for (Iterator it = params.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            Object paramValue = params.get(paramName);
            sb.append(getParameterMarkup(paramName, paramValue));
            if (it.hasNext())
                sb.append("&amp;");
        }
        return sb.toString();
    }

    protected String getParameterMarkup(String name, Object value) {
        StringBuffer sb = new StringBuffer();
        try {
            HTTPSettings httpSettings = HTTPSettings.lookup();
            sb.append(URLEncoder.encode(name, httpSettings.getEncoding())).append("=").append(URLEncoder.encode(String.valueOf(value), httpSettings.getEncoding()));
        } catch (UnsupportedEncodingException e) {
            log.error("Error: ", e);
        }
        return sb.toString();
    }

    public String getContextHost(ServletRequest request) {
        StringBuffer sb = new StringBuffer();
        String context = ((HttpServletRequest) request).getContextPath();
        String protocol = request.getScheme();
        while (context.startsWith("/")) context = context.substring(1);
        sb.append(protocol.toLowerCase()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80) sb.append(":").append(request.getServerPort());
        return sb.toString();
    }
}