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
package org.jbpm.formModeler.service.bb.mvc.taglib.formatter;

import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.*;

public class FormatterTag extends BodyTagSupport {

    private static transient Logger log = LoggerFactory.getLogger(FormatterTag.class);

    public static final int STAGE_READING_PARAMS = 1;
    public static final int STAGE_RENDERING_FRAGMENTS = 2;

    private int currentStage;
    private Formatter formatter;
    private String currentEnabledFragment = "";
    private List processingInstructions = new ArrayList();
    private int currentProcessingInstruction = 0;

    protected Object name;
    protected HashMap params = new HashMap();
    protected HashMap fragmentParams = new HashMap();
    protected FormaterTagDynamicAttributesInterpreter formaterTagDynamicAttributesInterpreter = null;
    protected Set fragments = new HashSet();

    public void clearFragmentParams() {
        fragmentParams.clear();
    }

    public HashMap getFragmentParams() {
        return fragmentParams;
    }

    public FormaterTagDynamicAttributesInterpreter getFormaterTagDynamicAttributesInterpreter() {
        return formaterTagDynamicAttributesInterpreter;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public void setParam(String name, Object value) throws JspException {
        if (params.containsKey(name)) throw new JspException("Duplicated param name \"" + name + '"');
        params.put(name, value);
    }

    public void addFragment(String name) throws JspException {
        if (fragments.contains(name)) throw new JspException("Duplicated fragment name \"" + name + '"');
        fragments.add(name);
    }

    public String getCurrentEnabledFragment() {
        return currentEnabledFragment;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public final int doStartTag() throws JspException {
        currentStage = STAGE_READING_PARAMS;
        if (name instanceof Formatter) {
            formatter = (Formatter) name;
        } else {
            formatter = (Formatter) CDIBeanLocator.getBeanByNameOrType(String.valueOf(name));
        }
        if (formatter == null) {
            log.error("Unable to find formatter @Named " + name + " or through class name. ");
            return SKIP_BODY;
        }
        formatter.setTag(this);
        return EVAL_BODY_INCLUDE;
    }

    public int doAfterBody() throws JspException {
        Integer result = SKIP_BODY;
        try {
            // Generate the formatter's rendering instructions.
            if (currentStage == STAGE_READING_PARAMS) {
                currentStage = STAGE_RENDERING_FRAGMENTS;
                if (formatter != null) {
                    synchronized (formatter) {
                        formatter.service((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());
                    }
                }
            }

            // Process all the instructions until finish.
            while (currentProcessingInstruction < processingInstructions.size()) {
                ProcessingInstruction pi = (ProcessingInstruction) processingInstructions.get(currentProcessingInstruction++);
                if (log.isDebugEnabled()) log.debug("Processing instruction " + pi);

                switch (pi.getType()) {
                    case ProcessingInstruction.SET_ATTRIBUTE:
                        fragmentParams.put(pi.getName(), pi.getValue());
                        break;

                    case ProcessingInstruction.RENDER_FRAGMENT:
                        currentEnabledFragment = pi.getName();
                        result = EVAL_BODY_AGAIN;
                        return result;
                        //break;

                    case ProcessingInstruction.INCLUDE_PAGE:
                        for (Iterator it = fragmentParams.entrySet().iterator(); it.hasNext();) {
                            Map.Entry entry = (Map.Entry) it.next();
                            pageContext.getRequest().setAttribute((String) entry.getKey(), entry.getValue());
                        }
                        pageContext.include(pi.getName());
                        for (Iterator it = fragmentParams.entrySet().iterator(); it.hasNext();) {
                            Map.Entry entry = (Map.Entry) it.next();
                            pageContext.getRequest().removeAttribute((String) entry.getKey());
                        }
                        clearFragmentParams();
                        break;

                    case ProcessingInstruction.WRITE_OUT:
                        pageContext.getOut().print(pi.getValue());
                        clearFragmentParams();
                        break;

                    case ProcessingInstruction.SET_DYNAMIC_ATTRIBUTES_INTERPRETER:
                        formaterTagDynamicAttributesInterpreter = (FormaterTagDynamicAttributesInterpreter) pi.getValue();
                        break;
                }
            }
            // Do after rendering.
            if (!result.equals(EVAL_BODY_AGAIN)) formatter.afterRendering((HttpServletRequest) pageContext.getRequest(), (HttpServletResponse) pageContext.getResponse());

        } catch (Throwable e) {
            throw new JspException(e);
        }
        return result;
    }

    public int doEndTag() throws JspException {
        //clean up
        currentEnabledFragment = "";
        processingInstructions = new ArrayList();
        currentProcessingInstruction = 0;
        params = new HashMap();
        fragmentParams = new HashMap();
        fragments = new HashSet();
        return EVAL_PAGE;
    }

    public void addProcessingInstruction(ProcessingInstruction instruction) {
        processingInstructions.add(instruction);
    }

    public Object getParam(String name) {
        return params.get(name);
    }

    public void release() {
    }
}
