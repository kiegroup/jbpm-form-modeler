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
package org.jbpm.formModeler.core.processing;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.processing.formProcessing.FormulaReplacementManager;
import org.jbpm.formModeler.core.processing.formProcessing.FunctionsProvider;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;

public abstract class DefaultFieldHandler extends AbstractFieldHandler {

    public String getFieldPattern(Field field) {
        if (StringUtils.isEmpty(field.getBag())) return field.getFieldPattern();

        if (!StringUtils.isEmpty(field.getPattern())) return field.getPattern();

        FieldType bagtype = getFieldTypeManager().getTypeByClass(field.getBag());
        if (bagtype != null) return bagtype.getPattern();

        return "";
    }

    public FormulaReplacementManager getReplacementManager() {
        return FormulaReplacementManager.lookup();
    }

    public FunctionsProvider getFunctionsProvider() {
        return FunctionsProvider.lookup();
    }

    public FormManager getFormManager() {
        return FormCoreServices.lookup().getFormManager();
    }

    public NamespaceManager getNamespaceManager() {
        return NamespaceManager.lookup();
    }

    public FormProcessor getFormProcessor() {
        return FormProcessingServices.lookup().getFormProcessor();
    }

    public FormStatusManager getFormStatusManager() {
        return FormStatusManager.lookup();
    }

    public FieldHandlersManager getFieldHandlersManager() {
        return FormProcessingServices.lookup().getFieldHandlersManager();
    }

    public FieldTypeManager getFieldTypeManager() {
        return FormCoreServices.lookup().getFieldTypeManager();
    }
}
