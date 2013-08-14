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
package org.jbpm.formModeler.service.bb.mvc.taglib.factory;

import org.jbpm.formModeler.service.bb.mvc.components.handling.UIBeanHandler;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

public class UseComponentTag extends GenericFactoryTag {

    private static transient Logger log = LoggerFactory.getLogger(UseComponentTag.class.getName());

    public static final String COMPONENT_ATTR_NAME = "currentComponentBeingRendered";

    public int doEndTag() throws JspTagException {
        Object bean = CDIBeanLocator.getBeanByNameOrType(getBean());
        if (bean != null) {
            if (bean instanceof UIBeanHandler) {
                String page = ((UIBeanHandler) bean).getBeanJSP();
                if (page == null) log.error("Page for component " + getBean() + " is null.");
                Object previousComponent = pageContext.getRequest().getAttribute(COMPONENT_ATTR_NAME);
                try {
                    ((UIBeanHandler) bean).beforeRenderBean();
                    pageContext.getRequest().setAttribute(COMPONENT_ATTR_NAME, bean);
                    pageContext.include(page);
                    pageContext.getRequest().setAttribute(COMPONENT_ATTR_NAME, previousComponent);
                    ((UIBeanHandler) bean).afterRenderBean();
                } catch (Exception e) {
                    throw new JspTagException("Error rendering UI bean '" + getBean() + "'", e);
                } finally {
                    pageContext.getRequest().setAttribute(COMPONENT_ATTR_NAME, previousComponent);
                }
            } else {
                log.error("Bean " + getBean() + " is not a UIBeanHandler");
            }
        } else {
            log.error("Bean " + getBean() + " is null.");
        }
        return EVAL_PAGE;
    }

    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }
}
