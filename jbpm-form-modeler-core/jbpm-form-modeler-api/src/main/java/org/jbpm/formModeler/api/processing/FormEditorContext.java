package org.jbpm.formModeler.api.processing;

import org.jbpm.formModeler.api.model.Form;


public class FormEditorContext {
    private FormRenderContext renderContext;
    private Form originalForm;
    private Object path;

    public FormEditorContext(FormRenderContext ctx, Object path) {
        this.renderContext = ctx;
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

    public FormRenderContext getRenderContext() {
        return renderContext;
    }

    public Form getForm() {
        return renderContext.getForm();
    }

    public FormEditorContextTO getFormEditorContextTO() {
        return new FormEditorContextTO(renderContext.getUID(), renderContext.getForm().getId(), path);
    }
}
