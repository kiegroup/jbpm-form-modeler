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

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ApplicationScoped
public class BindingManagerImpl implements BindingManager {

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

    public static BindingManagerImpl lookup() {
        return (BindingManagerImpl) CDIBeanLocator.getBeanByType(BindingManagerImpl.class);
    }

}
