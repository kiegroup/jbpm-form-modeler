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

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspTagException;

import org.jbpm.formModeler.service.bb.mvc.components.FactoryUniqueIdEncoder;
import org.jbpm.formModeler.service.bb.mvc.components.handling.UIBeanHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeTag extends TagSupport {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(EncodeTag.class);

    /**
     * Text to encode
     */
    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int doEndTag() throws JspTagException {
        UIBeanHandler uiBean = (UIBeanHandler) pageContext.getRequest().getAttribute(UseComponentTag.COMPONENT_ATTR_NAME);
        String encodedName = FactoryUniqueIdEncoder.lookup().encode(uiBean, name);
        try {
            pageContext.getOut().print(encodedName);
        } catch (Exception ex) {
            log.error("Error encoding name [" + name + "]");
        }
        return SKIP_BODY;
    }
}
