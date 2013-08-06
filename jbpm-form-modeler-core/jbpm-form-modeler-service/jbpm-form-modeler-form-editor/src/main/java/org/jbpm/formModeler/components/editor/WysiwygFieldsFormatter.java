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
package org.jbpm.formModeler.components.editor;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.PropertyDefinition;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Named("WysiwygFieldsFormatter")
public class WysiwygFieldsFormatter extends Formatter {

    @Inject
    private Log log;


    public FieldHandlersManager getFieldHandlersManager() {
        return FormProcessingServices.lookup().getFieldHandlersManager();
    }

    public FieldTypeManager getFieldTypesManager() {
        return FormCoreServices.lookup().getFieldTypeManager();
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Form form = WysiwygFormEditor.lookup().getCurrentForm();
            renderAvailableFields(form);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    protected void renderAvailableFields(Form form) throws Exception {
        HashSet availableEntityProperties = new HashSet();
        /*
        TODO: fix that
        availableEntityProperties.addAll(getEditor().getDdmManager().getPropertyNames(entity.getItemClass()));
        for (Iterator it = form.getFormFields().iterator(); it.hasNext();) {
            FormField formField = (FormField) it.next();
            availableEntityProperties.remove(formField.getField().getFieldName());
        }
        DDMManager ddmManager = entity.getDdmManager();
        HashMap availableEntityPropertiesMap = new HashMap();
        for (Iterator iterator = availableEntityProperties.iterator(); iterator.hasNext();) {
            String propName = (String) iterator.next();
            availableEntityPropertiesMap.put(propName, ddmManager.getPropertyType(propName, entity.getItemClass()));
        }

        */

        HashMap availableEntityPropertiesMap = new HashMap();

        renderDecorators(form);
        renderSeparator();
        renderPrimitiveTypes(form, availableEntityPropertiesMap);
        renderSeparator();
    }


    protected void renderSeparator() {
        renderFragment("separator");
    }

    protected void renderDecorators(Form form) throws Exception {
        List decorators = getFieldTypesManager().getFormDecoratorTypes();
        if (decorators.size() > 0) {
            renderFragment("decoratorsStart");
            for (int i = 0; i < decorators.size(); i++) {
                FieldType type = (FieldType) decorators.get(i);
                setAttribute("decorator", type);
                setAttribute("decoratorId", type.getCode());
                setAttribute("iconUri", getFieldTypesManager().getIconPathForCode(type.getCode()));
                setAttribute("position", i);
                renderFragment("outputDecorator");
            }
            renderFragment("decoratorsEnd");
        }
    }

    protected void renderPrimitiveTypes(Form form, HashMap availableEntityProperties) throws Exception {
        List<FieldHandler> handlers = getFieldHandlersManager().getHandlers();
        for (int i = 0; i < handlers.size(); i++) {
            FieldHandler handler = handlers.get(i);
            String managerId = handler.getName();
            String managerName = handler.getHumanName(getLocale());
            List fieldTypes = getTypesForManager(managerId);
            if (!fieldTypes.isEmpty()) {
                setAttribute("managerName", managerName);
                setAttribute("managerId", managerId);
                setAttribute("position", i);
                setAttribute("type", "primitive");
                renderFragment("typeStart");
                for (int j = 0; j < fieldTypes.size(); j++) {
                    FieldType type = (FieldType) fieldTypes.get(j);
                    if(getFieldTypesManager().isDisplayableType(type.getCode())){
                        setAttribute("typeName", type.getCode());
                        setAttribute("iconUri", getFieldTypesManager().getIconPathForCode(type.getCode()));
                        setAttribute("uid", "primitive" + i + "_" + j);
                        setAttribute("typeId", type.getCode());
                        renderFragment("outputType");
                    }
                }
                renderFragment("typeEnd");
            }
        }
    }

    protected List<FieldType> getTypesForManager(String managerClass) throws Exception {
        return getFieldTypesManager().getSuitableFieldTypes(managerClass);
    }

    protected List getAvailablePropertiesForType(FieldType type, Form form,Map availableEntityProperties) throws Exception {
        List props = new ArrayList();
        for (Iterator iterator = availableEntityProperties.keySet().iterator(); iterator.hasNext();) {
            String propertyName = (String) iterator.next();
            if (!getFieldHandlersManager().getHandler(type).acceptsPropertyName(propertyName)) continue;
            PropertyDefinition def = (PropertyDefinition) availableEntityProperties.get(propertyName);
            String typeDescription = def.getId();
            if (type.getFieldClass().equals(typeDescription)) {
                props.add(propertyName);
            }
        }
        return props;
    }
}
