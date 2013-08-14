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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;

public class CurrentComponentTag extends GenericFactoryTag {
    private static transient Logger log = LoggerFactory.getLogger(CurrentComponentTag.class);

    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doEndTag() throws JspTagException {
        UIBeanHandler currentComponent = (UIBeanHandler)pageContext.getRequest().getAttribute(UseComponentTag.COMPONENT_ATTR_NAME);
        try {
            String componentClass = currentComponent.getBeanName();
            pageContext.getOut().print(componentClass != null ? componentClass : "");
        } catch (java.io.IOException ex) {
            log.error("Error: ", ex);
        }
        return EVAL_PAGE;
    }


    /**
     * @see javax.servlet.jsp.tagext.TagSupport
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }
}