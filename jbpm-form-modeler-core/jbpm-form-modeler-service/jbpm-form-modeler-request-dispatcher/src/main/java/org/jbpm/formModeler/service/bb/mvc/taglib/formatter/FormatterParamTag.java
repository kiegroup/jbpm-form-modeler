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
package org.jbpm.formModeler.service.bb.mvc.taglib.formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 */
public class FormatterParamTag extends TagSupport {
    private static transient Logger log = LoggerFactory.getLogger(FormatterParamTag.class);

    protected String name;
    protected Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public final int doStartTag() {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        if (!(getParent() instanceof FormatterTag))
            throw new JspException("Wrong nesting: formatterParam named " + name + " must be inside a formatter.");
        FormatterTag parent = (FormatterTag) getParent();
        if (parent.getCurrentStage() == FormatterTag.STAGE_READING_PARAMS)
            parent.setParam(name, value);
        return EVAL_PAGE;
    }

}
