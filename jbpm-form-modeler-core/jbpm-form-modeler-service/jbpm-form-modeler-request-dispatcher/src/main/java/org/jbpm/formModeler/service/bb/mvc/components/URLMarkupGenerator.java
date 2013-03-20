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
import org.jbpm.formModeler.service.bb.mvc.Framework;
import org.jbpm.formModeler.service.bb.mvc.components.handling.HandlerFactoryElement;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class URLMarkupGenerator extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(URLMarkupGenerator.class.getName());

    private String handler = "factory";
    private String action = "set";
    public static final String COMMAND_RUNNER = "Controller";
    private Framework framework;

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get a permanent link to a given action on a bean
     *
     * @param bean     Factory component that will perform the action
     * @param property Component's property (method) that will be invoked
     * @param params   Extra parameters for link
     * @return a link url to a factory component action, independent on the page
     */
    public String getPermanentLink(String bean, String property, Map params) {
        String base = /*this.getBasePath();
        while (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        base = base + "/" +*/ COMMAND_RUNNER;
        StringBuffer sb = new StringBuffer();
        sb.append(base).append("?");
        String alias = Factory.getAlias(bean);
        //HandlerFactoryElement _component = (HandlerFactoryElement) Factory.lookup(bean);
        params.put(FactoryURL.PARAMETER_BEAN, alias != null ? alias : bean);
        params.put(FactoryURL.PARAMETER_PROPERTY, property);
        sb.append(getParamsMarkup(params));
        try {
            HandlerFactoryElement element = (HandlerFactoryElement) Factory.lookup(bean);
            if (element != null) element.setEnabledForActionHandling(true);
            else log.debug("Bean '" + bean + "' not found on factory"); 
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a HandlerFactoryElement.");
        }
        return sb.toString();
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
     * Get a link to executing an action on a bean
     *
     * @param bean
     * @param property
     * @param params
     * @return
     */
    public String getMarkup(String bean, String property, Map params) {
        StringBuffer sb = new StringBuffer();
        sb.append(getServletMapping()).append("?");
        String alias = Factory.getAlias(bean);
        HandlerFactoryElement component = (HandlerFactoryElement) Factory.lookup(bean);
        params.put(FactoryURL.PARAMETER_BEAN, alias != null ? alias : bean);
        params.put(FactoryURL.PARAMETER_PROPERTY, component.getActionName(property));
        sb.append(getParamsMarkup(params));
        try {
            HandlerFactoryElement element = (HandlerFactoryElement) Factory.lookup(bean);
            element.setEnabledForActionHandling(true);
        } catch (ClassCastException cce) {
            log.error("Bean " + bean + " is not a HandlerFactoryElement.");
        }
        return sb.toString();
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
            sb.append(URLEncoder.encode(name, framework.getFrameworkEncoding())).append("=").append(URLEncoder.encode(String.valueOf(value), framework.getFrameworkEncoding()));
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
        if (request.getServerPort() != 80)
            sb.append(":").append(request.getServerPort());
        return sb.toString();
    }
}