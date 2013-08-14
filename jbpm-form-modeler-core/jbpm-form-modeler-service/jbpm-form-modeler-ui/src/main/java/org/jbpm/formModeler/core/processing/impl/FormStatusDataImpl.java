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

import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.core.processing.formStatus.FormStatus;
import org.apache.commons.collections.CollectionUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 */
public class FormStatusDataImpl implements FormStatusData {

    private static transient Logger log = LoggerFactory.getLogger(FormStatusDataImpl.class.getName());

    private List wrongFields;
    private Map wrongFieldsMessages;
    private boolean valid;
    private boolean empty;
    private Map currentValues;
    private Map currentInputValues;
    private boolean isNew;
    private Map attributes = new HashMap();
    private Map<String, Object> loadedObjects = new HashMap<String, Object>();

    public FormStatusDataImpl(FormStatus status, boolean isNew) {
        setCurrentValues(status.getInputValues() != null ? Collections.unmodifiableMap(status.getInputValues()) : null);
        setCurrentInputValues(status.getLastParameterMap() != null ? Collections.unmodifiableMap(status.getLastParameterMap()) : null);
        setNew(isNew);
        setWrongFields(status.getWrongFields() != null ? Collections.unmodifiableList(new ArrayList(status.getWrongFields())) : null);
        setWrongFieldsMessages(status.getWrongFieldsMessages() != null ? Collections.unmodifiableMap(new HashMap(status.getWrongFieldsMessages())) : null);
        setValid(wrongFields == null || wrongFields.isEmpty());
        loadedObjects.putAll(status.getLoadedObjects());

        Map inputValues = status.getInputValues();
        setEmpty(true);
        Form form = null;
        try {
            form = status.getRelatedForm();
            for (Field field : form.getFormFields() ) {
                Object value = inputValues.get(field.getFieldName());
                if (value != null) {
                    FieldHandler fieldHandler = FormProcessingServices.lookup().getFieldHandlersManager().getHandler(field.getFieldType());
                    if (!fieldHandler.isEmpty(value)) setEmpty(false);
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        setAttributes(status.getAttributes() != null ? Collections.unmodifiableMap(status.getAttributes()) : null);
    }

    public void setAttributes(Map map) {
        attributes = map;
    }

    public void setCurrentInputValues(Map map) {
        currentInputValues = map;
    }

    public List getWrongFields() {
        return wrongFields != null ? wrongFields : Collections.EMPTY_LIST;
    }

    public boolean isValid() {
        return valid;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return empty;
    }

    public Object getCurrentValue(String fieldName) {
        return currentValues.get(fieldName);
    }

    public String getCurrentInputValue(String inputName) {
        if (currentInputValues == null)
            return null;
        String[] params = (String[]) currentInputValues.get(inputName);
        if (params != null && params.length == 1) {
            return params[0];
        }
        return null;
    }

    public Map getCurrentValues() {
        return currentValues != null ? Collections.unmodifiableMap(currentValues) : Collections.EMPTY_MAP;
    }

    public Map getCurrentInputValues() {
        return currentInputValues;
    }

    public void clear() {
        wrongFields = null;
        valid = false;
        currentValues = null;
        isNew = true;
    }

    public void setCurrentValues(Map currentValues) {
        this.currentValues = currentValues;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setWrongFields(List wrongFields) {
        this.wrongFields = wrongFields;
    }

    public void setNew(boolean b) {
        isNew = b;
    }

    public boolean isNew() {
        return isNew;
    }

    public Map getAttributes() {
        return attributes;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("FormStatusData: ");
        sb.append(" valid=").append(valid);
        if (!valid)
            sb.append(" wrongFields=").append(wrongFields);
        sb.append(" currentValues=").append(currentValues);
        sb.append(" currentInputValues=").append(currentInputValues);
        return sb.toString();
    }

    public boolean hasErrorMessage(String fieldName) {
        return !CollectionUtils.isEmpty((Collection) wrongFieldsMessages.get(fieldName));
    }
    
    public List getErrorMessages(String fieldName) {
        return (List) wrongFieldsMessages.get(fieldName);
    }

    public Map getWrongFieldsMessages() {
        return wrongFieldsMessages;
    }

    public void setWrongFieldsMessages(Map<String, String> wrongFieldsMessages) {
        this.wrongFieldsMessages = wrongFieldsMessages;
    }

    @Override
    public Object getLoadedObject(String id) {
        return loadedObjects.get(id);
    }
}
