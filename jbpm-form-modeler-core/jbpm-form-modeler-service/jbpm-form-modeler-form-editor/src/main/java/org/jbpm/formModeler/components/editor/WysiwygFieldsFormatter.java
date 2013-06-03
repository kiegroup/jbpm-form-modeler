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
import java.lang.reflect.Field;
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
        renderComplexTypes(form, availableEntityPropertiesMap);
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

    protected void renderComplexTypes(Form form,Map availableEntityProperties) throws Exception {
        /*
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.fieldHandlers", getLocale());
        // Render selectors, both remote and entity
        String[] entitySelectorsFieldHandlers = getFieldHandlersManager().getEntitySelectorsFieldHandlers();
        for (int i = 0; i < entitySelectorsFieldHandlers.length; i++) {
            Class fieldHandler = Class.forName(entitySelectorsFieldHandlers[i]);
            renderComplexType(fieldHandler, i > 2, i, bundle, form, entity, availableEntityProperties, false);
        }
             */
        //Render subforms
        //renderComplexType(NestedSubformFieldHandler.class, false, entitySelectorsFieldHandlers.length, bundle, form, entity, availableEntityProperties, true);
        //renderComplexType(CreateDynamicObjectFieldHandler.class, true, entitySelectorsFieldHandlers.length + 1, bundle, form, entity, availableEntityProperties, true);
    }

    protected void renderComplexType(Class fieldHandler, boolean multiple, int position, ResourceBundle bundle, Form form, Map availableEntityProperties, boolean isSubform) throws Exception {
        /*
        String code = getComplexTypeCode(fieldHandler);
        try {
            setAttribute("managerName", bundle.getString(code));
        }
        catch (Exception e) {
            setAttribute("managerName", code);
        }
        setAttribute("position", position);
        setAttribute("type", "complexType");
        setAttribute("iconUri", getEditor().getFieldTypesManager().getIconPathForCode(code));
        List availableProperties = getAvailablePropertiesForSelector(form, entity, availableEntityProperties, multiple, isSubform);
        if (availableProperties.isEmpty()) {
            renderFragment("disabledComplexTypeStart");
        } else {
            renderFragment("complexTypeStart");
            if (availableProperties.size() > 0) {
                setAttribute("position", position);
                setAttribute("type", "complexType");
                renderFragment("outputComplexFieldNameToAddStart");
                String[] remoteSelectorsFieldHandlers = getFieldHandlersManager().getRemoteSelectorsFieldHandlers();

                int pos = 0;
                
                for (Iterator it = availableProperties.iterator(); it.hasNext();) {
                    String prop = (String) it.next();
                    String managerClass = fieldHandler.getName();
                    PropertyDefinition definition = entity.getDdmManager().getPropertyType(prop, entity.getItemClass());
                    if (definition.isRemoteObject()) {
                        managerClass = remoteSelectorsFieldHandlers[position];
                        if (managerClass.contains("AdvancedSelect")) {
                            continue;
                        }
                    }
                    setAttribute("typeName", prop);
                    setAttribute("managerClass", managerClass);
                    setAttribute("uid", "complex" + position + "_" + pos);
                    renderFragment("outputComplexFieldNameToAdd");
                    pos ++;
                }

                renderFragment("outputComplexFieldNameToAddEnd");
            }
            renderFragment("complexTypeEnd");
        }
        */
    }

    protected String getComplexTypeCode(Class clazz) {
        try {
            Field codeField = clazz.getField("CODE");
            return (String) codeField.get(null);
        } catch (NoSuchFieldException e) {
            log.error("Error: ", e);
        } catch (IllegalAccessException e) {
            log.error("Error: ", e);
        }
        return null;
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
                    setAttribute("typeName", type.getCode());
                    setAttribute("iconUri", getFieldTypesManager().getIconPathForCode(type.getCode()));
                    setAttribute("uid", "primitive" + i + "_" + j);
                    setAttribute("typeId", type.getCode());
                    renderFragment("outputType");

                    /*
                    List availableProperties = getAvailablePropertiesForType(type, form, availableEntityProperties);
                    if (!availableProperties.isEmpty()) {
                        setAttribute("uid", "primitive" + i + "_" + j);
                        setAttribute("typeId", type.getDbid());
                        renderFragment("outputType");
                        setAttribute("uid", "primitive" + i + "_" + j);
                        renderFragment("outputFieldNameToAddStart");
                        for (int k = 0; k < availableProperties.size(); k++) {
                            String prop = (String) availableProperties.get(k);
                            setAttribute("prop", prop);
                            setAttribute("uid", "primitive" + i + "_" + j + "_" + k);
                            setAttribute("typeId", type.getDbid());
                            setAttribute("typeName", typeName);
                            renderFragment("outputFieldNameToAdd");
                        }
                        renderFragment("outputFieldNameToAddEnd");
                    } else {
                        renderFragment("outputDisabledType");
                    }
                    */
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

    protected List getAvailablePropertiesForSelector(Form form, Map availableEntityProperties, boolean multiple, boolean isSubform) throws Exception {
        List props = new ArrayList();
        /*
        for (Iterator iterator = availableEntityProperties.keySet().iterator(); iterator.hasNext();) {
            String propertyName = (String) iterator.next();
            PropertyDefinition def = (PropertyDefinition) availableEntityProperties.get(propertyName);
            if (def.isDDMEntity() || (def.isRemoteObject() && !isSubform)) {
                if (def.isMultiple()) {
                    if (multiple) props.add(propertyName);
                } else {
                    if (!multiple) props.add(propertyName);
                }
            }
        }
        */
        return props;
    }
}
