package org.jbpm.formModeler.core.processing.formProcessing;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormStatusData;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@ApplicationScoped
public class FormulasCalculatorChangeProcessor extends BasicFormChangeProcessor {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormulasCalculatorChangeProcessor.class.getName());

    @Override
    public FormChangeResponse doProcess(FormChangeResponse response) {
        try {
            Form form = context.getForm();

            //WM antiguamente se cargaba el objeto que estaba siendo pintado por el formulario.
            //de momento esa nocion ya no la tenemos, pues el formulario puede pintar mas de un objeto y tampoco
            //esta al PotterManager, etc.
            //Object loadedObject = formProcessor.getLoadedObject(form.getDbid(), context.getNamespace());
            //TODO revisar esto pues pere esta viendo de guardar los objetos que se estan pintando en el form
            //status, etc. De momento esto lo dejo asi temporalmente.
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