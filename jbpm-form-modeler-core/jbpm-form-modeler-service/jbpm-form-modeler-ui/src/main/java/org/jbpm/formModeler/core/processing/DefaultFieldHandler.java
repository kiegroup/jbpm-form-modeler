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
package org.jbpm.formModeler.core.processing;

import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.processing.formProcessing.FormulaReplacementManager;
import org.jbpm.formModeler.core.processing.formProcessing.FunctionsProvider;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;

public abstract class DefaultFieldHandler extends AbstractFieldHandler {

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
}
