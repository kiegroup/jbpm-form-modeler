/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.slf4j.Logger;
import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.StringTokenizer;

@ApplicationScoped
public class NamespaceManager {

    public static NamespaceManager lookup() {
        return (NamespaceManager) CDIBeanLocator.getBeanByType(NamespaceManager.class);
    }

    @Inject
    private SubformFinderService subformFinderService;

    private Logger log = LoggerFactory.getLogger(NamespaceManager.class);

    public String generateFieldNamesPace(String namespace, Field field) {
        return generateFieldNamesPace(namespace, field.getForm(), field.getFieldName());
    }

    public String generateFieldNamesPace(String namespace, Form form, String fieldName) {
        return namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + fieldName;
    }

    public String squashInputName(String inputName) {
        inputName = inputName.substring(inputName.indexOf(FormProcessor.NAMESPACE_SEPARATOR));

        StringTokenizer tokenizer = new StringTokenizer(inputName, FormProcessor.NAMESPACE_SEPARATOR);
        String result = "";

        if (tokenizer.countTokens() % 2 != 0) {
            log.warn("Unable to squash field name '{}', wrong number of name parts: {}", inputName, tokenizer.countTokens());
            System.out.println("Error! " + tokenizer.countTokens());
            return null;
        }

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (i % 2 != 0) {
                if (!result.isEmpty()) result += FormProcessor.NAMESPACE_SEPARATOR;
                result += token;
            }

            i++;
        }
        return result;
    }

    public String generateSquashedInputName(String namespace, Field field) {
        namespace = squashNamespace(namespace);

        if (StringUtils.isEmpty(namespace)) return field.getFieldName();

        return  namespace + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
    }

    protected String squashNamespace(String namespace) {
        if (namespace.indexOf(FormProcessor.NAMESPACE_SEPARATOR) == -1) return "";
        namespace = namespace.substring(namespace.indexOf(FormProcessor.NAMESPACE_SEPARATOR));
        StringTokenizer tokenizer = new StringTokenizer(namespace, FormProcessor.NAMESPACE_SEPARATOR);
        String result = "";

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (i % 2 != 0) {
                if (!result.isEmpty()) result += FormProcessor.NAMESPACE_SEPARATOR;
                result += token;
            }

            i++;
        }
        return result;
    }

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

    public FormNamespaceData getRootNamespace( String inputName ) {

        String parentNS;
        while (!StringUtils.isEmpty((parentNS = getParentNamespace( inputName ))) && parentNS.indexOf( FormProcessor.NAMESPACE_SEPARATOR ) != -1) {
            inputName = parentNS;
        }

        if (inputName.indexOf( FormProcessor.CUSTOM_NAMESPACE_SEPARATOR ) != -1) inputName = inputName.substring( 0, inputName.indexOf( FormProcessor.CUSTOM_NAMESPACE_SEPARATOR ) );

        return getNamespace( inputName );
    }
}
