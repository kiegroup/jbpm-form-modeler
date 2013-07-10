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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class DefaultDataHolder implements DataHolder {
    protected String renderColor;

    public String getUniqeId() {
        return StringUtils.defaultString(getInputId()) + "/" + StringUtils.defaultString(getOuputId());
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
        return "{" + getInputId() + "/" + fieldName+"}" ;
    }

    @Override
    public String getOuputBinding(String fieldName) {
        if (StringUtils.isEmpty(getOuputId()) || StringUtils.isEmpty(fieldName)) return "";
        return "{" + getOuputId() + "/" + fieldName+"}" ;
    }

    @Override
    public boolean containsBinding(String bindingString) {
        if (StringUtils.isEmpty(bindingString)) return false;

        String rawbingind = bindingString.substring(1, bindingString.length() - 1);

        String[] parts = rawbingind.split("/");

        if (parts == null || parts.length != 2 || StringUtils.isEmpty(parts[0])) return false;

        if (getInputId().equals(parts[0]) || getOuputId().equals(parts[0])) {
            return getDataFieldHolderById(parts[1]) != null;
        }

        return false;
    }

    @Override
    public boolean isAssignableForField(Field field) {
        if (field == null || field.getInputBinding() == null || field.getOutputBinding() == null) return false;

        return (containsBinding(field.getInputBinding()) || containsBinding(field.getOutputBinding()));
    }
}
