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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.processing.FormProcessor;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

public abstract class FormChangeProcessor {

    public static final int MAX_ELEMENTS_TO_SHOW = 20;

    private FormChangeProcessor nextProcessor;
    FormProcessingContext context;

    protected String[] supportedFormModes;
    protected String[] sizeRestrictedFormModes;

    @Inject
    protected FieldHandlersManager fieldHandlersManager;

    @Inject
    protected FormProcessor formProcessor;

    @Inject
    protected FormulaReplacementManager replacementManager;

    @Inject
    FormRenderContextManager formRenderContextManager;

    public FormChangeProcessor getNextProcessor() {
        return nextProcessor;
    }

    public void setNextProcessor(FormChangeProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public FormChangeResponse process(Form form, String namespace, FormChangeResponse response) {
        return process(FormProcessingContext.fullProcessingContext(form, namespace, null), response);
    }

    public FormChangeResponse process(Form form, String namespace, String formMode, FormChangeResponse response) {
        return process(FormProcessingContext.fullProcessingContext(form, namespace, formMode), response);
    }

    public FormChangeResponse process(FormProcessingContext context, FormChangeResponse response) {
        if (canProcess(context)) {
            this.context = context;
            response = doProcess(response);
        }
        if (nextProcessor != null) {
            response = nextProcessor.process(context, response);
        }
        return response;
    }

    public abstract FormChangeResponse doProcess(FormChangeResponse response);

    public abstract int getSupportedContextType();

    public boolean canProcess(FormProcessingContext context) {

        //WM workaround to avoid formulas calculation null pointer when the task has been closed
        if (formRenderContextManager.getRootContext(context.getNamespace()) == null) return false;

        if (!context.isFull() && context.getType() != getSupportedContextType()) return false;

        if (StringUtils.isEmpty(context.getFormMode()) || getSupportedFormModes() == null || getSupportedFormModes().length == 0)
            return true;
        return Arrays.asList(getSupportedFormModes()).contains(context.getFormMode());
    }

    public String[] getSupportedFormModes() {
        return supportedFormModes;
    }

    public void setSupportedFormModes(String[] supportedFormModes) {
        this.supportedFormModes = supportedFormModes;
    }

    public boolean restrictCombosSize(String formMode) {
        if (StringUtils.isEmpty(formMode) || getSizeRestrictedFormModes() == null || getSizeRestrictedFormModes().length == 0) return false;
        return Arrays.asList(getSizeRestrictedFormModes()).contains(formMode);
    }

    public String[] getSizeRestrictedFormModes() {
        return sizeRestrictedFormModes;
    }

    public void setSizeRestrictedFormModes(String[] sizeRestrictedFormModes) {
        this.sizeRestrictedFormModes = sizeRestrictedFormModes;
    }

    public int getMaxElementsToShow(Object[] items, String formMode) {
        return getMaxElementsToShow(Arrays.asList(items), formMode);
    }

    public int getMaxElementsToShow(Collection items, String formMode) {
        if (items == null) return 0;
        if (!restrictCombosSize(formMode)) return items.size();
        return items.size() < MAX_ELEMENTS_TO_SHOW ? items.size() : MAX_ELEMENTS_TO_SHOW;
    }

    protected Collection getEvaluableFields() {
        Collection fields = getContextEvaluableFields();

        if (CollectionUtils.isEmpty(fields)) fields = context.getForm().getFieldNames();

        return fields;
    }

    protected Collection getContextEvaluableFields() {
        switch (getSupportedContextType()) {
            case FormProcessingContext.TYPE_FORMULA: return context.getFieldsToEvaluateFormula();
            case FormProcessingContext.TYPE_DEFAULT_FORMULA: return context.getFieldsToEvaluateDefaultFormula();
            case FormProcessingContext.TYPE_RANGE: return context.getFieldsToEvaluateRange();
            case FormProcessingContext.TYPE_STYLE: return context.getFieldsToEvaluateStyle();
        }
        return null;
    }
}