package org.jbpm.formModeler.core.processing.formProcessing;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.formProcessing.replacers.FormulaReplacementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;


public abstract class BasicFormChangeProcessor extends FormChangeProcessor {
    private static transient Logger log = LoggerFactory.getLogger(BasicFormChangeProcessor.class);

    protected HashSet evaluatedFields = new HashSet();
    protected FunctionsProvider functionsProvider = FunctionsProvider.lookup();

    protected String replaceFieldValues(Form form, FormStatusData statusData, String namespace, Field field, String rangeFormula, Object loadedObject, FormChangeResponse response) {
        while (rangeFormula.indexOf("{") != -1) {
            int beginIndex = rangeFormula.indexOf("{");
            int endIndex = rangeFormula.indexOf("}", beginIndex);
            if (endIndex == -1) break;
            String entityValue = rangeFormula.substring(beginIndex + 1, endIndex);
            Object value = evaluateEntityValue(form, namespace, entityValue, loadedObject, statusData, response);
            value = value == null ? "" : value;
            rangeFormula = rangeFormula.substring(0, beginIndex) + value + rangeFormula.substring(endIndex + 1);
        }

        return rangeFormula;
    }

    protected Object evaluateEntityValue(Form form, String namespace, String entityValue, Object loadedObject, FormStatusData statusData, FormChangeResponse response) {
        String fieldName = entityValue;
        if (fieldName.indexOf("/") != -1)
            fieldName = fieldName.substring(0, fieldName.indexOf("/"));

        Field relatedField = form.getField(fieldName);
        if (relatedField != null && relatedField.getFieldFormula() != null && !"".equals(relatedField.getFieldFormula()) && !evaluatedFields.contains(fieldName)) {
            //Evaluate dependent formula first
            evaluateFormulaForField(form, namespace, relatedField, loadedObject, statusData, response, new Date());
        }

        Object value = null;
        // Get value from the loaded object.
        if (loadedObject != null) {
            JXPathContext ctx = JXPathContext.newContext(loadedObject);
            try {
                value = ctx.getValue(entityValue);
            } catch (Exception e) {
                log.debug("Error in JXPathContext ", e);
            }
        }

        // Get value from the status data, overwriting value from the loaded object if it proceeds.
        if (statusData.getCurrentValues() != null) {
            JXPathContext ctx = JXPathContext.newContext(statusData.getCurrentValues());
            try {
                if (statusData.getCurrentValues().containsKey(entityValue)) {
                    value = statusData.getCurrentValues().get(entityValue);
                } else {
                    value = ctx.getValue(entityValue);
                }
            } catch (Exception e) {
                log.debug("Error in JXPathContext ", e);
            }
        }

        if (value instanceof String)
            value = "\"" + StringEscapeUtils.escapeJava((String) value) + "\"";
        if (value instanceof Date)
            value = "new java.util.Date(" + ((Date) value).getTime() + "l)";
        if (value == null) {

            /* commentado por WM al migrar. TODO ver que hace esto
            try {
                PropertyDefinition propertyType = getPotterManager().getPropertyTypeForJXPath(entityValue, form.getSubject());
                if (propertyType != null && propertyType.getPropertyClass() == Boolean.class)
                    value = "false";
            } catch (Exception e) {
                log.error("Error: ", e);
            }
            */
        }
        return value;
    }

    protected void evaluateFormulaForField(Form form, String namespace, Field field, Object loadedObject, FormStatusData statusData, FormChangeResponse response, Date date) {
        evaluatedFields.add(field.getFieldName());
        if (field.getFieldFormula() != null && field.getFieldFormula().startsWith("=")) {
            Object value = evaluateFormula(form, namespace, field.getFieldFormula().substring(1), loadedObject, statusData, response, field, date);
            FormStatusData status1 = formProcessor.read(form, namespace);
            Object currentFieldValue = status1.getCurrentValue(field.getFieldName());
            if ((currentFieldValue != null && value == null) || (value != null && !value.equals(currentFieldValue))) {
                FieldHandler fieldHandler = fieldHandlersManager.getHandler(field.getFieldType());
                Map fieldValuesMap = fieldHandler.getParamValue(namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName(), value, field.getFieldPattern());
                formProcessor.setValues(form, namespace, fieldValuesMap, fieldValuesMap, true);
                response.addInstruction(new SetFieldValueInstruction(fieldValuesMap));
            }
        }
    }

    protected Object evaluateFormula(final Form form, final String namespace, final String formula, final Object loadedObject, final FormStatusData statusData, final FormChangeResponse response, final Field field, final Date date) {

        if (log.isDebugEnabled()) log.debug("Evaluating formula " + formula);
        FormulaReplacementContext ctx = new FormulaReplacementContext();
        ctx.setBeforeFieldEvaluation(false);
        ctx.setDate(date);
        ctx.setField(field);
        ctx.setFormula(formula);
        ctx.setNamespace(namespace);
        String paramValue = "";
        FieldHandler manager = fieldHandlersManager.getHandler(field.getFieldType());
        String fieldId = namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
        try {
            Map parametersMap = statusData.getCurrentInputValues();
            Object value = manager.getValue(field, fieldId, parametersMap == null ? Collections.EMPTY_MAP : parametersMap, Collections.EMPTY_MAP, field.getFieldType().getFieldClass(), statusData.getCurrentValue(field.getFieldName()));
            Map params = manager.getParamValue(fieldId, value, field.getFieldPattern());
            if (params != null) {
                String[] paramValues = (String[]) params.get(fieldId);
                if (paramValues != null && paramValues.length > 0)
                    paramValue = paramValues[0];
            }
        } catch (Exception e) {
            log.debug("Error: ", e);
        }
        ctx.setParamValue(paramValue == null ? "" : paramValue);
        String modifiedFormula = replacementManager.replace(ctx);

        // Compute the entity properties
        modifiedFormula = replaceFieldValues(form, statusData, namespace, field, modifiedFormula, loadedObject, response);

        //Evaluate the resulting formula
        Interpreter interpreter = getInterpreter(form, namespace);
        try {
            if (log.isDebugEnabled()) log.debug("Interpreting formula: '" + modifiedFormula + "'");
            functionsProvider.populate(interpreter);
            ctx.populate(interpreter);
            Object result = interpreter.eval(modifiedFormula);
            return result;
        } catch (EvalError evalError) {
            log.debug("Error interpreting formula: " + evalError + " will cause formula evaluation to fail quietly.");
            return null;
        }

    }

    protected Interpreter getInterpreter(Form form, String namespace) {
        Interpreter i = (Interpreter) formProcessor.getAttribute(form, namespace, FormProcessor.ATTR_INTERPRETER);
        if (i == null)
            formProcessor.setAttribute(form, namespace, FormProcessor.ATTR_INTERPRETER, i = new Interpreter());
        return i;
    }

}
