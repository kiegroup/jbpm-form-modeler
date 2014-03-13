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
package org.jbpm.formModeler.api.model;

import org.jbpm.formModeler.api.client.FormRenderContext;

import java.io.Serializable;
import java.util.Set;

public interface DataHolder extends Comparable, Serializable {
    String getUniqeId();
    String getInputId();
    String getOuputId();

    void setInputId(String inputId);
    void setOutputId(String outputId);

    String getSupportedType();
    void setSupportedType(String type);

    boolean canHaveChildren();

    Object createInstance(FormRenderContext context) throws Exception;
    void writeValue(Object destination, String propName, Object value) throws Exception;
    Object readFromBindingExperssion(Object source, String bindingExpression) throws Exception;
    Object readValue(Object source, String propName) throws Exception;
    Set<DataFieldHolder> getFieldHolders();
    DataFieldHolder getDataFieldHolderById(String id);
    String getTypeCode();
    String getInfo();
    void setRenderColor(String renderColor);
    String getRenderColor();

    String getInputBinding(String fieldName);
    String getOuputBinding(String fieldName);

    boolean containsInputBinding(String bindingString);
    boolean containsOutputBinding(String bindingString);
    boolean containsBinding(String bindingString);
    boolean isAssignableValue(Object value);
    boolean isAssignableForField(Field field);
}
