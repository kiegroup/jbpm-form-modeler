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

import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FieldHandler;
import org.jbpm.formModeler.api.processing.FormProcessor;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Manager class for FormStatuses, it has the creation/access methods to the FormStatuses.
 */
public class FormStatusManager {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormStatusManager.class.getName());

    private Map formStatuses = new Hashtable();
    public NamespaceManager namespaceManager;

    /**
     * Get form status associated with given form id and namespace
     *
     * @param formId    form
     * @param namespace namespace
     * @return the form status associated with given form id and namespace
     */
    public FormStatus getFormStatus(Long formId, String namespace) {
        namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        return (FormStatus) formStatuses.get(namespace + FormProcessor.NAMESPACE_SEPARATOR + formId);
    }

    /**
     * Create and store a new form status associated with given form id and namespace
     *
     * @param formId    form
     * @param namespace namespace
     * @return the form status associated with given form id and namespace
     */
    public FormStatus createFormStatus(Long formId, String namespace) {
        namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        FormStatus fs = new FormStatus(formId, namespace);
        formStatuses.put(namespace + FormProcessor.NAMESPACE_SEPARATOR + formId, fs);
        try {
            Form form = namespaceManager.getFormsManager().getFormById(formId);
            for (Field pff : form.getFormFields()) {;
                FieldHandler handler = (FieldHandler) Factory.lookup(pff.getFieldType().getManagerClass());
                handler.initialize(pff, namespace);
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }


        return fs;
    }

    /**
     * Destroy form status associated with given form id and namespace
     *
     * @param formId    form
     * @param namespace namespace
     */
    public void destroyFormStatus(Long formId, String namespace) {
        namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        // Delete this formstatus and all nested form statuses
        String requestedPreffix = namespace + FormProcessor.NAMESPACE_SEPARATOR + formId;
        synchronized (formStatuses) {
            for (Iterator it = formStatuses.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(requestedPreffix)) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Clear wrong fields in given formStatus and all their subforms
     *
     * @param formId    form to clear
     * @param namespace form starting namespace
     */
    public void cascadeClearWrongFields(Long formId, String namespace) {
        namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        // Delete this formstatus and all nested form statuses
        String requestedPreffix = namespace + FormProcessor.NAMESPACE_SEPARATOR + formId;
        for (Iterator it = formStatuses.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (key.startsWith(requestedPreffix)) {
                FormStatus formStatus = (FormStatus) formStatuses.get(key);
                formStatus.clearFormErrors();
            }
        }
    }

    public FormStatus getParent(FormStatus fs) {
        if (fs == null)
            return null;
        String namespace = fs.getNamespace();
        if (StringUtils.isEmpty(namespace))
            return null;
        FormNamespaceData fsd = namespaceManager.getNamespace(namespace);
        if (fsd == null)
            return null;
        return getFormStatus(fsd.getForm().getId(), fsd.getNamespace());
    }
}
