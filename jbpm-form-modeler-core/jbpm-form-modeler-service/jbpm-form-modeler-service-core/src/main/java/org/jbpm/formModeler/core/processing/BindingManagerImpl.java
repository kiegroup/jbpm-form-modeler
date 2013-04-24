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
package org.jbpm.formModeler.core.processing;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.BindingSource;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.processing.BindingManager;
import org.jbpm.formModeler.api.processing.PropertyDefinition;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;
import org.jbpm.formModeler.core.config.FieldTypeManagerImpl;

import javax.enterprise.context.ApplicationScoped;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ApplicationScoped
public class BindingManagerImpl implements BindingManager {


    @Override
    public PropertyDefinition getPropertyDefinition(FieldType type) throws Exception {
        PropertyDefinitionImpl def = new PropertyDefinitionImpl();

        def.setPropertyClass(Class.forName(type.getFieldClass()));

        return def;
    }

    @Override
    public PropertyDefinition getPropertyDefinition(String propertyName, String className) throws Exception{
        return getPropertyDefinition(propertyName, Class.forName(className));
    }

    @Override
    public PropertyDefinition getPropertyDefinition(String propertyName, Class clazz) throws Exception{

        try {
            PropertyDefinitionImpl def = new PropertyDefinitionImpl();
            Field field = clazz.getDeclaredField(propertyName);
            def.setPropertyClass(field.getType());
            return def;
        } catch (Exception e) {

        }

        return null;
    }

    @Override
    public boolean hasProperty(Object obj, String propName) {
        try {
            Field field = obj.getClass().getDeclaredField(propName);
            return field != null;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public Object getPropertyValue(Object obj, String propName) {
        Object value = null;

        try {
            Method getter = obj.getClass().getMethod("get" + StringUtils.capitalize(propName));
            value = getter.invoke(obj);
            return value;
        } catch (Exception e) {

        }
        return value;
    }

    @Override
    public void setPropertyValue(Object destination, String propName, Object value) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field field = destination.getClass().getDeclaredField(propName);

        Method setterMethod = destination.getClass().getMethod("set" + StringUtils.capitalize(propName), new Class[]{field.getType()});
        setterMethod.invoke(destination, new Object[]{value});
    }

    public static final BindingManagerImpl lookup() {
        return (BindingManagerImpl) CDIHelper.getBeanByType(BindingManagerImpl.class);
    }

    @Override
    public Map getBindingFields(BindingSource source) {
        if(BindingSource.BINDING_CODE_TYPE_CLASSNAME.equals(source.getBindingType())){
            return calculatePropertyNames(source.getBindingStr());
        }
        return null;
    }

    private Map calculatePropertyNames(String className) {
        Class clase = null;
        try {
            clase = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }

        if (clase == null) {
            return null;
        }

        Map staticProperties = new HashMap();
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
                    staticProperties.put(propertyName, clazz);
                    break;
                }
            }
        }
        return staticProperties;
    }

    protected boolean isValidReturnType(String returnType){
        if(returnType== null) return false;
        if ("void".equals(returnType)) return true;
        if (FieldTypeManagerImpl.lookup().getTypeByClass(returnType) != null) return true;
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
