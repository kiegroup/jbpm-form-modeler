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
package org.jbpm.formModeler.core.processing.formStatus;

import org.jbpm.formModeler.api.model.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Defines the status of a form with the submited values, each FormStatus is identified with a namespace and a formId.
 */
public class FormStatus implements Serializable {
    private static transient Logger log = LoggerFactory.getLogger(FormStatus.class);

    private Map<String, Object> inputValues = new HashMap<String, Object>();
    private Map lastParameterMap;
    private Form relatedForm;
    private Set wrongFields = new TreeSet();
    private Map<String, List> wrongFieldsMessages = new HashMap<String, List>();
    private String namespace;
    private Map attributes = new HashMap();
    private Map<String, Object> loadedObjects = new HashMap<String, Object>();

    public FormStatus(Form relatedForm, String namespace, Map currentValues) {
        this.relatedForm = relatedForm;
        this.namespace = namespace;
        if (currentValues != null) inputValues.putAll(currentValues);
    }

    public Map getInputValues() {
        return inputValues;
    }

    public Map getLastParameterMap() {
        return lastParameterMap;
    }

    public void setLastParameterMap(Map lastParameterMap) {
        this.lastParameterMap = lastParameterMap;
    }

    public Set getWrongFields() {
        return wrongFields;
    }

    public Form getRelatedForm() {
        return relatedForm;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    protected void clear() {
        inputValues.clear();
        attributes.clear();
        lastParameterMap = null;
    }

    public Map getAttributes() {
        return attributes;
    }

    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

    public void clearFormErrors() {
        wrongFields.clear();
        wrongFieldsMessages.clear();
    }
    
    public void removeWrongField(String fieldName) {
        wrongFields.remove(fieldName);
        wrongFieldsMessages.remove(fieldName);
    }
    
    public List getErrorMessage(String fieldName) {
        return wrongFieldsMessages.get(fieldName);
    }
    
    public void addErrorMessages(String fieldName, List messages) {
        wrongFields.add(fieldName);
        wrongFieldsMessages.put(fieldName, messages);
    }
    
    public Map getWrongFieldsMessages() {
        return wrongFieldsMessages;
    }

    public void setWrongFieldsMessages(Map wrongFieldsMessages) {
        this.wrongFieldsMessages = wrongFieldsMessages;
    }

    public Map<String, Object> getLoadedObjects() {
        return loadedObjects;
    }

    public void setLoadedObjects(Map<String, Object> loadedObjects) {
        this.loadedObjects = loadedObjects;
    }

    public void addLoadedObject(String id, Object obj) {
        loadedObjects.put(id, obj);
    }

    public Object getLoadedObject(String id) {
        return loadedObjects.get(id);
    }
}
