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

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.jbpm.formModeler.dataModeler.integration.DataModelerService;
import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;


import java.util.*;

public class DataModelerDataHolder extends PojoDataHolder {

    private Class holderClass;

    public DataModelerDataHolder(String holderId, String inputId, String outputId, String holderClass, String renderColor) {
        super(holderId, inputId, outputId, holderClass, renderColor);
    }

    public DataModelerDataHolder(String holderId, String inputId, String outputId, Class holderClass, String renderColor) {
        super(holderId, inputId, outputId, holderClass.getCanonicalName(), renderColor);
        this.holderClass = holderClass;
    }

    @Override
    public Object createInstance(FormRenderContext context) throws Exception {
        FormRenderContentMarshallerManager marshallerManager = (FormRenderContentMarshallerManager) CDIBeanLocator.getBeanByType(FormRenderContentMarshallerManager.class);
        ContentMarshallerContext contextMarshaller = marshallerManager.getContentMarshaller(context.getUID());
        ClassLoader classLoader = contextMarshaller.getClassloader();
        return createInstance(classLoader.loadClass(getClassName()));
    }

    @Override
    public String getTypeCode() {
        return DataModelerService.HOLDER_TYPE_DATA_MODEL;
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

    @Override
    protected Class getHolderClass() throws ClassNotFoundException {
        return holderClass;
    }
}
