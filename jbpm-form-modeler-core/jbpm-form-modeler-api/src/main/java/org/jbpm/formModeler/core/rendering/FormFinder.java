package org.jbpm.formModeler.core.rendering;

import org.jbpm.formModeler.api.model.Form;

/**
 * Created by pefernan on 4/20/15.
 */
public interface FormFinder {
    Form getForm(String ctxUID);

    Form getFormByPath( String ctxUID, String formPath );

    Form getFormById(String ctxUID, long formId);

    int getPriority();
}
