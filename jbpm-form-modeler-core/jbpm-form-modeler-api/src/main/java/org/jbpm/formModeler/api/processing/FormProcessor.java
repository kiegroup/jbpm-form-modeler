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
package org.jbpm.formModeler.api.processing;

import org.jbpm.formModeler.api.model.Form;

import java.util.Map;

/**
 * A FormProcessor is a class that handles a form submission.
 */
public interface FormProcessor {

    public static final String NAMESPACE_SEPARATOR = "-";
    public static final String CUSTOM_NAMESPACE_SEPARATOR = ".";
    public static final String DEFAULT_NAMESPACE = "NS";
    public static final String MODIFIED_FIELD_NAMES = "-jbpm-modifiedFieldNames";
    public static final String FORM_MODE = "-jbpm-formMode";
    public static final String ATTR_INTERPRETER = "-jbpm-formulasInterpreter";


    /**
     * Get submit values from a request parameter map and a files map, and store it in
     * a form status.
     *
     * @param form         Formulary to process
     * @param parameterMap Parameters map, as extracted from a request.
     * @param filesMap     files map, where a parameter name points to a file
     * @param namespace    Form namespace
     */
    public void setValues(Form form, String namespace, Map parameterMap, Map filesMap);

    /**
     * Get submit values from a request parameter map and a files map, and store it in
     * a form status.
     *
     * @param form         Formulary to process
     * @param parameterMap Parameters map, as extracted from a request.
     * @param filesMap     files map, where a parameter name points to a file
     * @param incremental  Determines if form status should be cleared before setting the parameter values
     * @param namespace    Form namespace
     */
    public void setValues(Form form, String namespace, Map parameterMap, Map filesMap, boolean incremental);

    /**
     * Directly modify the status value for a form field.
     *
     * @param form      Formulary to modify
     * @param namespace Form namespace
     * @param fieldName Field name to modify
     * @param value     new value to set for this field.
     */
    public void modify(Form form, String namespace, String fieldName, Object value);

    /**
     * Sets an attribute for given form status
     *
     * @param form           Formulary to process
     * @param namespace      Form namespace
     * @param attributeName  Attribute name
     * @param attributeValue Attribute value
     */
    public void setAttribute(Form form, String namespace, String attributeName, Object attributeValue);

    /**
     * Gets an attribute for given form status
     *
     * @param form          Formulary to process
     * @param namespace     Form namespace
     * @param attributeName Attribute name
     * @return the attribute value
     */
    public Object getAttribute(Form form, String namespace, String attributeName);

    /**
     * Read status for given form id.
     *
     * @param form    Form id to read
     * @param namespace Form namespace
     * @return a FormStatusData object representing the form status
     */
    public FormStatusData read(Form form, String namespace);

    /**
     * Read status for given form id.
     *
     * @param form    Form to read
     * @param namespace Form namespace
     * @param bindingData Values to load into the status
     * @return a FormStatusData object representing the form status
     */
    public FormStatusData read(Form form, String namespace, Map<String, Object> bindingData);

    /**
     * Calculates all formulas for given form. Should be called before reading status, otherwise, some
     * calculations might be performed afterwards (they may arrive asynchronously through ajax).
     *
     * @param form      Formulary to store
     * @param namespace Form namespace
     */
    public void flushPendingCalculations(Form form, String namespace);

    /**
     * Returns the Map to persis based on the data stored on the data stored on a FormStatus
     *
     * @param form The form that corresponds the FormStatus
     * @param namespace The namespace that identifies the FormStatus
     * @return The object to persist
     * @throws Exception in case of error building the Map
     */
    public Map getMapRepresentationToPersist(Form form, String namespace) throws Exception;

    /**
     * Loads into form status values read from a persistent object.
     *
     * @param formId        Form id to fill in
     * @param objIdentifier object identifier to read
     * @param itemClassName item class name to read
     * @param namespace     Form namespace
     * @throws Exception in case of error reading.
     */
    public void load(Long formId, String namespace, Long objIdentifier, String itemClassName) throws Exception;

    /**
     * Loads into form status values read from a persistent object.
     *
     * @param formId        Form id to fill in
     * @param objIdentifier object identifier to read
     * @param itemClassName item class name to read
     * @param namespace     Form namespace
     * @param formMode      Form mode for the form that's loading the status
     * @throws Exception in case of error reading.
     */
    public void load(Long formId, String namespace, Long objIdentifier, String itemClassName, String formMode) throws Exception;

    /**
     * Loads into form status values read from given object.
     *
     * @param formId     Form id to fill in
     * @param loadObject Object to load
     * @param namespace  Form namespace
     */
    public void load(Long formId, String namespace, Object loadObject) throws Exception;

    /**
     * Loads into form status values read from given object.
     *
     * @param formId     Form id to fill in
     * @param loadObject Object to load
     * @param formMode      Form mode for the form that's loading the status
     * @param namespace  Form namespace
     */
    public void load(Long formId, String namespace, Object loadObject, String formMode) throws Exception;

    /**
     * If an object was loaded for given form id, return the loaded object
     *
     * @param formId    form id to read.
     * @param namespace Form namespace
     * @return The loaded object if any
     * @throws Exception in case of error reading persisted object
     */
    public Object getLoadedObject(Long formId, String namespace) throws Exception;

    /**
     * Clears status data for given form id.
     *
     * @param formId    Formulary id to clear
     * @param namespace Form namespace
     */
    public void clear(Long formId, String namespace);

    /**
     * Clear a field value. Just sets to null related status entry
     *
     * @param formId    Formulary id to process
     * @param namespace Form namespace
     * @param fieldName Field name to clear
     */
    void clearField(Long formId, String namespace, String fieldName);

    /**
     * Remove field errors that might be in this formStatus. A new call to setValues() might restore
     * them if the condition that made them be errors still holds
     *
     * @param form      Form to clear
     * @param namespace Namespace to clear
     */
    void clearFieldErrors(Form form, String namespace);

    /**
     * Mark a field as wrong
     *
     * @param form      form to modify
     * @param namespace namespace
     * @param fieldName field name to mark as wrong
     */
    void forceWrongField(Form form, String namespace, String fieldName);
}
