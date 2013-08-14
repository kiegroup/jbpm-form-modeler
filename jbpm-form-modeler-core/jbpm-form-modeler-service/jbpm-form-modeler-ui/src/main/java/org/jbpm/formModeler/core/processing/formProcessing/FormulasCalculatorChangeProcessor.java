package org.jbpm.formModeler.core.processing.formProcessing;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@ApplicationScoped
public class FormulasCalculatorChangeProcessor extends BasicFormChangeProcessor {
    private static transient Logger log = LoggerFactory.getLogger(FormulasCalculatorChangeProcessor.class);

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
        return response;
    }

    @Override
    public int getSupportedContextType() {
        return FormProcessingContext.TYPE_FORMULA;
    }
}