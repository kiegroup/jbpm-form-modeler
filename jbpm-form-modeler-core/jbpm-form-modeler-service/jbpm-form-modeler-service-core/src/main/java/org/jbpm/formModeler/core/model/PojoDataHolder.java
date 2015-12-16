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
package org.jbpm.formModeler.core.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.builders.dataHolder.PojoDataHolderBuilder;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoDataHolder extends DefaultDataHolder  {
    private transient Logger log = LoggerFactory.getLogger(PojoDataHolder.class);

    private String inputId;
    private String outputId;
    private String className;

    protected FieldTypeManager fieldTypeManager;

    protected Set<DataFieldHolder> dataFieldHolders;


    public Object createInstance(FormRenderContext context) throws Exception {
        return createInstance(Class.forName(className));
    }

    protected Object createInstance(Class pojoClass) throws Exception {
        for (Constructor constructor : pojoClass.getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor.newInstance();
            }
        }
        return null;
    }

    public PojoDataHolder(String uniqueId, String inputId, String outputId, String className, String renderColor) {
        this.uniqueId = uniqueId;
        this.inputId = inputId;
        this.outputId = outputId;
        this.className = className;
        fieldTypeManager = (FieldTypeManager) CDIBeanLocator.getBeanByType(FieldTypeManager.class);
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void writeValue(Object destination, String propName, Object value) throws Exception {
        if (destination == null) return;
        Field field = destination.getClass().getDeclaredField(propName);

        Method setterMethod = destination.getClass().getMethod("set" + capitalize(propName), new Class[]{field.getType()});
        setterMethod.invoke(destination, new Object[]{value});
    }

    @Override
    public Object readFromBindingExperssion(Object source, String bindingExpression) throws Exception {
        if (source == null || StringUtils.isEmpty(bindingExpression) || bindingExpression.indexOf("/") == -1) return null;

        bindingExpression = bindingExpressionUtil.extractBindingExpression(bindingExpression);

        String[] bindingParts = bindingExpression.split("/");

        if (bindingParts.length == 2) {
            return readValue(source, bindingParts[1]);
        }
        return null;
    }

    @Override
    public Object readValue(Object source, String propName) throws Exception {
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
            if(dataFieldHolders == null || dataFieldHolders.size()==0)
                dataFieldHolders = calculatePropertyNames();
            return dataFieldHolders;
        }catch (Exception e){
        }
        return null;
    }

    @Override
    public String getTypeCode() {
        return PojoDataHolderBuilder.HOLDER_TYPE_POJO_CLASSNAME;
    }


    @Override
    public String getInfo() {
        return className;
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

    protected Class getHolderClass() throws ClassNotFoundException {
        if (!StringUtils.isEmpty(className)) return Class.forName(className);
        return null;
    }

    private Set<DataFieldHolder> calculatePropertyNames() throws Exception {

        Class clazz = getHolderClass();

        if (clazz == null) {
            return null;
        }

        Set<DataFieldHolder> dataFieldHolders = new TreeSet<DataFieldHolder>();

        for (Field field : clazz.getDeclaredFields()) {

            if (isValidType(field.getType().getName())) {
                String capitalizedName = capitalize(field.getName());
                try {
                    Method setter = clazz.getDeclaredMethod("set" + capitalizedName, field.getType());

                    if (!setter.getReturnType().getName().equals("void") && !Modifier.isPublic(setter.getModifiers())) continue;

                    Method getter;

                    if (field.getType().equals(boolean.class)) getter = clazz.getDeclaredMethod("is" + capitalizedName);
                    else getter = clazz.getDeclaredMethod("get" + capitalizedName);

                    if (!getter.getReturnType().equals(field.getType()) && !Modifier.isPublic(getter.getModifiers())) continue;

                    Type type = field.getGenericType();

                    DataFieldHolder fieldHolder;

                    if (type instanceof ParameterizedType) {
                        ParameterizedType generictype = (ParameterizedType)type;
                        Type[] arguments = generictype.getActualTypeArguments();
                        if (arguments == null || arguments.length > 1) fieldHolder =  new DataFieldHolder(this, field.getName(), field.getType().getName());
                        else fieldHolder =  new DataFieldHolder(this, field.getName(), field.getType().getName(), ((Class<?>)arguments[0]).getName());
                    } else {
                        fieldHolder =  new DataFieldHolder(this, field.getName(), field.getType().getName());
                    }

                    dataFieldHolders.add(fieldHolder);

                } catch (Exception e) {
                    getLogger().debug("Unable to generate field holder for '{}': {}", field.getName(), e);
                }
            }
        }

        return dataFieldHolders;
    }

    protected boolean isValidType(String returnType) throws Exception{
        if(returnType== null) return false;
        if (fieldTypeManager.getTypeByClass(returnType) != null) return true;
        else return false;

    }

    @Override
    public boolean isAssignableValue(Object value) {
        if (value == null) return true;
        return value.getClass().getName().equals(this.getClassName());
    }

    public Logger getLogger() {
        return log;
    }
}
