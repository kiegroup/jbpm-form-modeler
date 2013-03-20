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
package org.jbpm.formModeler.core;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FieldTemplate extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FieldTemplate.class.getName());

    private String fieldName;
    private String type;
    private Map label = new HashMap();
    private Map title = new HashMap();
    private boolean fieldRequired;
    private boolean disabled;
    private boolean readonly;
    private String size;
    private String formula;
    private boolean groupWithPrevious;
    private String pattern;
    private long maxlength = -1;
    private String styleclass;
    private String cssStyle;
    private String height;
    private long tabindex = -1;
    private String accesskey;
    private String rangeFormula;

    private Properties htmlContent = new Properties();
    private String labelCSSClass;
    private String labelCSSStyle;
    private Map dynProps = new HashMap();

    public Map getDynProps() {
        return dynProps;
    }

    public void setDynProps(Map dynProps) {
        this.dynProps = dynProps;
    }

    public Properties getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(Properties htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getLabelCSSClass() {
        return labelCSSClass;
    }

    public void setLabelCSSClass(String labelCSSClass) {
        this.labelCSSClass = labelCSSClass;
    }

    public String getLabelCSSStyle() {
        return labelCSSStyle;
    }

    public void setLabelCSSStyle(String labelCSSStyle) {
        this.labelCSSStyle = labelCSSStyle;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean getFieldRequired() {
        return fieldRequired;
    }

    public void setFieldRequired(boolean fieldRequired) {
        this.fieldRequired = fieldRequired;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean getGroupWithPrevious() {
        return groupWithPrevious;
    }

    public void setGroupWithPrevious(boolean groupWithPrevious) {
        this.groupWithPrevious = groupWithPrevious;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Map getLabel() {
        return label;
    }

    public void setLabel(Map label) {
        this.label = label;
    }

    public long getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(long maxlength) {
        this.maxlength = maxlength;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRangeFormula() {
        return rangeFormula;
    }

    public void setRangeFormula(String rangeFormula) {
        this.rangeFormula = rangeFormula;
    }

    public boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStyleclass() {
        return styleclass;
    }

    public void setStyleclass(String styleclass) {
        this.styleclass = styleclass;
    }

    public long getTabindex() {
        return tabindex;
    }

    public void setTabindex(long tabindex) {
        this.tabindex = tabindex;
    }

    public Map getTitle() {
        return title;
    }

    public void setTitle(Map title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
