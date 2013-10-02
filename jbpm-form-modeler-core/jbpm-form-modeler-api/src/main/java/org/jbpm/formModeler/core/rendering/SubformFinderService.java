package org.jbpm.formModeler.core.rendering;


import org.jbpm.formModeler.api.model.Form;

public interface SubformFinderService {
    public static final String MAIN_RESOURCES_PATH = "src/main/resources";

    Form getForm(String ctxUID);

    Form getFormByPath(String path);

    Form getSubFormFromPath(String formPath, String ctxUID);

    Form getFormById(long idForm, String ctxUID);
}
