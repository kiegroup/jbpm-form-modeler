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
import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.fieldHandlers.SubformFieldHandler;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named("org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform.CreateDynamicObjectFieldHandler")
public class CreateDynamicObjectFieldHandler extends SubformFieldHandler {
    private static transient Logger log = LoggerFactory.getLogger(CreateDynamicObjectFieldHandler.class);

    public static final String CODE = "subformMultiple";

    @Inject
    private SubformFinderService subformFinderService;

    @Inject
    private FormProcessor formProcessor;

    @Inject
    private SubFormHelper helper;

    /**
     * Read a parameter value (normally from a request), and translate it to
     * an object with desired class (that must be one of the returned by this handler)
     *
     * @return a object with desired class
     * @throws Exception
     */
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        String[] tableEnterMode = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "tableEnterMode");
        boolean doTableEnterMode = tableEnterMode != null && tableEnterMode.length == 1 && tableEnterMode[0].equals("true");

        String[] sCount = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "count");
        int count = sCount != null && sCount.length == 1 ? Integer.valueOf(sCount[0]) : 0;

        Form form = getTableDataForm(field, inputName);

        Map[] previousValuesMap = (Map[]) previousValue;

        if (doTableEnterMode && count > 0) {
            if (previousValuesMap == null) previousValuesMap = new Map[count];

            for (int i = 0; i < count; i++) {
                String namespace = inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i;
                formProcessor.setValues(form, namespace, parametersMap, filesMap);
                FormStatusData status = formProcessor.read(form, namespace);
                if (status.isValid()) {
                    final Map objectCreated = formProcessor.getMapRepresentationToPersist(form, namespace);

                    if (previousValuesMap[i] != null) previousValuesMap[i].putAll(objectCreated);
                    else previousValuesMap[i] = objectCreated;
                }
            }
        }

        String[] createParams = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
        boolean doCreate = createParams != null && createParams.length == 1 && createParams[0].equals("true");
        if (doCreate) {
            Form createForm = getCreateForm(field, inputName);
            boolean addItemEnabled = Boolean.TRUE.equals(formProcessor.getAttribute(createForm, inputName, FormStatusData.DO_THE_ITEM_ADD));
            if (addItemEnabled) {
                formProcessor.setValues(createForm, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create", parametersMap, filesMap);
                FormStatusData status = formProcessor.read(createForm, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                if (status.isValid()) {
                    final Map objectCreated = formProcessor.getMapRepresentationToPersist(createForm, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                    if (previousValuesMap == null) previousValuesMap = new Map[0];
                    previousValuesMap = (Map[]) ArrayUtils.add(previousValuesMap, objectCreated);

                    helper.clearExpandedField( inputName );
                }
            }
        }

        String[] editParams = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited");
        boolean doSaveEdited = editParams != null && editParams.length == 1 && editParams[0].equals("true");
        if (doSaveEdited) {
            Integer position = helper.getEditFieldPosition( inputName );
            if (position != null) {
                Form editForm = getEditForm(field, inputName);
                formProcessor.setValues(editForm, inputName, parametersMap, filesMap);
                FormStatusData status = formProcessor.read(editForm, inputName);
                if (status.isValid()) {
                    final Map objectCreated = formProcessor.getMapRepresentationToPersist(editForm, inputName);
                    previousValuesMap[position].putAll( objectCreated );
                    formProcessor.clear(editForm, inputName);
                    helper.clearEditFieldPositions( inputName );
                }
            }
        }
        return previousValuesMap;
    }

    @Override
    public Map getParamValue(Field field, String inputName, Object value) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Object getStatusValue( Field field, String inputName, Object value, Map rootLoadedObjects ) {
        if (value == null) return new Map[0];
        if (!rootLoadedObjects.containsKey( inputName )) rootLoadedObjects.put( inputName, value );

        Form form = getEnterDataForm(inputName, field);
        DataHolder holder = form.getHolders().iterator().next();

        List values = (List) value;
        Map[] result = new Map[values.size()];

        for (int i = 0; i < values.size(); i++) {
            try {
                Object val = values.get(i);
                Map<String, Object> inputData = new HashMap();
                if (!StringUtils.isEmpty(holder.getInputId())) inputData.put(holder.getInputId(), val);
                rootLoadedObjects.remove( holder.getUniqeId() );
                result[i] = formProcessor.readValuesToLoad(form, inputData, new HashMap(), rootLoadedObjects, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i);
            } catch (Exception e) {
                log.error("Error getting status value for field: " + inputName, e);
            }
        }

        Map<String, Object> loadedObjects = new HashMap();
        loadedObjects.put(holder.getUniqeId(), result);

        formProcessor.read(form, inputName, null, loadedObjects);

        return result;
    }

    @Override
    public Object persist( Field field, String inputName, Object fieldValue ) throws Exception {

        if (fieldValue == null) return null;

        FormNamespaceData rootNamespaceData = getNamespaceManager().getRootNamespace( inputName );
        FormStatusData rootData = formProcessor.read(rootNamespaceData.getForm(), rootNamespaceData.getNamespace());

        Form form = getEnterDataForm(inputName, field);

        // getting parent object to obtain the parent child elements
        Object originalValue = rootData.getLoadedObject( inputName );
        List loadedObjects = null;

        if (originalValue != null) loadedObjects = (List) originalValue;
        else loadedObjects = Collections.EMPTY_LIST;

        Map[] values = (Map[]) fieldValue;
        List result = new ArrayList();

        List<Integer> removedValues = helper.getRemovedFieldPositions( inputName );

        if (removedValues != null) {
            // Check if any value has been removed from the parent form
            for (int i = 0; i < removedValues.size(); i++) {
                Integer removed = removedValues.get(i);
                if (removed < loadedObjects.size()) loadedObjects.remove(removed);
            }
        }

        DataHolder holder = form.getHolders().iterator().next();

        for (int i = 0; i < values.length; i++) {
            Object loadedObject = null;
            if (loadedObjects != null && loadedObjects.size() > i) {
                loadedObject = loadedObjects.get(i);
            }

            result.add(formProcessor.persistFormHolder(form, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i, values[i], holder, loadedObject));
        }

        helper.clearRemovedFieldPositions( inputName );
        return result;
    }

    public Form calculateFieldForm(Field field, String formPath, String namespace) {
        if (StringUtils.isEmpty(formPath)) formPath = field.getDefaultSubform();

        return subformFinderService.getFormByPath(formPath, namespace);
    }

    public Object deleteElementInPosition(Form form, String namespace, String field, int position) {
        synchronized (form) {
            FormStatusData statusData = formProcessor.read(form, namespace);
            Object previousValue = statusData.getCurrentValue(field);
            if (previousValue != null) {
                Object[] vals = (Object[]) previousValue;
                if (position < vals.length) {
                    previousValue = ArrayUtils.remove(vals, position);
                } else {
                    log.error("Cannot delete position " + position + " in array with size " + vals.length);
                }
            } else {
                log.error("Cannot delete position " + position + " in null array.");
            }
            return previousValue;
        }
    }

    public Form getCreateForm(Field field, String namespace) {
        try {
            return calculateFieldForm(field, field.getDefaultSubform(), namespace);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public Form getPreviewDataForm(Field field, String namespace) {
        try {
            return calculateFieldForm(field, field.getPreviewSubform(), namespace);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public Form getTableDataForm(Field field, String namespace) {
        try {
            return calculateFieldForm(field, field.getTableSubform(), namespace);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public Form getEditForm(Field field, String namespace) {
        try {
            return calculateFieldForm(field, field.getDefaultSubform(), namespace);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }
}
