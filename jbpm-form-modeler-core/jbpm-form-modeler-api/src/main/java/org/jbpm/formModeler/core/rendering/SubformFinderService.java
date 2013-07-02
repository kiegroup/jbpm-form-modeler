package org.jbpm.formModeler.core.rendering;


import org.jbpm.formModeler.api.model.Form;

public interface SubformFinderService {
    Form getFormFromPath(String formPath, String ctxUID);
}
