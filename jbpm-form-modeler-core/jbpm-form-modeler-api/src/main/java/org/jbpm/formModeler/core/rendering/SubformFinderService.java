package org.jbpm.formModeler.core.rendering;


import org.jbpm.formModeler.api.model.Form;

public interface SubformFinderService {
    public static final String MAIN_RESOURCES_PATH = "src/main/resources";

    Form getFormFromPath(String formPath, String ctxUID);

    Form getFormById(long idForm, String ctxUID);
}
