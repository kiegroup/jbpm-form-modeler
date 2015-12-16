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
package org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform;


import org.apache.commons.lang3.ArrayUtils;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.slf4j.Logger;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.processing.*;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.core.processing.formStatus.FormStatus;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;

import org.jbpm.formModeler.core.rendering.SubformFinderService;

import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@ApplicationScoped
@Named("org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.SubFormSendHandler")
public class SubFormSendHandler extends BeanHandler {

    private Logger log = LoggerFactory.getLogger(SubFormSendHandler.class);

    @Inject
    private SubformFinderService subformFinderService;

    @Inject
    private SubFormHelper helper;

    public NamespaceManager getNamespaceManager() {
        return NamespaceManager.lookup();
    }

    public FormProcessor getFormProcessor() {
        return FormProcessingServices.lookup().getFormProcessor();
    }

    public FormStatusManager getFormStatusManager() {
        return FormStatusManager.lookup();
    }
    public FieldHandlersManager getFieldHandlersManager() {
        return FormProcessingServices.lookup().getFieldHandlersManager();
    }

    public void actionExpandSubform(CommandRequest request) {
        log.debug("Expanding subform");
        Enumeration parameterNames = request.getRequestObject().getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if (parameterName.endsWith(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "expand")) {
                String parameterValue = request.getParameter(parameterName);
                if ("true".equals(parameterValue) || "false".equals(parameterValue)) {
                    boolean expand = Boolean.valueOf(parameterValue).booleanValue();

                    FormNamespaceData fsd = getNamespaceManager().getNamespace(parameterName);
                    //  Clear the child create form
                    getFormProcessor().setValues(fsd.getForm(), fsd.getNamespace(), request.getRequestObject().getParameterMap(), request.getFilesByParamName());
                    String fieldName = fsd.getFieldNameInParent();
                    fieldName = fieldName.substring(0, fieldName.length() - (FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create").length());
                    Field field = fsd.getForm().getField(fieldName);

                    String inputName = getNamespaceManager().generateFieldNamesPace( fsd.getNamespace(), field );

                    if (expand)
                        helper.setExpandedField( inputName, inputName );
                    else {
                        helper.clearExpandedField( inputName );
                    }

                    // PFP : cleared error when the subform to expand is a required field.
                    if (expand) {
                        FormStatus formStatus = getFormStatusManager().getFormStatus(fsd.getForm(), fsd.getNamespace());
                        if (formStatus != null)
                            formStatus.removeWrongField(fieldName);
                    }

                    FieldHandler handler = getFieldHandlersManager().getHandler(field.getFieldType());
                    if (handler instanceof CreateDynamicObjectFieldHandler) {
                        CreateDynamicObjectFieldHandler fHandler = (CreateDynamicObjectFieldHandler) handler;
                        Form createForm = fHandler.getCreateForm(field, fsd.getNamespace());
                        String createFormNamespace = fsd.getNamespace() + FormProcessor.NAMESPACE_SEPARATOR + fsd.getForm().getId() + FormProcessor.NAMESPACE_SEPARATOR + fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create";
                        getFormProcessor().clear(createForm, createFormNamespace);
                        log.debug("Cleared subform status");
                    } else {
                        log.error("Can't clear subform to unknown field type: " + handler.getClass().getName());
                    }

                    // Clear errors in parent
                    getFormProcessor().clearFieldErrors(fsd.getForm(), fsd.getNamespace());
                    break;
                }
            }
        }
    }

    public void actionAddItem(CommandRequest request) {
        log.debug("Adding item to subform");
        Set s = getFormNamespaceDatas(request, FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            FormNamespaceData formData = (FormNamespaceData) iterator.next();
            String fieldName = formData.getFieldNameInParent();
            fieldName = fieldName.substring(0, fieldName.length() - (FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create").length());
            Field field = formData.getForm().getField(fieldName);
            FieldHandler handler = getFieldHandlersManager().getHandler(field.getFieldType());
            if (handler instanceof CreateDynamicObjectFieldHandler) {
                CreateDynamicObjectFieldHandler fHandler = (CreateDynamicObjectFieldHandler) handler;
                Form createForm = fHandler.getCreateForm(field, formData.getNamespace());
                String createFormNamespace = formData.getNamespace() + FormProcessor.NAMESPACE_SEPARATOR + formData.getForm().getId() + FormProcessor.NAMESPACE_SEPARATOR + fieldName;
                getFormProcessor().setAttribute(createForm, createFormNamespace, FormStatusData.DO_THE_ITEM_ADD, Boolean.TRUE);
                getFormProcessor().setValues(formData.getForm(), formData.getNamespace(), request.getRequestObject().getParameterMap(), request.getFilesByParamName());
                FormStatusData createStatus = getFormProcessor().read(createForm, createFormNamespace + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                boolean operationSuccess = createStatus.isValid();
                if (operationSuccess) {
                    getFormProcessor().clearFieldErrors(formData.getForm(), formData.getNamespace());
                    getFormProcessor().clear(createForm, createFormNamespace + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                } else {
                    List wrongFields = createStatus.getWrongFields();
                    getFormProcessor().clearFieldErrors(formData.getForm(), formData.getNamespace());
                    for (int i = 0; i < wrongFields.size(); i++) {
                        String fieldNameToMarkAsWrong = (String) wrongFields.get(i);
                        getFormProcessor().forceWrongField(createForm, createFormNamespace + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create", fieldNameToMarkAsWrong);
                    }
                }
                getFormProcessor().setAttribute(createForm, createFormNamespace, FormStatusData.DO_THE_ITEM_ADD, Boolean.FALSE);
                log.debug("Item added to subform");
            } else {
                log.error("Can't add item to unknown field type: " + handler.getClass().getName());
            }
        }
    }

    public void actionDeleteItem(CommandRequest request) throws Exception {
        String[] uids = request.getRequestObject().getParameterValues("child_uid_value");
        String uid = "";
        if (uids != null) {
            for (int i = 0; i < uids.length; i++) {
                if (uids[i] != null && !"".equals(uids[i])) uid = uids[i];
            }
        }
        String sIndex = request.getParameter(uid + "_index");
        String parentFormId = request.getParameter(uid + "_parentFormId");
        String parentNamespace = request.getParameter(uid + "_parentNamespace");
        String fieldName = request.getParameter(uid + "_field");
        String inputName = request.getParameter(uid + "_inputName");

        Form parentForm = subformFinderService.getFormById( Long.decode( parentFormId ), parentNamespace );

        getFormProcessor().setValues(parentForm, parentNamespace, request.getRequestObject().getParameterMap(), request.getFilesByParamName());
        Field field = parentForm.getField(fieldName);
        FieldHandler handler = getFieldHandlersManager().getHandler(field.getFieldType());
        if (handler instanceof CreateDynamicObjectFieldHandler) {
            CreateDynamicObjectFieldHandler fHandler = (CreateDynamicObjectFieldHandler) handler;
            int index =  Integer.decode(sIndex).intValue();
            Object deletedResultValue = fHandler.deleteElementInPosition(parentForm, parentNamespace, fieldName, index);

            List<Integer> removedValues = helper.getRemovedFieldPositions( inputName );

            if (removedValues == null) helper.setRemovedFieldPositions( inputName, ( removedValues = new ArrayList<Integer>() ) );

            removedValues.add(index);

            getFormProcessor().modify(parentForm, parentNamespace, fieldName, deletedResultValue);
        } else {
            log.error("Cannot delete value in a field which is not a CreateDynamicObjectFieldHandler.");
        }
        getFormProcessor().clearFieldErrors(parentForm, parentNamespace);
    }

    public void actionEditItem(CommandRequest request) throws Exception {
        editItem(request, true);
    }

    public void actionSaveEditedItem(CommandRequest request) {
        log.debug("Saving edited item in subform");
        Set s = getFormNamespaceDatas(request, FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited");
        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            FormNamespaceData formData = (FormNamespaceData) iterator.next();
            getFormProcessor().setValues(formData.getForm(), formData.getNamespace(), request.getRequestObject().getParameterMap(), request.getFilesByParamName(), true);
        }
        log.debug("Item from subform, saved");
    }

    public void actionCancelEditItem(CommandRequest request) throws Exception {
        editItem(request, false);
    }

    public void actionPreviewItem(CommandRequest request) throws Exception {
        previewItem(request, true);
    }

    public void actionCancelPreviewItem(CommandRequest request) throws Exception {
        previewItem(request, false);
    }

    public void previewItem(CommandRequest request, boolean doIt) throws Exception {
        String[] uids = request.getRequestObject().getParameterValues("child_uid_value");
        String uid = "";
        if (uids != null) {
            for (int i = 0; i < uids.length; i++) {
                if (uids[i] != null && !"".equals(uids[i])) uid = uids[i];
            }
        }
        String index = request.getParameter(uid + "_index");
        String parentFormId = request.getParameter(uid + "_parentFormId");
        String parentNamespace = request.getParameter(uid + "_parentNamespace");
        String inputName = request.getParameter(uid + "_inputName");

        Form form = subformFinderService.getFormById(Long.decode(parentFormId), parentNamespace);
        getFormProcessor().setValues(form, parentNamespace, request.getRequestObject().getParameterMap(), request.getFilesByParamName());

        if (doIt) {
            helper.setPreviewFieldPosition( inputName, Integer.decode(index) );
        } else {
            helper.clearPreviewFieldPositions( inputName );
        }
        getFormProcessor().clearFieldErrors(form, parentNamespace);
    }

    public void editItem(CommandRequest request, boolean doIt) throws Exception {
        String[] uids = request.getRequestObject().getParameterValues("child_uid_value");
        String uid = "";
        if (uids != null) {
            for (int i = 0; i < uids.length; i++) {
                if (uids[i] != null && !"".equals(uids[i])) uid = uids[i];
            }
        }
        String index = request.getParameter(uid + "_index");
        String parentFormId = request.getParameter(uid + "_parentFormId");
        String parentNamespace = request.getParameter(uid + "_parentNamespace");
        String fieldName = request.getParameter(uid + "_field");
        String inputName = request.getParameter(uid + "_inputName");

        Form form = subformFinderService.getFormById( Long.decode( parentFormId ), parentNamespace );
        Field field = form.getField(fieldName);

        getFormProcessor().setValues(form, parentNamespace, request.getRequestObject().getParameterMap(), request.getFilesByParamName());

        if (doIt) {
            FormStatusData fsd = getFormProcessor().read(form, parentNamespace);
            Map[] previousValue = deepCloneOfMapArray( ( Map[] ) fsd.getCurrentValue( fieldName ), new HashMap() );
            helper.setEditFieldPreviousValues( inputName, previousValue );
            CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler( field.getFieldType() );

            Form formToEdit = fieldHandler.getEditForm(field, parentNamespace);

            getFormProcessor().clear( formToEdit, parentNamespace + FormProcessor.NAMESPACE_SEPARATOR + parentFormId + FormProcessor.NAMESPACE_SEPARATOR + fieldName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + index);
            getFormProcessor().clear( formToEdit, parentNamespace + FormProcessor.NAMESPACE_SEPARATOR + parentFormId + FormProcessor.NAMESPACE_SEPARATOR + fieldName );
            helper.setEditFieldPosition( inputName, Integer.decode( index ) );
        } else {
            Object previousValue = helper.getEditFieldPreviousValues( inputName );
            getFormProcessor().modify( form, parentNamespace, fieldName, previousValue );
            helper.clearExpandedField( inputName );
            helper.clearEditFieldPositions( inputName );
            helper.clearEditFieldPreviousValues( inputName );
            helper.clearPreviewFieldPositions( inputName );
        }
        getFormProcessor().clearFieldErrors(form, parentNamespace);
    }



    protected Map[] deepCloneOfMapArray(Map[] maparray, Map cache) {
        if (maparray == null || maparray.length == 0) return maparray;
        Map[] clone = new Map[0];
        for (int i = 0; i < maparray.length; i++) {
            Map map = maparray[i];
            Map cloneMap = deepCloneOfMap(map, cache);
            clone = (Map[]) ArrayUtils.add(clone, cloneMap);
        }
        return clone;
    }

    protected Map deepCloneOfMap(Map map, Map cache) {
        if (map == null) return null;
        if (map instanceof I18nSet) return map;
        Map clone = new HashMap();
        Set keys = map.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            Object value = map.get(key);
            if (value == null) {
                clone.put(key, value);
            } else if (value instanceof Map) {
                clone.put(key, deepCloneOfMap((Map) value, cache));
            } else if (value instanceof Map[]) {
                clone.put(key, deepCloneOfMapArray((Map[]) value, cache));
            } else {
                clone.put(key, value);
            }
        }
        return clone;
    }


    protected Set getFormNamespaceDatas(CommandRequest request, String action) {
        Set s = new HashSet();
        for (Enumeration en = request.getRequestObject().getParameterNames(); en.hasMoreElements();) {
            String paramName = (String) en.nextElement();
            String paramValue = request.getParameter(paramName);
            if ("true".equals(paramValue) && paramName.endsWith(action)) {
                FormNamespaceData fsd = getNamespaceManager().getNamespace(paramName);
                if (fsd != null) s.add(fsd);

                /* Add also all parent namespaces ??? Seems not to be needed
               while (fsd != null) {
                   s.add(fsd);
                   fsd = namespaceManager.getNamespace(fsd.getNamespace());
               } */
            }
        }
        return s;
    }

    @Override
    public boolean isEnabledForActionHandling() {
        return true;
    }
}
