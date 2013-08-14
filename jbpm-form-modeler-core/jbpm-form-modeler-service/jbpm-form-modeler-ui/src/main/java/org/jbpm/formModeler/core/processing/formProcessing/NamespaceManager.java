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
package org.jbpm.formModeler.core.processing.formProcessing;

import org.slf4j.Logger;
import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class NamespaceManager {

    public static NamespaceManager lookup() {
        return (NamespaceManager) CDIBeanLocator.getBeanByType(NamespaceManager.class);
    }

    @Inject
    private SubformFinderService subformFinderService;

    private Logger log = LoggerFactory.getLogger(NamespaceManager.class);

    public String getParentNamespace(String namespace) {
        if (namespace != null) {
            int lastIndex = namespace.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR);
            int previousLastIndex = namespace.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR, lastIndex - 1);
            if (previousLastIndex != -1) {
                String parentNamespace = namespace.substring(0, previousLastIndex);
                if (log.isDebugEnabled())
                    log.debug("Parent namespace for '" + namespace + "' is '" + parentNamespace + "'");
                return parentNamespace;
            }
        }
        if (log.isDebugEnabled())
            log.debug("Parent namespace for '" + namespace + "' is empty string");
        return "";
    }

    public FormNamespaceData getNamespace(String fieldName) {
        if (fieldName != null) {
            int lastIndex = fieldName.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR);
            int previousLastIndex = fieldName.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR, lastIndex - 1);
            if (previousLastIndex != -1) {
                String formIdString = fieldName.substring(previousLastIndex + 1, lastIndex);
                String namespace = fieldName.substring(0, previousLastIndex);
                String fieldNameInParent = fieldName.substring(lastIndex + 1, fieldName.length());
                if (!"_".equals(formIdString)) {
                    Long formId = Long.decode(formIdString);
                    try {
                        Form form = subformFinderService.getFormById(formId, namespace);
                        return new FormNamespaceData(form, namespace, fieldNameInParent);
                    } catch (Exception e) {
                        log.error("Error: ", e);
                    }
                }
            }
        }
        return null;
    }
}
