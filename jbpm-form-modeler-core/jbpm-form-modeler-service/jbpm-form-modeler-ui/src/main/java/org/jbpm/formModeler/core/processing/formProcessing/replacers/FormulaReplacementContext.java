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
package org.jbpm.formModeler.core.processing.formProcessing.replacers;

import bsh.EvalError;
import bsh.Interpreter;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class FormulaReplacementContext {
    private static transient Logger log = LoggerFactory.getLogger(FormulaReplacementContext.class.getName());

    private String formula;
    private String paramValue;
    private Field field;
    private String namespace;
    private Date date;
    private boolean beforeFieldEvaluation;
    /**
     * Auxiliary item
     */
    private Map item;

    public FormulaReplacementContext() {
    }

    public FormulaReplacementContext(boolean beforeFieldEvaluation, Date date, Field field, String formula, String namespace, String paramValue) {
        this.beforeFieldEvaluation = beforeFieldEvaluation;
        this.date = date;
        this.field = field;
        this.formula = formula;
        this.namespace = namespace;
        this.paramValue = paramValue;
    }

    /**
     * Creates a new context as a copy of an existing one.
     *
     * @param ctx
     */
    public FormulaReplacementContext(FormulaReplacementContext ctx) {
        this(ctx.isBeforeFieldEvaluation(), ctx.getDate(), ctx.getField(), ctx.getFormula(), ctx.getNamespace(), ctx.getParamValue());
    }


    public boolean isBeforeFieldEvaluation() {
        return beforeFieldEvaluation;
    }

    public void setBeforeFieldEvaluation(boolean beforeFieldEvaluation) {
        this.beforeFieldEvaluation = beforeFieldEvaluation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getFieldInputName() {
        return new StringBuffer().append(namespace).append(FormProcessor.NAMESPACE_SEPARATOR).
                append(field.getForm().getId()).append(FormProcessor.NAMESPACE_SEPARATOR).
                append(field.getFieldName()).toString();
    }

    public Map getItem() {
        return item;
    }

    public void setItem(Map item) {
        this.item = item;
    }

    public FormulaReplacementContext getParentContext() {
        try {
            if (namespace.indexOf(FormProcessor.NAMESPACE_SEPARATOR) == -1) return null;

            String fieldName = namespace.substring(namespace.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR));
            String parentNamespace = namespace.substring(0, namespace.lastIndexOf(fieldName));
            String formId = parentNamespace.substring(parentNamespace.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR) + 1);
            parentNamespace = parentNamespace.substring(0, parentNamespace.lastIndexOf(FormProcessor.NAMESPACE_SEPARATOR));
            FormulaReplacementContext parent = new FormulaReplacementContext();

            SubformFinderService subformFinderService = (SubformFinderService) CDIBeanLocator.getBeanByType(SubformFinderService.class);

            fieldName = fieldName.substring(1);
            if (fieldName.indexOf(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR) != -1)
                fieldName = fieldName.substring(0, fieldName.lastIndexOf(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR));

            parent.setNamespace(parentNamespace);
            parent.setField(subformFinderService.getFormById(Long.decode(formId), parentNamespace).getField(fieldName));

            parent.setBeforeFieldEvaluation(beforeFieldEvaluation);
            parent.setDate(this.getDate());
            parent.setFormula(null);
            parent.setParamValue(null);
            return parent;
        } catch (Exception e) {
            log.error("Error getting field parent: ", e);
        }
        return null;
    }

    public void populate(Interpreter interpreter) throws EvalError {
        if (item != null) {
            interpreter.set("item", item);
        }
        interpreter.set("ctx", this);
    }
}
