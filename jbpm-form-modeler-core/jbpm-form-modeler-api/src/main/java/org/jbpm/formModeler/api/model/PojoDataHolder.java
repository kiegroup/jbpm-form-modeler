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

import org.jbpm.formModeler.api.config.FieldTypeManager;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;


import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;

public class PojoDataHolder extends DefaultDataHolder implements Comparable {
    private String id;
    private String className;
//    private String renderColor;

    FieldTypeManager fieldTypeManager;

    Set<DataFieldHolder> dataFieldHolders;


    public Object createInstance() throws Exception {
        Object result = null;
        for (Constructor constructor : Class.forName(className).getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                result = constructor.newInstance();
            }
        }
        return result;
    }

    public PojoDataHolder(){
    }

    public PojoDataHolder(String id, String className,String renderColor) {
        this.id = id;
        this.className = className;
        fieldTypeManager = (FieldTypeManager)CDIHelper.getBeanByType(FieldTypeManager.class);
        setRenderColor(renderColor);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void writeValue(Object destination, String propName, Object value) throws Exception {
        Field field = destination.getClass().getDeclaredField(propName);

        Method setterMethod = destination.getClass().getMethod("set" + capitalize(propName), new Class[]{field.getType()});
        setterMethod.invoke(destination, new Object[]{value});
    }

    @Override
    public Object readValue(Object destination, String propName) throws Exception {
        Object value = null;

        Method getter = destination.getClass().getMethod("get" + capitalize(propName));
        value = getter.invoke(destination);

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
        return Form.HOLDER_TYPE_CODE_POJO_CLASSNAME;
    }


    public int compareTo(Object o) {
        return id.compareTo(((PojoDataHolder) o).getId());
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

    private Set<DataFieldHolder> calculatePropertyNames() throws Exception{

        Class clase = null;
        try {
            clase = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        if (clase == null) {
            return null;
        }

        Set<DataFieldHolder> dataFieldHolders = new TreeSet<DataFieldHolder>();

        Map propertiesDescriptors = new HashMap();
        Method[] methods = clase.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            Class[] parameterTypes = method.getParameterTypes();
            Class returnType = method.getReturnType();
            if (isValidReturnType(returnType.getName())) {
                String propertyName = getPropertyName(methodName, returnType, parameterTypes);
                if (propertyName != null && Modifier.isPublic(method.getModifiers())) {
                    Map values = (Map) propertiesDescriptors.get(propertyName);
                    if (values == null)
                        propertiesDescriptors.put(propertyName, values = new HashMap());
                    Class clazz = parameterTypes.length == 0 ? returnType : parameterTypes[0]; // Relevant
                    // class
                    Boolean[] clazzValues = (Boolean[]) values.get(clazz);
                    if (clazzValues == null)
                        values.put(clazz, clazzValues = new Boolean[]{Boolean.FALSE, Boolean.FALSE});
                    clazzValues[parameterTypes.length] = Boolean.TRUE;// 0 ->
                    // getter,
                    // 1->
                    // setter
                }
            }
        }
        DataFieldHolder fieldHolder = null;
        for (Iterator it = propertiesDescriptors.keySet().iterator(); it.hasNext(); ) {
            String propertyName = (String) it.next();
            Map propertyValue = (Map) propertiesDescriptors.get(propertyName);
            for (Iterator itMethods = propertyValue.keySet().iterator(); itMethods.hasNext(); ) {
                Class clazz = (Class) itMethods.next();
                Boolean[] clazzValues = (Boolean[]) propertyValue.get(clazz);
                if (clazzValues[0].booleanValue() && clazzValues[1].booleanValue()) {
                    try{
                        fieldHolder =  new DataFieldHolder(this,propertyName, fieldTypeManager.getTypeByClass(clazz.getName()).getCode());
                        dataFieldHolders.add(fieldHolder);
                    } catch (Exception e){
                        //The
                    }
                    break;
                }
            }
        }
        return dataFieldHolders;
    }

    protected boolean isValidReturnType(String returnType) throws Exception{
        if(returnType== null) return false;
        if ("void".equals(returnType)) return true;
        if (fieldTypeManager.getTypeByClass(returnType) != null) return true;
            //else if ("boolean".equals(returnType)) return true;
        else return false;

    }

    protected String getPropertyName(String methodName, Class returnType, Class[] parameterTypes) {
        String propName = null;
        if (((methodName.startsWith("get") && (parameterTypes.length == 0)) || (methodName.startsWith("set") && parameterTypes.length == 1)) && methodName.length() > 3) {
            propName = String.valueOf(Character.toLowerCase(methodName.charAt(3)));
            if (methodName.length() > 4)
                propName += methodName.substring(4);
        } else if (methodName.startsWith("is") && methodName.length() > 2 && returnType.equals(Boolean.class) && parameterTypes.length == 0) {
            propName = String.valueOf(Character.toLowerCase(methodName.charAt(2)));
            if (methodName.length() > 3)
                propName += methodName.substring(3);
        }
        return propName;
    }

//    @Override
//    public String getRenderColor() {
//        return renderColor;
//    }

//    @Override
//    public void setRenderColor(String renderColor) {
//        this.renderColor = renderColor;
//    }
}
