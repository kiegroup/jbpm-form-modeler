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
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.builders.dataHolder.BasicTypeHolderBuilder;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

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


    public BasicTypeDataHolder(String uniqueId, String inputId, String outputId, String className, String renderColor) {
        this.uniqueId = uniqueId;
        this.inputId = inputId;
        this.outputId = outputId;
        fieldTypeManager = (FieldTypeManager) CDIBeanLocator.getBeanByType(FieldTypeManager.class);

        try{
            this.basicFieldType = fieldTypeManager.getTypeByClass(className);
        }catch (Exception e){

        }

        setRenderColor(renderColor);
    }

    //TODO remove this constructor.
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
    }

    @Override
    public Object readFromBindingExperssion(Object source, String bindingExpression) throws Exception {

        if (source == null || StringUtils.isEmpty(bindingExpression) ) return null;

        bindingExpression = bindingExpressionUtil.extractBindingExpression(bindingExpression);

        return readValue(source, bindingExpression);
    }

    @Override
    public Object readValue(Object source, String propName) throws Exception {
        return source;
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
                DataFieldHolder datafieldHolder =  new DataFieldHolder(this,StringUtils.defaultIfEmpty(inputId, outputId), basicFieldType.getFieldClass());
                dataFieldHolders.add(datafieldHolder);

            }
            return dataFieldHolders;
        }catch (Exception e){
        }
        return null;
    }

    @Override
    public String getTypeCode() {
        return BasicTypeHolderBuilder.HOLDER_TYPE_BASIC_TYPE;
    }

    @Override
    public String getInfo() {
        return basicFieldType.getFieldClass();
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
        if (value == null) return true;
        try {
            return value.getClass().getName().equals(this.basicFieldType.getFieldClass()) || Class.forName(this.basicFieldType.getFieldClass()).isAssignableFrom(value.getClass()) ;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    @Override
    public String getInputBinding(String fieldName) {
        if (StringUtils.isEmpty(getInputId()) || StringUtils.isEmpty(fieldName)) return "";
        return bindingExpressionUtil.generateBindingExpression(getInputId());
        //return "{" + getInputId() +"}" ;
    }

    @Override
    public String getOuputBinding(String fieldName) {
        if (StringUtils.isEmpty(getOuputId()) || StringUtils.isEmpty(fieldName)) return "";
        return bindingExpressionUtil.generateBindingExpression(getOuputId());
        //return "{" + getOuputId() + "}" ;
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

        if (StringUtils.isEmpty(rawbinding)) return false;

        return id.equals(rawbinding);
    }

    @Override
    public boolean canHaveChildren() {
        return false;
    }
}
