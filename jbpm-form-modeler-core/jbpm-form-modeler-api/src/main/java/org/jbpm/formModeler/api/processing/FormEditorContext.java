package org.jbpm.formModeler.api.processing;

import org.jbpm.formModeler.api.model.Form;

import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.processing.FormRenderListener;
import org.uberfire.backend.vfs.Path;
import java.util.Map;

public class FormEditorContext extends FormRenderContext {
    private Form originalForm;
    private Object path;

    public FormEditorContext(String uid, Form form, Map<String, Object> bindingData, FormRenderListener formRenderListener, Object path) {
        super(uid, form, bindingData, formRenderListener);
        this.path = path;
    }

    public Object getPath() {
        return path;
    }

    public void setPath(Object path) {
        this.path = path;
    }

    public Form getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(Form originalForm) {
        this.originalForm = originalForm;
    }
}
