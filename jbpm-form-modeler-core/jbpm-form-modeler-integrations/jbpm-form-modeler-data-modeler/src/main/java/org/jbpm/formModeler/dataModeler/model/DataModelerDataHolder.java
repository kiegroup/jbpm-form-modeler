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
package org.jbpm.formModeler.dataModeler.model;

import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;


import java.lang.reflect.Constructor;
import java.util.*;

public class DataModelerDataHolder extends PojoDataHolder implements Comparable {

    DataObjectTO dataObjectTO ;

    public DataModelerDataHolder(String id, String className, String renderColor, DataObjectTO dataObjectTO) {
        super(id, className, renderColor);
        this.dataObjectTO = dataObjectTO;
    }

    public DataModelerDataHolder(String id, String className, String renderColor) {
        super(id, className, renderColor);
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
        return Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL;
    }


    public int compareTo(Object o) {
        return super.getId().compareTo(((PojoDataHolder) o).getId());
    }

    @Override
    public String getInfo() {
        return super.getClassName();
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
        if (dataObjectTO == null) return Collections.EMPTY_SET;
        List<ObjectPropertyTO> properties = dataObjectTO.getProperties();

        Set<DataFieldHolder> dataFieldHolders = new TreeSet<DataFieldHolder>();

        Map propertiesDescriptors = new HashMap();
        DataFieldHolder fieldHolder = null;

        for (ObjectPropertyTO propertyTO : properties) {;
            Class returnType = propertyTO.getClass();
            if (isValidReturnType(propertyTO.getClassName())) {
                try{
                    fieldHolder =  new DataFieldHolder(this,propertyTO.getName(), fieldTypeManager.getTypeByClass(propertyTO.getClassName()).getCode());
                    dataFieldHolders.add(fieldHolder);
                } catch (Exception e){

                }
            }
        }
        return dataFieldHolders;
    }
}
