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
package org.jbpm.formModeler.core.processing.impl;

import au.com.bytecode.opencsv.CSVParser;
import org.apache.commons.logging.Log;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.ProcessingMessagedException;
import org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler;
import org.jbpm.formModeler.core.processing.formProcessing.FormChangeProcessor;
import org.jbpm.formModeler.core.processing.formProcessing.FormChangeResponse;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.core.processing.formStatus.FormStatus;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

@ApplicationScoped
public class FormProcessorImpl implements FormProcessor, Serializable {

    @Inject
    private Log log;

    // TODO: fix formulas
    //@Inject
    private FormChangeProcessor formChangeProcessor;

    @Inject
    private FieldHandlersManager fieldHandlersManager;

    @Inject
    FormRenderContextManager formRenderContextManager;

    private CSVParser rangeParser = new CSVParser(';');
    private CSVParser optionParser = new CSVParser(',');

    protected FormStatus getContextFormStatus(FormRenderContext context) {
        return FormStatusManager.lookup().getFormStatus(context.getForm().getId(), context.getUID());
    }

    protected FormStatus getFormStatus(Form form, String namespace) {
        return getFormStatus(form, namespace, new HashMap());
    }

    protected FormStatus getFormStatus(Form form, String namespace, Map currentValues) {
        FormStatus formStatus = FormStatusManager.lookup().getFormStatus(form.getId(), namespace);
        return formStatus != null ? formStatus : createFormStatus(form, namespace, currentValues);
    }

    protected boolean existsFormStatus(Long formId, String namespace) {
        FormStatus formStatus = FormStatusManager.lookup().getFormStatus(formId, namespace);
        return formStatus != null;
    }

    protected FormStatus createFormStatus(Form form, String namespace, Map currentValues) {
        FormStatus fStatus = FormStatusManager.lookup().createFormStatus(form.getId(), namespace);
        setDefaultValues(form, namespace, currentValues);
        return fStatus;
    }

    protected void setDefaultValues(Form form, String namespace, Map currentValues) {

        if (form != null) {
            Set formFields = form.getFormFields();
            Map params = new HashMap(5);

            Map rangeFormulas = (Map) getAttribute(form, namespace, FormStatusData.CALCULATED_RANGE_FORMULAS);

            if (rangeFormulas == null) {
                rangeFormulas = new HashMap();
                setAttribute(form, namespace, FormStatusData.CALCULATED_RANGE_FORMULAS, rangeFormulas);
            }

            for (Iterator iterator = formFields.iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                Object value = currentValues.get(field.getFieldName());
                String inputName = getPrefix(form, namespace) + field.getFieldName();

                try {
                    FieldHandler handler = fieldHandlersManager.getHandler(field.getFieldType());
                    if ((value instanceof Map && !((Map)value).containsKey(FORM_MODE)) && !(value instanceof I18nSet)) ((Map)value).put(FORM_MODE, currentValues.get(FORM_MODE));
                    Map paramValue = handler.getParamValue(inputName, value, field.getFieldPattern());
                    if (paramValue != null && !paramValue.isEmpty()) params.putAll(paramValue);

                    // Init ranges for simple combos
                    String rangeFormula = field.getRangeFormula();
                    if (!StringUtils.isEmpty(rangeFormula) && rangeFormula.startsWith("{") && rangeFormula.endsWith("}")) {
                        rangeFormula = rangeFormula.substring(1, rangeFormula.length() - 1);

                        String[] options = rangeParser.parseLine(rangeFormula);
                        if (options != null) {
                            Map rangeValues = new TreeMap();
                            for (String option : options) {
                                String[] values = optionParser.parseLine(option);
                                if (values != null && values.length == 2) {
                                    rangeValues.put(values[0], values[1]);
                                }
                            }
                            rangeFormulas.put(field.getFieldName(), rangeValues);
                        }
                    }

                /*
                TODO: implement again formulas for default values

                Object defaultValue = pField.get("defaultValueFormula");
                String inputName = getPrefix(pf, namespace) + pField.getFieldName();
                try {
                    String pattern = (String) pField.get("pattern");
                    Object value = currentValues.get(pField.getFieldName());
                    FieldHandler handler = pField.getFieldType().getManager();
                    if (value == null) {
                        if (defaultValue != null) {
                            if (!defaultValue.toString().startsWith("=")) {
                                log.error("Incorrect formula specified for field " + pField.getFieldName());
                                continue;
                            }
                            if (handler instanceof DefaultFieldHandler)
                                value = ((DefaultFieldHandler) handler).evaluateFormula(pField, defaultValue.toString().substring(1), "", new HashMap(0), "", namespace, new Date());
                        }
                    }
                    if ((value instanceof Map && !((Map)value).containsKey(FormProcessor.FORM_MODE)) && !(value instanceof I18nSet) && !(value instanceof I18nObject)) ((Map)value).put(FormProcessor.FORM_MODE, currentValues.get(FormProcessor.FORM_MODE));
                    Map paramValue = handler.getParamValue(inputName, value, pattern);
                    if (paramValue != null && !paramValue.isEmpty()) params.putAll(paramValue); */
                } catch (Exception e) {
                    log.error("Error obtaining default values for " + inputName, e);
                }
            }
            setValues(form, namespace, params, null, true);
        }
    }

    protected void destroyFormStatus(Form form, String namespace) {
        FormStatusManager.lookup().destroyFormStatus(form.getId(), namespace);
    }

    public void setValues(Form form, String namespace, Map parameterMap, Map filesMap) {
        setValues(form, namespace, parameterMap, filesMap, false);
    }

    public void setValues(Form form, String namespace, Map parameterMap, Map filesMap, boolean incremental) {
        if (form != null) {
            namespace = StringUtils.defaultIfEmpty(namespace, DEFAULT_NAMESPACE);
            FormStatus formStatus = getFormStatus(form, namespace);
            //  if (!incremental) formStatus.getWrongFields().clear();
            if (incremental) {
                Map mergedParameterMap = new HashMap();
                if (formStatus.getLastParameterMap() != null)
                    mergedParameterMap.putAll(formStatus.getLastParameterMap());
                if (parameterMap != null)
                    mergedParameterMap.putAll(parameterMap);
                formStatus.setLastParameterMap(mergedParameterMap);
            } else {
                formStatus.setLastParameterMap(parameterMap);
            }
            String inputsPrefix = getPrefix(form, namespace);
            
            for (Field field : form.getFormFields()) {
                setFieldValue(field, formStatus, inputsPrefix, parameterMap, filesMap, incremental);
            }
        }
    }

    public void modify(Form form, String namespace, String fieldName, Object value) {
        FormStatus formStatus = getFormStatus(form, namespace);
        formStatus.getInputValues().put(fieldName, value);
        propagateChangesToParentFormStatuses(formStatus, fieldName, value);
    }

    public void setAttribute(Form form, String namespace, String attributeName, Object attributeValue) {
        if (form != null) {
            FormStatus formStatus = getFormStatus(form, namespace);
            formStatus.getAttributes().put(attributeName, attributeValue);
        }
    }

    public Object getAttribute(Form form, String namespace, String attributeName) {
        if (form != null){
            FormStatus formStatus = getFormStatus(form, namespace);
            return formStatus.getAttributes().get(attributeName);
        }
        return null;
    }

    protected void setFieldValue(Field field, FormStatus formStatus, String inputsPrefix, Map parameterMap, Map filesMap, boolean incremental) {
        String fieldName = field.getFieldName();
        String inputName = inputsPrefix + fieldName;
        FieldHandler handler = fieldHandlersManager.getHandler(field.getFieldType());
        try {
            Object previousValue = formStatus.getInputValues().get(fieldName);
            boolean isRequired = field.getFieldRequired().booleanValue();
            
            if (!handler.isEvaluable(inputName, parameterMap, filesMap) && !(handler.isEmpty(previousValue) && isRequired)) return;

            Object value = null;
            boolean emptyNumber = false;
            try {
                value = handler.getValue(field, inputName, parameterMap, filesMap, field.getFieldType().getFieldClass(), previousValue);
            } catch (NumericFieldHandler.EmptyNumberException ene) {
                //Treat this case in particular, as returning null in a numeric field would make related formulas not working.
                emptyNumber = true;
            }
            if (incremental && value == null && !emptyNumber) {
                if (log.isDebugEnabled()) log.debug("Refusing to overwrite input value for parameter " + fieldName);
            } else {
                formStatus.getInputValues().put(fieldName, value);
                try {
                    propagateChangesToParentFormStatuses(formStatus, fieldName, value);
                } catch (Exception e) {
                    log.error("Error modifying formStatus: ", e);
                }
                boolean isEmpty = handler.isEmpty(value);
                if (isRequired && isEmpty && !incremental) {
                    log.debug("Missing required field " + fieldName);
                    formStatus.getWrongFields().add(fieldName);
                } else {
                    formStatus.removeWrongField(fieldName);
                }
            }
        } catch (ProcessingMessagedException pme) {
            log.debug("Processing field: ", pme);
            formStatus.addErrorMessages(fieldName, pme.getMessages());
        } catch (Exception e) {
            log.debug("Error setting field value:", e);
            if (!incremental) {
                formStatus.getInputValues().put(fieldName, null);
                formStatus.getWrongFields().add(fieldName);
            }
        }
    }

    protected void propagateChangesToParentFormStatuses(FormStatus formStatus, String fieldName, Object value) {
        FormStatus parent = FormStatusManager.lookup().getParent(formStatus);
        if (parent != null) {
            String fieldNameInParent = NamespaceManager.lookup().getNamespace(formStatus.getNamespace()).getFieldNameInParent();
            Object valueInParent = parent.getInputValues().get(fieldNameInParent);
            if (valueInParent != null) {
                Map parentMapObjectRepresentation = null;
                if (valueInParent instanceof Map) {
                    parentMapObjectRepresentation = (Map) valueInParent;
                } else if (valueInParent instanceof Map[]) {
                    //Take the correct value
                    Map editFieldPositions = (Map) parent.getAttributes().get(FormStatusData.EDIT_FIELD_POSITIONS);
                    if (editFieldPositions != null) {
                        Integer pos = (Integer) editFieldPositions.get(fieldNameInParent);
                        if (pos != null) {
                            parentMapObjectRepresentation = ((Map[]) valueInParent)[pos.intValue()];
                        }
                    }
                }
                if (parentMapObjectRepresentation != null) {
                    //Copy my value to parent
                    parentMapObjectRepresentation.put(fieldName, value);
                    propagateChangesToParentFormStatuses(parent, fieldNameInParent, valueInParent);
                }
            }
        }
    }

    public FormStatusData read(String ctxUid) {
        FormStatusDataImpl data = null;

        try {
            FormRenderContext context = formRenderContextManager.getFormRenderContext(ctxUid);
            if (context == null ) return null;

            FormStatus formStatus = getContextFormStatus(context);

            boolean isNew = formStatus == null;

            if (isNew) {
                formStatus = createContextFormStatus(context);
            }

            data = new FormStatusDataImpl(formStatus, isNew);
        } catch (Exception e) {
            log.error("Error: ", e);
        }

        return data;
    }

    protected FormStatus createContextFormStatus(FormRenderContext context) throws Exception {
        Map values = new HashMap();

        Map<String, Object> bindingData = context.getBindingData();

        if (bindingData != null && !bindingData.isEmpty()) {

            Form form = context.getForm();
            Set<Field> fields = form.getFormFields();

            if (fields != null) {
                for (Field field : form.getFormFields()) {
                    String bindingString = field.getBindingStr();
                    if (!StringUtils.isEmpty(bindingString)) {
                        bindingString = bindingString.substring(1, bindingString.length() - 1);

                        boolean canSetValue = bindingString.indexOf("/") > 0;

                        Object value = null;
                        if (canSetValue) {
                            String holderId = bindingString.substring(0, bindingString.indexOf("/"));
                            String holderFieldId = bindingString.substring(holderId.length() + 1);

                            DataHolder holder = form.getDataHolderById(holderId);
                            if (holder != null && !StringUtils.isEmpty(holderFieldId)) {

                                Object holderValue = bindingData.get(holder.getId());

                                if (holderValue == null) continue;

                                value = holder.readValue(holderValue, holderFieldId);
                            }
                        } else {
                            value = bindingData.get(bindingString);
                        }

                        values.put(field.getFieldName(), value);
                    }
                }
            }
        }

        return getFormStatus(context.getForm(), context.getUID(), values);
    }

    public FormStatusData read(Form form, String namespace, Map currentValues) {
        boolean exists = existsFormStatus(form.getId(), namespace);
        if (currentValues == null) currentValues = new HashMap();
        FormStatus formStatus = getFormStatus(form, namespace, currentValues);
        FormStatusDataImpl data = null;
        try {
            data = new FormStatusDataImpl(formStatus, !exists);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return data;
    }

    public FormStatusData read(Form form, String namespace) {
        return read(form, namespace, new HashMap<String, Object>());
    }

    public void flushPendingCalculations(Form form, String namespace) {
        if (formChangeProcessor != null) {
            formChangeProcessor.process(form, namespace, new FormChangeResponse());//Response is ignored, we just need the session values.
        }
    }

    public void persist(String ctxUid) throws Exception {
        ctxUid = StringUtils.defaultIfEmpty(ctxUid, FormProcessor.DEFAULT_NAMESPACE);
        persist(formRenderContextManager.getFormRenderContext(ctxUid));

    }

    public void persist(FormRenderContext context) throws Exception {
        Form form = context.getForm();

        Map mapToPersist = getFilteredMapRepresentationToPersist(form, context.getUID());

        Map<String, Object> result = new HashMap<String, Object>();

        for (Iterator it = mapToPersist.keySet().iterator(); it.hasNext();) {
            String fieldName = (String) it.next();
            Field field = form.getField(fieldName);
            if (field != null) {
                String bindingString = field.getBindingStr();
                if (!StringUtils.isEmpty(bindingString)) {
                    bindingString = bindingString.substring(1, bindingString.length() - 1);

                    boolean canBind = bindingString.indexOf("/") > 0;

                    if (canBind) {
                        String holderId = bindingString.substring(0, bindingString.indexOf("/"));
                        String holderFieldId = bindingString.substring(holderId.length() + 1);
                        DataHolder holder = form.getDataHolderById(holderId);
                        if (holder != null && !StringUtils.isEmpty(holderFieldId)) {
                            Object value = context.getBindingData().get(holderId);
                            holder.writeValue(value, holderFieldId, mapToPersist.get(fieldName));
                            if (!result.containsKey(holderId)) result.put(holderId, value);
                        }
                        else canBind = false;
                    }

                    if (!canBind) {
                        log.debug("Unable to bind DataHolder for field '" + fieldName + "' to '" + bindingString + "'. This may be caused because bindingString is incorrect or the form doesn't contains the defined DataHolder.");
                        if (!result.containsKey(fieldName)) result.put(fieldName, mapToPersist.get(fieldName));
                    }



                }
            }
        }

        context.setPersistedData(result);
    }

    public Map getMapRepresentationToPersist(Form form, String namespace) throws Exception {
        namespace = StringUtils.defaultIfEmpty(namespace, DEFAULT_NAMESPACE);
        flushPendingCalculations(form, namespace);
        Map m = new HashMap();
        FormStatus formStatus = getFormStatus(form, namespace);
        if (!formStatus.getWrongFields().isEmpty()) {
            throw new IllegalArgumentException("Validation error.");
        }

        fillObjectValues(m, formStatus.getInputValues(), form);

        Set s = (Set) m.get(MODIFIED_FIELD_NAMES);
        if (s == null) {
            m.put(MODIFIED_FIELD_NAMES, s = new TreeSet());
        }
        s.addAll(form.getFieldNames());
        return m;
    }

    protected Map getFilteredMapRepresentationToPersist(Form form, String namespace) throws Exception {
        Map inputValues = getMapRepresentationToPersist(form, namespace);
        Map mapToPersist = filterMapRepresentationToPersist(inputValues);
        return mapToPersist;
    }

    public Map filterMapRepresentationToPersist(Map inputValues) throws Exception {
        Map filteredMap = new HashMap();
        Set keys = inputValues.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            filteredMap.put(key, inputValues.get(key));
        }
        return filteredMap;
    }

    /**
     * Copy to obj values read from status map values
     *
     * @param obj
     * @param values
     * @throws Exception
     */
    protected void fillObjectValues(final Map obj, Map values, Form form) throws Exception {
        Map valuesToSet = new HashMap();
        for (Iterator it = values.keySet().iterator(); it.hasNext();) {
            String propertyName = (String) it.next();
            Object propertyValue = values.get(propertyName);
            valuesToSet.put(propertyName, propertyValue);
        }
        obj.putAll(valuesToSet);
    }

    protected FormManagerImpl getFormsManager() {
        return (FormManagerImpl) CDIBeanLocator.getBeanByType(FormManagerImpl.class);
    }

    @Override
    public void clear(FormRenderContext context) {
        clear(context.getForm(), context.getUID());
    }

    @Override
    public void clear(String ctxUID) {
        clear(formRenderContextManager.getFormRenderContext(ctxUID));
    }

    public void clear(Form form, String namespace) {
        if (log.isDebugEnabled())
            log.debug("Clearing form status for form " + form.getName() + " with namespace '" + namespace + "'");
        destroyFormStatus(form, namespace);
    }

    public void clearField(Form form, String namespace, String fieldName) {
        FormStatus formStatus = getFormStatus(form, namespace);
        formStatus.getInputValues().remove(fieldName);
    }

    public void clearFieldErrors(Form form, String namespace) {
        FormStatusManager.lookup().cascadeClearWrongFields(form.getId(), namespace);
    }

    public void forceWrongField(Form form, String namespace, String fieldName) {
        FormStatusManager.lookup().getFormStatus(form.getId(), namespace).getWrongFields().add(fieldName);
    }

    protected String getPrefix(Form form, String namespace) {
        return namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR;
    }
}
