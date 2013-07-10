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
package org.jbpm.formModeler.core.processing.fieldHandlers;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.rendering.SubformFinderService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named("org.jbpm.formModeler.core.processing.fieldHandlers.CreateDynamicObjectFieldHandler")
public class CreateDynamicObjectFieldHandler extends SubformFieldHandler {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CreateDynamicObjectFieldHandler.class.getName());

    public static final String CODE = "subformMultiple";

    @Inject
    private SubformFinderService subformFinderService;

    public CreateDynamicObjectFieldHandler() {
        setPageToIncludeForDisplaying("/formModeler/fieldHandlers/CreateDynamicObject/show.jsp");
        setPageToIncludeForRendering("/formModeler/fieldHandlers/CreateDynamicObject/input.jsp");
        setPageToIncludeForSearching("/formModeler/fieldHandlers/CreateDynamicObject/search.jsp");
    }

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
                getFormProcessor().setValues(form, namespace, parametersMap, filesMap);
                FormStatusData status = getFormProcessor().read(form, namespace);
                if (status.isValid()) {
                    final Map objectCreated = getFormProcessor().getMapRepresentationToPersist(form, namespace);

                    if (previousValuesMap[i] != null) previousValuesMap[i].putAll(objectCreated);
                    else previousValuesMap[i] = objectCreated;
                }
            }
        }

        String[] createParams = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
        boolean doCreate = createParams != null && createParams.length == 1 && createParams[0].equals("true");
        if (doCreate) {
            Form createForm = getCreateForm(field, inputName);
            boolean addItemEnabled = Boolean.TRUE.equals(getFormProcessor().getAttribute(createForm, inputName, FormStatusData.DO_THE_ITEM_ADD));
            if (addItemEnabled) {
                getFormProcessor().setValues(createForm, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create", parametersMap, filesMap);
                FormStatusData status = getFormProcessor().read(createForm, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                if (status.isValid()) {
                    final Map objectCreated = getFormProcessor().getMapRepresentationToPersist(createForm, inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                    if (previousValuesMap == null) previousValuesMap = new Map[0];
                    previousValuesMap = (Map[]) ArrayUtils.add(previousValuesMap, objectCreated);
                    // Not the correct place to do it !!!  form.getProcessor().clear(form.getDbid(), inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
                    // Collapse form
                    Form parentForm = field.getForm();
                    String parentNamespace = getNamespaceManager().getParentNamespace(inputName);
                    Set expandedFields = (Set) getFormProcessor().getAttribute(parentForm, parentNamespace, FormStatusData.EXPANDED_FIELDS);
                    if (expandedFields != null) {
                        expandedFields.remove(field.getFieldName());
                        getFormProcessor().setAttribute(parentForm, parentNamespace, FormStatusData.EXPANDED_FIELDS, expandedFields);
                    }
                    getFormProcessor().clear(form, parentNamespace);
                }
            }
        }

        String[] editParams = (String[]) parametersMap.get(inputName + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "saveEdited");
        boolean doSaveEdited = editParams != null && editParams.length == 1 && editParams[0].equals("true");
        if (doSaveEdited) {
            // Collapse form
            Form parentForm = field.getForm();
            String parentNamespace = getNamespaceManager().getParentNamespace(inputName);
            Map expandedFields = (Map) getFormProcessor().getAttribute(parentForm, parentNamespace, FormStatusData.EDIT_FIELD_POSITIONS);
            if (expandedFields != null && !expandedFields.isEmpty()) {
                Integer positionStr = (Integer) expandedFields.get(field.getFieldName());
                int position = positionStr.intValue();
                Form editForm = getEditForm(field, inputName);
                getFormProcessor().setValues(editForm, inputName, parametersMap, filesMap);
                FormStatusData status = getFormProcessor().read(editForm, inputName);
                if (status.isValid()) {
                    final Map objectCreated = getFormProcessor().getMapRepresentationToPersist(editForm, inputName);
                    previousValuesMap[position].putAll(objectCreated);
                    getFormProcessor().clear(editForm, inputName);
                    //Collapse form
                    expandedFields.remove(field.getFieldName());
                    getFormProcessor().setAttribute(parentForm, parentNamespace, FormStatusData.EDIT_FIELD_POSITIONS, expandedFields);
                }
            }
        }
        return previousValuesMap;
    }

    @Override
    public Map getParamValue(String inputName, Object value, String pattern) {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Object getStatusValue(Field field, String inputName, Object value) {
        if (value == null) return new Map[0];
        Form form = getEnterDataForm(inputName, field);
        DataHolder holder = form.getHolders().iterator().next();

        List values = (List) value;
        Map[] result = new Map[values.size()];

        for (int i = 0; i < values.size(); i++) {
            try {
                Object val = values.get(i);
                Map<String, Object> inputData = new HashMap();
                if (!StringUtils.isEmpty(holder.getInputId())) inputData.put(holder.getInputId(), val);

                Map<String, Object> outputData = new HashMap();

                if (!StringUtils.isEmpty(holder.getOuputId())) outputData.put(holder.getOuputId(), val);

                result[i] = getFormProcessor().createFieldContextValueFromHolder(form, inputName, inputData, outputData, new HashMap<String, Object>(), holder);
            } catch (Exception e) {
                log.error("Error getting status value for field: " + inputName, e);
            }
        }

        Map<String, Object> loadedObjects = new HashMap();
        loadedObjects.put(holder.getUniqeId(), result);

        getFormProcessor().read(form, inputName, null, loadedObjects);

        return result;
    }

    @Override
    public Object persist(Field field, String inputName) throws Exception {
        FormStatusData data = getFormProcessor().read(field.getForm(), getNamespaceManager().getParentNamespace(inputName));

        Object objectValue = data.getCurrentValue(field.getFieldName());
        if (objectValue == null) return null;

        Map[] values = (Map[]) objectValue;
        List result = new ArrayList();
        Form form = getEnterDataForm(inputName, field);

        for (int i = 0; i < values.length; i++) {
            result.add(getFormProcessor().persistFormHolder(form, inputName, values[i], form.getHolders().iterator().next()));
        }

        return result;
    }

    public Form calculateFieldForm(Field field, String formPath, String namespace) {
        if (StringUtils.isEmpty(formPath)) formPath = field.getDefaultSubform();

        return subformFinderService.getFormFromPath(formPath, namespace);
    }

    public Object deleteElementInPosition(Form form, String namespace, String field, int position) {
        synchronized (form) {
            FormStatusData statusData = getFormProcessor().read(form, namespace);
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