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

import org.jbpm.formModeler.core.processing.ProcessingMessagedException;
import org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler;
import org.jbpm.formModeler.core.processing.formProcessing.FormChangeProcessor;
import org.jbpm.formModeler.core.processing.formProcessing.FormChangeResponse;
import org.jbpm.formModeler.core.processing.formStatus.FormStatus;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.processing.FieldHandler;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.processing.FormStatusData;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.FactoryWork;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

@ApplicationScoped
public class FormProcessorImpl implements FormProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormProcessorImpl.class.getName());

    private FormChangeProcessor formChangeProcessor;

    @Inject
    private FormStatusManager formStatusManager;

    public FormStatusManager getFormStatusManager() {
        return formStatusManager;
    }

    public void setFormStatusManager(FormStatusManager formStatusManager) {
        this.formStatusManager = formStatusManager;
    }

    public FormChangeProcessor getFormChangeProcessor() {
        return formChangeProcessor;
    }

    public void setFormChangeProcessor(FormChangeProcessor formChangeProcessor) {
        this.formChangeProcessor = formChangeProcessor;
    }

    protected FormStatus getFormStatus(Long formId, String namespace) {
        return getFormStatus(formId, namespace, new HashMap());
    }

    protected FormStatus getFormStatus(Long formId, String namespace, Map currentValues) {
        FormStatus formStatus = getFormStatusManager().getFormStatus(formId, namespace);
        return formStatus != null ? formStatus : createFormStatus(formId, namespace, currentValues);
    }

    protected boolean existsFormStatus(Long formId, String namespace) {
        FormStatus formStatus = getFormStatusManager().getFormStatus(formId, namespace);
        return formStatus != null;
    }

    protected FormStatus createFormStatus(Long formId, String namespace) {
        return createFormStatus(formId, namespace, new HashMap());
    }

    protected FormStatus createFormStatus(Long formId, String namespace, Map currentValues) {
        FormStatus fStatus = formStatusManager.createFormStatus(formId, namespace);
        setDefaultValues(formId, namespace, currentValues);
        return fStatus;
    }

    protected void setDefaultValues(Long formId, String namespace, Map currentValues) {
        Form pf = null;
        try {
            pf = getFormsManager().getFormById(formId);
        } catch (Exception e) {
            log.error("Error recovering Form with id " + formId + ", no field default values will be set", e);
        }

        if (pf != null) {
            Set formFields = pf.getFormFields();
            Map params = new HashMap(5);
            for (Iterator iterator = formFields.iterator(); iterator.hasNext(); ) {
                Field pField = (Field) iterator.next();
                Object value = currentValues.get(pField.getFieldName());
                String inputName = getPrefix(pf, namespace) + pField.getFieldName();

                try {
                    FieldHandler handler = (FieldHandler) Factory.lookup(pField.getFieldType().getManagerClass());
                    if ((value instanceof Map && !((Map) value).containsKey(FORM_MODE)) && !(value instanceof I18nSet))
                        ((Map) value).put(FORM_MODE, currentValues.get(FORM_MODE));
                    Map paramValue = handler.getParamValue(inputName, value, pField.getPattern());
                    if (paramValue != null && !paramValue.isEmpty()) params.putAll(paramValue);

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
            setValues(pf, namespace, params, null, true);
        }
    }

    protected void destroyFormStatus(Long formId, String namespace) {
        formStatusManager.destroyFormStatus(formId, namespace);
    }

    public void setValues(Form form, String namespace, Map parameterMap, Map filesMap) {
        setValues(form, namespace, parameterMap, filesMap, false);
    }

    public void setValues(Form form, String namespace, Map parameterMap, Map filesMap, boolean incremental) {
        if (form != null) {

            namespace = StringUtils.defaultIfEmpty(namespace, DEFAULT_NAMESPACE);
            FormStatus formStatus = getFormStatus(form.getId(), namespace);
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
        FormStatus formStatus = getFormStatus(form.getId(), namespace);
        formStatus.getInputValues().put(fieldName, value);
        propagateChangesToParentFormStatuses(formStatus, fieldName, value);
    }

    public void setAttribute(Form form, String namespace, String attributeName, Object attributeValue) {
        if (form != null) {
            FormStatus formStatus = getFormStatus(form.getId(), namespace);
            formStatus.getAttributes().put(attributeName, attributeValue);
        }
    }

    public Object getAttribute(Form form, String namespace, String attributeName) {
        if (form != null) {
            FormStatus formStatus = getFormStatus(form.getId(), namespace);
            return formStatus.getAttributes().get(attributeName);
        }
        return null;
    }

    protected void setFieldValue(Field field, FormStatus formStatus, String inputsPrefix, Map parameterMap, Map filesMap, boolean incremental) {
        String fieldName = field.getFieldName();
        String inputName = inputsPrefix + fieldName;
        FieldHandler handler = (FieldHandler) Factory.lookup(field.getFieldType().getManagerClass());
        try {
            Object previousValue = formStatus.getInputValues().get(fieldName);
            boolean isRequired = field.getFieldRequired().booleanValue();

            if (!handler.isEvaluable(inputName, parameterMap, filesMap) && !(handler.isEmpty(previousValue) && isRequired))
                return;

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
        FormStatus parent = getFormStatusManager().getParent(formStatus);
        if (parent != null) {
            String fieldNameInParent = getFormStatusManager().getNamespaceManager().getNamespace(formStatus.getNamespace()).getFieldNameInParent();
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

    public FormStatusData read(final Long formId, final String namespace, final Map currentValues) {
        final FormStatusDataImpl[] data = new FormStatusDataImpl[1];

        /*
            This Factory.doWork is needed to read the form data outside Factory context.
            This must be on CDI migration
         */
        Factory.doWork(new FactoryWork() {
            public void doWork() {
                boolean exists = existsFormStatus(formId, namespace);
                Map values = currentValues;
                if (values == null) values = new HashMap();
                FormStatus formStatus = getFormStatus(formId, namespace, values);

                try {
                    data[0] = new FormStatusDataImpl(formStatus, !exists);
                } catch (Exception e) {
                    log.error("Error: ", e);
                }

            }
        });
        return data[0];
    }

    public FormStatusData read(Long formId, String namespace) {
        return read(formId, namespace, new HashMap());
    }

    public void flushPendingCalculations(Form form, String namespace) {
        if (getFormChangeProcessor() != null)
            getFormChangeProcessor().process(form, namespace, new FormChangeResponse());//Response is ignored, we just need the session values.
    }

    public Map getMapRepresentationToPersist(Form form, String namespace) throws Exception {
        namespace = StringUtils.defaultIfEmpty(namespace, DEFAULT_NAMESPACE);
        flushPendingCalculations(form, namespace);
        Map m = new HashMap();
        FormStatus formStatus = getFormStatus(form.getId(), namespace);
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
        for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
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
        for (Iterator it = values.keySet().iterator(); it.hasNext(); ) {
            String propertyName = (String) it.next();
            Object propertyValue = values.get(propertyName);
            valuesToSet.put(propertyName, propertyValue);
        }
        obj.putAll(valuesToSet);
    }

    public void load(Long formId, String namespace, Long objIdentifier, String itemClassName) throws Exception {
        load(formId, namespace, objIdentifier, itemClassName, null);
    }

    public void load(Long formId, String namespace, Long objIdentifier, String itemClassName, String formMode) throws Exception {
        namespace = StringUtils.defaultIfEmpty(namespace, DEFAULT_NAMESPACE);
        FormStatus formStatus = createFormStatus(formId, namespace);
        formStatus.setLoadedItemId(objIdentifier);
        formStatus.setLoadedItemClass(itemClassName);
        load(formId, namespace, getLoadedObject(formId, namespace), formMode);
    }

    public void load(Long formId, String namespace, Object loadObject) throws Exception {
        load(formId, namespace, loadObject, null);
    }

    public void load(Long formId, String namespace, Object loadObject, String formMode) throws Exception {
        namespace = StringUtils.defaultIfEmpty(namespace, DEFAULT_NAMESPACE);
        if (loadObject == null) { //Clear loaded object id preserving fields
            FormStatus formStatus = getFormStatus(formId, namespace);
            formStatus.setLoadedItemId(null);
            formStatus.setLoadedItemClass(null);
            // Simulate a fake form submission with no filled in fields, so that all values are properly set internally.
            setValues(getFormsManager().getFormById(formId), namespace, Collections.EMPTY_MAP, Collections.EMPTY_MAP, true);
        } else
            synchronized (loadObject) {
                FormStatus formStatus = getFormStatus(formId, namespace, (Map) loadObject);
                if (loadObject instanceof Map) {
                    Map obj = (Map) loadObject;
                    Iterator it = obj.keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        Object value = obj.get(key);
                        if (value != null)
                            formStatus.getInputValues().put(key, value);
                    }
                }
            }
        //Calculate formulas
        /*
        TODO: evaluate formulas
        if (getFormChangeProcessor() != null)
            getFormChangeProcessor().process(getFormsManager().getFormById(formId), namespace, formMode, new FormChangeResponse());//Response is ignored, we just need the session values.
            */

    }

    protected FormManagerImpl getFormsManager() {
        return (FormManagerImpl) CDIHelper.getBeanByType(FormManagerImpl.class);
    }

    public Object getLoadedObject(Long formId, String namespace) throws Exception {
        FormStatus formStatus = getFormStatus(formId, namespace);
        Object loadedObject = null;
        if (formStatus != null) {
            final Serializable objIdentifier = formStatus.getLoadedItemId();
            final String itemClassName = formStatus.getLoadedItemClass();
            //TODO load data from object here!
        }
        return loadedObject;
    }

    public void clear(Long formId, String namespace) {
        if (log.isDebugEnabled())
            log.debug("Clearing form status for formulary " + formId + " with namespace '" + namespace + "'");
        destroyFormStatus(formId, namespace);
    }

    public void clearField(Long formId, String namespace, String fieldName) {
        FormStatus formStatus = getFormStatus(formId, namespace);
        formStatus.getInputValues().remove(fieldName);
    }

    public void clearFieldErrors(Form form, String namespace) {
        formStatusManager.cascadeClearWrongFields(form.getId(), namespace);
    }

    public void forceWrongField(Form form, String namespace, String fieldName) {
        formStatusManager.getFormStatus(form.getId(), namespace).getWrongFields().add(fieldName);
    }

    // OLD deprecated methods (before namespaces)

    public void clear(Long formId) {
        clear(formId, "");
    }

    public Object getLoadedObject(Long formId) throws Exception {
        return getLoadedObject(formId, "");
    }

    public void load(Long formId, Object loadObject) throws Exception {
        load(formId, "", loadObject);
    }

    public void load(Long formId, Long objIdentifier, String itemClassName) throws Exception {
        load(formId, "", objIdentifier, itemClassName);
    }

    public FormStatusData read(Long formId) {
        return read(formId, "");
    }

    public void setValues(Form form, Map parameterMap, Map filesMap) {
        setValues(form, "", parameterMap, filesMap);
    }

    public void setValues(Form form, Map parameterMap, Map filesMap, boolean incremental) {
        setValues(form, "", parameterMap, filesMap, incremental);
    }

    protected String getPrefix(Form form, String namespace) {
        return namespace + NAMESPACE_SEPARATOR + form.getId() + NAMESPACE_SEPARATOR;
    }
}
