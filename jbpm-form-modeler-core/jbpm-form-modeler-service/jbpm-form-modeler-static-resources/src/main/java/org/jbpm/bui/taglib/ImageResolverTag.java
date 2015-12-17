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
package org.jbpm.bui.taglib;

import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.jbpm.formModeler.service.bb.mvc.taglib.ContextTag;
import org.jbpm.bui.resources.StaticResourceResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class ImageResolverTag extends BodyTagSupport {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(ImageResolverTag.class);

    private String imageURL;

    /**
     * Static image relativePath
     */
    private String relativePath;

    @Override
    public int doStartTag() throws JspException {
        if (!StringUtils.isEmpty(relativePath)) {
            imageURL = getImageResolver().getImagePath(relativePath);
            if (!StringUtils.isEmpty(imageURL)) {
                imageURL = ContextTag.getContextPath(imageURL, (HttpServletRequest)pageContext.getRequest());
            }
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        try {

            if (StringUtils.isEmpty(imageURL)) {
                if (log.isDebugEnabled()) log.debug("imageURL is null . Clearing content.");
                if (super.bodyContent != null) {
                    imageURL = super.bodyContent.getString();
                    super.bodyContent.clear();
                }
            } else {
                if (log.isDebugEnabled()) log.debug("imageURL = " + imageURL + ". ");
                if (super.id != null) {
                    if (log.isDebugEnabled()) log.debug("Setting " + super.id + " to " + imageURL);
                    super.pageContext.setAttribute(super.id, imageURL, PageContext.PAGE_SCOPE);
                    return SKIP_BODY;
                } else {
                    if (log.isDebugEnabled()) log.debug("Printing imageURL to " + imageURL);
                    super.pageContext.getOut().print(imageURL);
                }
            }
        } catch (Exception ex) {
            log.error("Error building imageURL: ", ex);
            throw new JspException("Exception ", ex);
        }
        return EVAL_PAGE;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public StaticResourceResolver getImageResolver() {
        return (StaticResourceResolver) CDIBeanLocator.getBeanByType(StaticResourceResolver.class);
    }

    public static class TEI extends TagExtraInfo {
        public VariableInfo[] getVariableInfo(TagData data) {
            String varName = data.getId();
            if (varName == null)
                return new VariableInfo[0];
            else
                return (new VariableInfo[]{
                        new VariableInfo(varName, "java.lang.String", true, VariableInfo.AT_END)
                });
        }
    }
}
