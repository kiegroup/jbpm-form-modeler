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

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;

import java.util.List;

public class FormProcessingContext {
    public static final int TYPE_FULL = 0;
    public static final int TYPE_FORMULA = 1;
    public static final int TYPE_RANGE = 2;
    public static final int TYPE_STYLE = 3;
    public static final int TYPE_DEFAULT_FORMULA = 4;
    
    private int type;
    private Form form;
    private String namespace;
    private String formMode;
    private List<String> fieldsToEvaluateFormula;
    private List<String> fieldsToEvaluateRange;
    private List<String> fieldsToEvaluateStyle;
    private List<String> fieldsToEvaluateDefaultFormula;

    
    public static FormProcessingContext fullProcessingContext(Form form, String namespace, String formMode) {
        return new FormProcessingContext(TYPE_FULL, form, namespace, formMode, null);
    }

    public static FormProcessingContext formulaProcessingContext(Form form, String namespace, String formMode, List<String> fieldsToEvaluateFormula) {
        return new FormProcessingContext(TYPE_FORMULA, form, namespace, formMode, fieldsToEvaluateFormula);
    }
    
    public static FormProcessingContext rangeProcessingContext(Form form, String namespace, String formMode, List<String> fieldsToEvaluateRange) {
        return new FormProcessingContext(TYPE_RANGE, form, namespace, formMode, fieldsToEvaluateRange);
    }

    public static FormProcessingContext styleProcessingContext(Form form, String namespace, String formMode, List<String> fieldsToEvaluateStyle) {
        return new FormProcessingContext(TYPE_STYLE, form, namespace, formMode, fieldsToEvaluateStyle);
    }

    public static FormProcessingContext defaultFormulaProcessingContext(Form form, String namespace) {
        return new FormProcessingContext(TYPE_DEFAULT_FORMULA, form, namespace, null, null);
    }

    private FormProcessingContext(int type, Form form, String namespace, String formMode, List<String> fieldsToEvaluate) {
        this.type = type;
        this.form = form;

        this.namespace = StringUtils.defaultIfEmpty(namespace, FormProcessor.DEFAULT_NAMESPACE);
        this.formMode = StringUtils.defaultString(formMode);
        switch (type) {
            case TYPE_FORMULA: this.fieldsToEvaluateFormula = fieldsToEvaluate;
                break;
            case TYPE_RANGE: this.fieldsToEvaluateRange = fieldsToEvaluate;
                break;
            case TYPE_STYLE: this.fieldsToEvaluateStyle = fieldsToEvaluate;
                break;
            case TYPE_DEFAULT_FORMULA: this.fieldsToEvaluateDefaultFormula = fieldsToEvaluate;
                break;
        }
    }

    public List<String> getFieldsToEvaluateStyle() {
        return fieldsToEvaluateStyle;
    }

    public void setFieldsToEvaluateStyle(List<String> fieldsToEvaluateStyle) {
        this.fieldsToEvaluateStyle = fieldsToEvaluateStyle;
    }

    public List<String> getFieldsToEvaluateFormula() {
        return fieldsToEvaluateFormula;
    }

    public void setFieldsToEvaluateFormula(List<String> fieldsToEvaluateFormula) {
        this.fieldsToEvaluateFormula = fieldsToEvaluateFormula;
    }

    public List<String> getFieldsToEvaluateDefaultFormula() {
        return fieldsToEvaluateDefaultFormula;
    }

    public void setFieldsToEvaluateDefaultFormula(List<String> fieldsToEvaluateDefaultFormula) {
        this.fieldsToEvaluateDefaultFormula = fieldsToEvaluateDefaultFormula;
    }

    public List getFieldsToEvaluateRange() {
        return fieldsToEvaluateRange;
    }

    public Form getForm() {
        return form;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFormMode() {
        return formMode;
    }

    public int getType() {
        return type;
    }

    public boolean isFull() {
        return TYPE_FULL == type;
    }

    public boolean isFormula() {
        return TYPE_FORMULA == type;
    }

    public boolean isRange() {
        return TYPE_RANGE == type;
    }
}
