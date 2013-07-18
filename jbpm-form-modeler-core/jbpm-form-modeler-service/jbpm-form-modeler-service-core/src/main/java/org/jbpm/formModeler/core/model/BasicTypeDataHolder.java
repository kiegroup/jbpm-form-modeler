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
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class BasicTypeDataHolder extends DefaultDataHolder  {
    private String inputId;
    private String outputId;
    private FieldType basicFieldType;

    protected FieldTypeManager fieldTypeManager;

    protected Set<DataFieldHolder> dataFieldHolders;


    public Object createInstance(FormRenderContext context) throws Exception {
        String className = basicFieldType.getFieldClass();
        return Class.forName(className).newInstance();
    }

    public BasicTypeDataHolder(){
    }

    public BasicTypeDataHolder(String inputId, String outputId, String className, String renderColor) {
        this.inputId = inputId;
        this.outputId = outputId;
        fieldTypeManager = (FieldTypeManager) CDIBeanLocator.getBeanByType(FieldTypeManager.class);

        try{
            this.basicFieldType = fieldTypeManager.getTypeByClass(className);
        }catch (Exception e){

        }

        setRenderColor(renderColor);
    }

    public BasicTypeDataHolder(String inputId, String typeCode, String renderColor) {
        this.inputId = inputId;
        this.outputId = inputId;

        fieldTypeManager = (FieldTypeManager) CDIBeanLocator.getBeanByType(FieldTypeManager.class);
        try{
            this.basicFieldType = fieldTypeManager.getTypeByCode(typeCode);
        }catch (Exception e){

        }

        setRenderColor(renderColor);
    }

    @Override
    public String getInputId() {
        return inputId;
    }

    @Override
    public String getOuputId() {
        return outputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    public void setOutputId(String outputId) {
        this.outputId = outputId;
    }

    public FieldType geBasicFieldType() {
        return basicFieldType;
    }

    public void setBasicFieldType(FieldType basicFieldType) {
        this.basicFieldType = basicFieldType;
    }

    @Override
    public void writeValue(Object destination, String propName, Object value) throws Exception {
        //TODO.. verify this behaviour
        if (destination == null) return;
        Field field = destination.getClass().getDeclaredField(propName);

        Method setterMethod = destination.getClass().getMethod("set" + capitalize(propName), new Class[]{field.getType()});
        setterMethod.invoke(destination, new Object[]{value});
    }

    @Override
    public Object readFromBindingExperssion(Object source, String bindingExpression) throws Exception {
        if (source == null || StringUtils.isEmpty(bindingExpression) || bindingExpression.indexOf("/") == -1) return null;

        bindingExpression = bindingExpression.substring(1, bindingExpression.length() - 1);

        String[] bindingParts = bindingExpression.split("/");

        if (bindingParts.length == 2) {
            return readValue(source, bindingParts[1]);
        }
        return null;
    }

    @Override
    public Object readValue(Object source, String propName) throws Exception {
        //TODO.. verify this behaviour
        if (source == null) return null;

        Method getter = source.getClass().getMethod("get" + capitalize(propName));
        Object value = getter.invoke(source);

        return value;
    }

    private String capitalize(String string) {
        if (null == string) return "";
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    @Override
    public Set<DataFieldHolder> getFieldHolders() {
        try{
            if(dataFieldHolders == null || dataFieldHolders.size()==0){
                dataFieldHolders = new TreeSet<DataFieldHolder>();
                DataFieldHolder datafieldHolder =  new DataFieldHolder(this,inputId, basicFieldType.getCode());
                dataFieldHolders.add(datafieldHolder);

            }
            return dataFieldHolders;
        }catch (Exception e){
        }
        return null;
    }

    @Override
    public String getTypeCode() {
        return Form.HOLDER_TYPE_CODE_BASIC_TYPE;
    }

    @Override
    public String getInfo() {
        return basicFieldType.getCode();
    }

    @Override
    public DataFieldHolder getDataFieldHolderById(String fieldHolderId) {
        if(getFieldHolders()!=null){
            for(DataFieldHolder dataFieldHolder: getFieldHolders() ){
                if(dataFieldHolder.getId().equals(fieldHolderId))
                    return dataFieldHolder;
            }
        }
        return null;
    }


    @Override
    public boolean isAssignableValue(Object value) {
        //TODO verify
        if (value == null) return true;
        return value.getClass().getName().equals(this.basicFieldType.getFieldClass());
    }
}
