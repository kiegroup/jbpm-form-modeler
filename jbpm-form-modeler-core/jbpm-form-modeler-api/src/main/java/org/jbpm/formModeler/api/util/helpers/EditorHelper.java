package org.jbpm.formModeler.api.util.helpers;


import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

public class EditorHelper implements Serializable {

    private Long originalForm;
    private Form formToEdit;

    @PostConstruct
    protected void init() {
        System.out.println("Hey I'm beeing constructed: " + this);
    }

    public Form getFormToEdit() {
        return formToEdit;
    }

    public void setFormToEdit(Form formToEdit) {
        this.formToEdit = formToEdit;
    }

    public Long getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(Long originalForm) {
        this.originalForm = originalForm;
    }

    public static EditorHelper lookup() {
        return (EditorHelper) CDIHelper.getBeanByType(EditorHelper.class);
    }
}
