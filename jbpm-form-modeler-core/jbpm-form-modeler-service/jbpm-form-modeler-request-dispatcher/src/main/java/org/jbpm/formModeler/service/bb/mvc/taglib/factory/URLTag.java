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
package org.jbpm.formModeler.service.bb.mvc.taglib.factory;

import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import java.util.HashMap;
import java.util.Map;

public class URLTag extends GenericFactoryTag {
    private static transient Logger log = LoggerFactory.getLogger(URLTag.class.getName());

    private Map paramsMap = new HashMap();
    private boolean friendly = true;

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public int doStartTag() throws JspException {
        paramsMap.clear();
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        URLMarkupGenerator urlMarkupGenerator = URLMarkupGenerator.lookup();
        String markup = friendly ? urlMarkupGenerator.getMarkup(getBean(), getAction(), paramsMap) : urlMarkupGenerator.getPermanentLink(getBean(), getAction(), paramsMap) ;
        try {
            pageContext.getOut().print(markup);
        } catch (java.io.IOException ex) {
            log.error("Error: ", ex);
        }
        return EVAL_PAGE;
    }

    public void addParam(String name, Object value) {
        Object previousValue = paramsMap.put(name, value);
        if (previousValue != null) {
            log.warn("Overwritting value for parameter named " + name);
        }
    }
}
