package org.jbpm.formModeler.core.processing.formProcessing;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 10/2/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
@ApplicationScoped
public class DefaultFormulaProcessor extends BasicFormChangeProcessor {
    private static transient Logger log = LoggerFactory.getLogger(DefaultFormulaProcessor.class);


    @Override
    public FormChangeResponse doProcess(FormChangeResponse response) {

        try {
            Form form = context.getForm();
            if (form == null) {
                //TODO evaluate if this control should be removed
                log.warn("Form object is not present in current FormProcessingContext, formula evaluation will be canceled. context: " + context);
                return response;
            }

            //Current forms implementation supports more than one object, so the loaded objects is not loaded
            //at this moment any more.
            //TODO change the evaluateFormulaForField signature to remove this parameter
            Object loadedObject = null;

            FormStatusData statusData = formProcessor.read(form, context.getNamespace());

            Collection fieldNames = getEvaluableFields();
            evaluatedFields.clear();
            for (Iterator iterator = fieldNames.iterator(); iterator.hasNext();) {
                String fieldName = (String) iterator.next();
                Field field = form.getField(fieldName);
                evaluateFormulaForField(form, context.getNamespace(), field, loadedObject, statusData, response, new Date());
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }

        return null;
    }

    protected void evaluateFormulaForField(Form form, String namespace, Field field, Object loadedObject, FormStatusData statusData, FormChangeResponse response, Date date) {
        evaluatedFields.add(field.getFieldName());
        String defaultFormula = field.getDefaultValueFormula();
        if (defaultFormula != null && defaultFormula.startsWith("=")) {
            Object value = evaluateFormula(form, namespace, defaultFormula.substring(1), loadedObject, statusData, response, field, date);
            FormStatusData status1 = formProcessor.read(form, namespace);
            Object currentFieldValue = status1.getCurrentValue(field.getFieldName());
            if ((currentFieldValue != null && value == null) || (value != null && !value.equals(currentFieldValue))) {
                FieldHandler fieldHandler = fieldHandlersManager.getHandler(field.getFieldType());
                Map fieldValuesMap = fieldHandler.getParamValue(namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName(), value, field.getFieldPattern());
                formProcessor.setValues(form, namespace, fieldValuesMap, fieldValuesMap, true);
            }
        }
    }

    @Override
    public int getSupportedContextType() {
        return 4;
    }
}
