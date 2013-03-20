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
package org.jbpm.formModeler.core.processing.formDecorators;


import org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler;

public class FreeText extends InputTextFieldHandler {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FreeText.class.getName());

    private String pageToIncludeForRendering = "/formModeler/formDecorators/FreeText/input.jsp";
    private String pageToIncludeForDisplaying = "/formModeler/formDecorators/FreeText/show.jsp";
    private String pageToIncludeForSearching = "/formModeler/formDecorators/FreeText/search.jsp";

    public String getPageToIncludeForDisplaying() {
        return pageToIncludeForDisplaying;
    }

    public void setPageToIncludeForDisplaying(String pageToIncludeForDisplaying) {
        this.pageToIncludeForDisplaying = pageToIncludeForDisplaying;
    }

    public String getPageToIncludeForRendering() {
        return pageToIncludeForRendering;
    }

    public void setPageToIncludeForRendering(String pageToIncludeForRendering) {
        this.pageToIncludeForRendering = pageToIncludeForRendering;
    }

    public String getPageToIncludeForSearching() {
        return pageToIncludeForSearching;
    }

    public void setPageToIncludeForSearching(String pageToIncludeForSearching) {
        this.pageToIncludeForSearching = pageToIncludeForSearching;
    }

}
