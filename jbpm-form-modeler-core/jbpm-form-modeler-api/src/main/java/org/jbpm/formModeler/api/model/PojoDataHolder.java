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


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class PojoDataHolder implements DataHolder,Comparable {
    private String id;
    private String className;

    FieldTypeManager fieldTypeManager;


    public PojoDataHolder(String id, String className) {
        this.id = id;
        this.className = className;
        fieldTypeManager = (FieldTypeManager)CDIHelper.getBeanByType(FieldTypeManager.class);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void load(Map<String, Object> values) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeValue(String id, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object readValue(String id) {
        return null;
    }

    @Override
    public Set<DataFieldHolder> getFieldHolders() {
        try{
            return calculatePropertyNames();
        }catch (Exception e){
        }
        return null;
    }

    @Override
    public String getTypeCode() {
        return "className";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int compareTo(Object o) {
        return id.compareTo(((PojoDataHolder) o).getId());
    }

    @Override
    public String getShowBindingStr() {
        return className;
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
        for (Iterator it = propertiesDescriptors.keySet().iterator(); it.hasNext(); ) {
            String propertyName = (String) it.next();
            Map propertyValue = (Map) propertiesDescriptors.get(propertyName);
            for (Iterator itMethods = propertyValue.keySet().iterator(); itMethods.hasNext(); ) {
                Class clazz = (Class) itMethods.next();
                Boolean[] clazzValues = (Boolean[]) propertyValue.get(clazz);
                if (clazzValues[0].booleanValue() && clazzValues[1].booleanValue()) {
                    try{
                        dataFieldHolders.add(new DataFieldHolder(this,propertyName, fieldTypeManager.getTypeByClass(clazz.getName()).getCode()));
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
}
