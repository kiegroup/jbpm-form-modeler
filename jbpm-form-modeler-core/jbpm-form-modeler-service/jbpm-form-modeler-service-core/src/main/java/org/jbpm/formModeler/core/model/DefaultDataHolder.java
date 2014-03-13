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
package org.jbpm.formModeler.core.model;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.util.BindingExpressionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class DefaultDataHolder implements DataHolder {
    protected String renderColor;
    
    protected String uniqueId;

    protected String supportedType;

    protected BindingExpressionUtil bindingExpressionUtil = BindingExpressionUtil.getInstance();

    @Override
    public String getSupportedType() {
        return supportedType;
    }

    @Override
    public void setSupportedType(String supportedType) {
        this.supportedType = supportedType;
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }

    public String getUniqeId() {
        //return StringUtils.defaultString(getInputId()) + "/" + StringUtils.defaultString(getOuputId());
        return uniqueId;
    }

    @Override
    public String getRenderColor() {
        return renderColor;
    }

    @Override
    public void setRenderColor(String renderColor) {
        this.renderColor = renderColor;
    }

    @Override
    public String getInputBinding(String fieldName) {
        if (StringUtils.isEmpty(getInputId()) || StringUtils.isEmpty(fieldName)) return "";
        return bindingExpressionUtil.generateBindingExpression(getInputId(), fieldName);
        //return "{" + getInputId() + "/" + fieldName+"}" ;
    }

    @Override
    public String getOuputBinding(String fieldName) {
        if (StringUtils.isEmpty(getOuputId()) || StringUtils.isEmpty(fieldName)) return "";
        return bindingExpressionUtil.generateBindingExpression(getOuputId(), fieldName);
        //return "{" + getOuputId() + "/" + fieldName+"}" ;
    }

    @Override
    public boolean containsInputBinding(String bindingString) {
        return containsBinding(bindingString, getInputId());
    }

    @Override
    public boolean containsOutputBinding(String bindingString) {
        return containsBinding(bindingString, getOuputId());
    }

    @Override
    public boolean containsBinding(String bindingString) {
        return containsBinding(bindingString, getInputId()) || containsBinding(bindingString, getOuputId());
    }

    protected boolean containsBinding(String bindingString, String id) {
        if (StringUtils.isEmpty(bindingString) || StringUtils.isEmpty(id)) return false;

        String rawbinding = bindingExpressionUtil.extractBindingExpression(bindingString);

        String[] parts = rawbinding.split("/");

        if (parts == null || parts.length != 2 || StringUtils.isEmpty(parts[0])) return false;

        return id.equals(parts[0]);
    }

    @Override
    public boolean isAssignableForField(Field field) {
        if (field == null || field.getInputBinding() == null || field.getOutputBinding() == null) return false;
        return (containsBinding(field.getInputBinding()) || containsBinding(field.getOutputBinding()));
    }

    public int compareTo(Object o) {
        return getUniqeId().compareTo(((DataHolder) o).getUniqeId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof DataHolder)) return false;

        DataHolder holder = (DataHolder) obj;

        return (holder.getUniqeId().equals(getUniqeId()));
    }
}
