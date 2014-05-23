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

import org.apache.commons.jxpath.JXPathContext;
import org.jbpm.formModeler.core.processing.formProcessing.*;
import org.slf4j.Logger;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.config.RangeProviderManager;
import org.jbpm.formModeler.core.processing.*;
import org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler;
import org.jbpm.formModeler.core.processing.formStatus.FormStatus;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.core.util.BindingExpressionUtil;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

@ApplicationScoped
public class FormProcessorImpl implements FormProcessor, Serializable {

    private Logger log = LoggerFactory.getLogger(FormProcessor.class);

    // TODO: fix formulas
    @Inject
    private FormulasCalculatorChangeProcessor formChangeProcessor;

    @Inject
    private RangeProviderManager rangeProviderManager;

    @Inject
    private DefaultFormulaProcessor defaultFormulaProcessor;

    @Inject
    private FieldHandlersManager fieldHandlersManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    private BindingExpressionUtil bindingExpressionUtil = BindingExpressionUtil.getInstance();

    protected FormStatus getContextFormStatus(FormRenderContext context) {
        return FormStatusManager.lookup().getFormStatus(context.getForm(), context.getUID());
    }

    protected FormStatus getFormStatus(Form form, String namespace) {
        return getFormStatus(form, namespace, new HashMap(), new HashMap<String, Object>());
    }

    protected FormStatus getFormStatus(Form form, String namespace, Map<String, Object> currentValues, Map<String, Object> loadedObjects) {
        FormStatus formStatus = FormStatusManager.lookup().getFormStatus(form, namespace);
        return formStatus != null ? formStatus : createFormStatus(form, namespace, currentValues, loadedObjects);
    }

    protected boolean existsFormStatus(Form form, String namespace) {
        FormStatus formStatus = FormStatusManager.lookup().getFormStatus(form, namespace);
        return formStatus != null;
    }

    protected FormStatus createFormStatus(Form form, String namespace, Map currentValues, Map<String, Object> loadedObjects) {
        FormStatus fStatus = FormStatusManager.lookup().createFormStatus(form, namespace, currentValues);
        fStatus.setLoadedObjects(loadedObjects);
        setDefaultValues(form, namespace, currentValues);
        return fStatus;
    }

    protected void setDefaultValues(Form form, String namespace, Map currentValues) {

        if (form != null) {
            Set formFields = form.getFormFields();

            Map rangeFormulas = (Map) getAttribute(form, namespace, FormStatusData.CALCULATED_RANGE_FORMULAS);

            if (rangeFormulas == null) {
                rangeFormulas = new HashMap();
                setAttribute(form, namespace, FormStatusData.CALCULATED_RANGE_FORMULAS, rangeFormulas);
            }

            for (Iterator iterator = formFields.iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                String inputName = getPrefix(form, namespace) + field.getFieldName();

                try {
                    // Init ranges for simple combos
                    String rangeFormula = field.getRangeFormula();

                    if (rangeFormula!=null && rangeFormula.trim().length() > 0) {
                        rangeFormulas.put(field.getFieldName(), rangeProviderManager.getRangeValues(rangeFormula, namespace));
                    }
                } catch (Exception e) {
                    log.error("Error obtaining default values for " + inputName, e);
                }
            }

            defaultFormulaProcessor.process(FormProcessingContext.defaultFormulaProcessingContext(form, namespace), null);
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
                    log.debug("Can't propagate changes to parentFormStatuses: ", e);
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
        Map<String, Object> loadedObjects = new HashMap<String, Object>();
        Map values = readValuesToLoad(context.getForm(), context.getInputData(), context.getOutputData(), loadedObjects, context.getUID());
        return getFormStatus(context.getForm(), context.getUID(), values, loadedObjects);
    }

    @Override
    public Map readValuesToLoad(Form form, Map inputData, Map outputData, Map loadedObjects, String namespace) {
        Map values = new HashMap();
        Set<Field> fields = form.getFormFields();

        if (fields != null) {
            for (Field field : form.getFormFields()) {

                String inputExperession = field.getInputBinding();
                String outputExpression = field.getOutputBinding();

                boolean hasInput = !StringUtils.isEmpty(inputExperession);
                boolean hasOutput = !StringUtils.isEmpty(outputExpression);

                if (!hasInput && !hasOutput) continue;

                Object value;
                if ((hasInput && !hasOutput) || (hasInput && outputData.isEmpty())) {
                    value = readFieldValue(field, inputData, loadedObjects, namespace, true);
                } else {
                    value = readFieldValue(field, outputData, loadedObjects, namespace, false);
                }
                values.put(field.getFieldName(), value);
            }
        }
        return values;
    }


    protected Object readFieldValue(Field field, Map dataToLoad, Map loadedObjects, String namespace, boolean isInput) {
        Form form = field.getForm();

        String bindingExpression = isInput ?  field.getInputBinding() : field.getOutputBinding();

        Object value = null;
        if (!StringUtils.isEmpty(bindingExpression)) {
            DataHolder holder = form.getDataHolderByField(field);

            if (holder == null) value = getUnbindedFieldValue(bindingExpression, dataToLoad);
            else {
                String holderId = isInput ? holder.getInputId() : holder.getOuputId();
                value = getBindedValue(field, holder, holderId, bindingExpression, dataToLoad, loadedObjects, namespace);
            }
        }
        return value;
    }

    protected Object getUnbindedFieldValue(String bindingExpression, Map<String, Object> bindingData) {
        if (bindingExpression.indexOf("/") != -1) {
            try {
                String root = bindingExpression.substring(0, bindingExpression.indexOf("/"));
                String expression = bindingExpression.substring(root.length() + 1);

                Object object = bindingData.get(root);
                JXPathContext ctx = JXPathContext.newContext(object);
                return ctx.getValue(expression);
            } catch (Exception e) {
                log.warn("Error getting value for xpath xpression '" + bindingExpression + "' :", e);
            }
        }
        return bindingData.get(bindingExpression);
    }

    protected Object getBindedValue(Field field, DataHolder holder, String holderId, String bindingExpression, Map data, Map loadedObjects, String namespace) {
        Object value = getValueFromHolder(holder, holderId, bindingExpression, data, loadedObjects);

        FieldHandler handler = fieldHandlersManager.getHandler(field.getFieldType());
        if (handler instanceof PersistentFieldHandler) {
            String inputName = getPrefix(field.getForm(), namespace) + field.getFieldName();
            value = ((PersistentFieldHandler) handler).getStatusValue(field, inputName, value);
        }

        return value;
    }


    protected Object getValueFromHolder(DataHolder holder, String holderId, String bindingExpression, Map<String, Object> bindingData, Map<String, Object> loadedObjects) {
        if (holder != null) {
            Object bindingValue = bindingData.get(holderId);
            try {
                if (bindingValue != null && holder.isAssignableValue(bindingValue)) {
                    loadedObjects.put(holder.getUniqeId(), bindingValue);
                    return holder.readFromBindingExperssion(bindingValue, bindingExpression);
                }
                return null;
            } catch (Exception e) {
                log.warn("Unable to read value from expression '" + bindingExpression + "'. Error: ", e);
            }
        }
        return getUnbindedFieldValue(bindingExpression, bindingData);
    }

    public FormStatusData read(Form form, String namespace, Map formValues) {
        return read(form, namespace, formValues, new HashMap());
    }

    @Override
    public FormStatusData read(Form form, String namespace, Map<String, Object> formValues, Map<String, Object> loadedObjects) {
        boolean exists = existsFormStatus(form, namespace);
        if (formValues == null) formValues = new HashMap();
        FormStatus formStatus = getFormStatus(form, namespace, formValues, loadedObjects);
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

        Map<String, Object> result = context.getOutputData();

        for (Iterator it = mapToPersist.keySet().iterator(); it.hasNext();) {
            String fieldName = (String) it.next();
            Field field = form.getField(fieldName);

            if (field != null) {

                DataHolder holder = form.getDataHolderByField(field);

                String bindingString = field.getOutputBinding();

                if (StringUtils.isEmpty(bindingString)) continue;

                Object value = persistField(field, mapToPersist, holder, context.getUID());

                bindingString = bindingExpressionUtil.extractBindingExpression(bindingString);

                boolean simpleBinding = StringUtils.isEmpty(bindingString) || bindingString.indexOf("/") == -1;

                if (holder == null || simpleBinding) result.put(bindingString, value);
                else {
                    String holderFieldId = bindingString.substring((holder.getOuputId() + "/").length());

                    Object holderOutputValue = result.get(holder.getOuputId());
                    if (holderOutputValue == null || !holder.isAssignableValue(holderOutputValue)) {
                        holderOutputValue = context.getInputData().get(holder.getInputId());
                        if (holderOutputValue == null || !holder.isAssignableValue(holderOutputValue)) holderOutputValue = holder.createInstance(context);
                        result.put(holder.getOuputId(), holderOutputValue);
                    }

                    holder.writeValue(holderOutputValue, holderFieldId, value);
                }
            }
        }
    }

    @Override
    public Object persistFormHolder(Form form, String namespace, Map<String, Object> mapToPersist, DataHolder holder, Object loadedObject) throws Exception {
        if (holder == null) return null;

        if (loadedObject == null) {
            FormRenderContext context = formRenderContextManager.getRootContext(namespace);
            loadedObject = holder.createInstance(context);
        }

        for (Iterator it = mapToPersist.keySet().iterator(); it.hasNext();) {
            String fieldName = (String) it.next();
            Field field = form.getField(fieldName);

            if (field != null && holder.isAssignableForField(field)) {
                String bindingString = field.getOutputBinding();

                if (StringUtils.isEmpty(bindingString)) continue;

                bindingString = bindingExpressionUtil.extractBindingExpression(bindingString);
                String holderFieldId = bindingString.substring(holder.getOuputId().length() + 1);

                Object value = persistField(field, mapToPersist, holder, namespace);

                holder.writeValue(loadedObject, holderFieldId, value);
            }
        }
        return loadedObject;
    }

    protected Object persistField(Field field, Map<String, Object> mapToPersist, DataHolder holder, String namespace) throws Exception{
        String bindingString = field.getOutputBinding();

        if (holder == null && !StringUtils.isEmpty(bindingString)) return mapToPersist.get(field.getFieldName());

        bindingString = bindingExpressionUtil.extractBindingExpression(bindingString);

        boolean complexBinding = bindingString.indexOf("/") > 0;

        if (complexBinding) {
            String holderId = bindingString.substring(0, bindingString.indexOf("/"));
            String holderFieldId = bindingString.substring(holderId.length() + 1);
            if (holder != null && !StringUtils.isEmpty(holderFieldId)) {

                FieldHandler handler = fieldHandlersManager.getHandler(field.getFieldType());

                if (handler instanceof PersistentFieldHandler) {

                    String inputName = getPrefix(field.getForm(), namespace) + field.getFieldName();

                    return ((PersistentFieldHandler) handler).persist(field, inputName);

                } else
                    return mapToPersist.get(field.getFieldName());
            }
        }
        return mapToPersist.get(field.getFieldName());
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
        FormStatusManager.lookup().getFormStatus(form, namespace).getWrongFields().add(fieldName);
    }

    protected String getPrefix(Form form, String namespace) {
        return namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR;
    }
}
