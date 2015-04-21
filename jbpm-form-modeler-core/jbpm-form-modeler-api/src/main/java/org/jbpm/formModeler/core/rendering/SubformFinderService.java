package org.jbpm.formModeler.core.rendering;


import org.jbpm.formModeler.api.model.Form;

public interface SubformFinderService {
    Form getForm(String ctxUID);

    Form getFormByPath(String formPath, String ctxUID);

    Form getFormById(long idForm, String ctxUID);
}
