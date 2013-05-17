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
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;


import java.lang.reflect.Field;
import java.lang.reflect.*;
import java.util.*;

public class DataModelerDataHolder extends PojoDataHolder implements Comparable,DataHolder {
    private String id;
    private String className;
    DataObjectTO dataObjectTO ;

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


    public DataModelerDataHolder(String id, String className, String renderColor, DataObjectTO dataObjectTO) {
        this.id = id;
        this.className = className;
        fieldTypeManager = (FieldTypeManager)CDIHelper.getBeanByType(FieldTypeManager.class);
        this.dataObjectTO = dataObjectTO;
        setRenderColor(renderColor);
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
        return "className";
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
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();

        Set<DataFieldHolder> dataFieldHolders = new TreeSet<DataFieldHolder>();

        Map propertiesDescriptors = new HashMap();
        DataFieldHolder fieldHolder = null;

        for (ObjectPropertyTO propertyTO : properties) {;
            Class returnType = propertyTO.getClass();
            if (isValidReturnType(returnType.getName())) {
                try{
                    fieldHolder =  new DataFieldHolder(this,propertyTO.getName(), fieldTypeManager.getTypeByClass(propertyTO.getName()).getCode());
                    dataFieldHolders.add(fieldHolder);
                } catch (Exception e){
                    //The
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

}
