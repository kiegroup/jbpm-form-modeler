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

import org.slf4j.Logger;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Holds session information for forms.
 */
//@SessionScoped
@ApplicationScoped
public class FormStatusManager implements Serializable {

    public static FormStatusManager lookup() {
        return (FormStatusManager) CDIBeanLocator.getBeanByType(FormStatusManager.class);
    }

    private Logger log = LoggerFactory.getLogger(FormStatusManager.class);

    private transient Map formStatuses = new Hashtable();

    /**
     * Get form status associated with given form id and namespace
     *
     * @param form    form
     * @param namespace namespace
     * @return the form status associated with given form id and namespace
     */
    public FormStatus getFormStatus(Form form, String namespace) {
        namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        return (FormStatus) formStatuses.get(namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId());
    }

    /**
     * Create and store a new form status associated with given form id and namespace
     *
     * @param form    form
     * @param namespace namespace
     * @return the form status associated with given form id and namespace
     */
    public FormStatus createFormStatus(Form form, String namespace, Map<String, Object> currentValues) {
        namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        FormStatus fs = new FormStatus(form, namespace, currentValues);
        formStatuses.put(namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId(), fs);
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
        // Delete this forms tatus and all nested form statuses
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
        // Delete this form status and all nested form statuses
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
        if (fs == null) return null;

        String namespace = fs.getNamespace();
        if (StringUtils.isEmpty(namespace)) return null;

        FormNamespaceData fsd = NamespaceManager.lookup().getNamespace(namespace);
        if (fsd == null) return null;

        return getFormStatus(fsd.getForm(), fsd.getNamespace());
    }
}
