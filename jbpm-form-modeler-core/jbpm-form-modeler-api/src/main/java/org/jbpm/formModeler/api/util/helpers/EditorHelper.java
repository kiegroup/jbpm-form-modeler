package org.jbpm.formModeler.api.util.helpers;


import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;

public class EditorHelper implements Serializable {

    private Long originalForm;
    private HashMap loadedForms;

    @PostConstruct
    protected void init() {
        System.out.println("Hey I'm beeing constructed: " + this);
    }

    public Long getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(Long originalForm) {
        this.originalForm = originalForm;
    }


    public void setFormToEdit(String path, Form formToEdit) {
        if(loadedForms==null) loadedForms=new HashMap();
        this.loadedForms.put(path,formToEdit);
    }

    public Form getFormToEdit(String path) {
        if(loadedForms!=null)
            return (Form)loadedForms.get(path);
        else
            return null;
    }

    public Form removeEditingForm(String path) {
        if(loadedForms != null)
            return (Form) loadedForms.remove(path);
        else
            return null;
    }

    public static EditorHelper lookup() {
        return (EditorHelper) CDIHelper.getBeanByType(EditorHelper.class);
    }




}
