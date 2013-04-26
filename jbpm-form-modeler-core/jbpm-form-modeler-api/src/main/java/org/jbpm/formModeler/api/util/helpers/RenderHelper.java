package org.jbpm.formModeler.api.util.helpers;


import org.jbpm.formModeler.api.model.Form;


public class RenderHelper {
    private Form form;
    private Long sessionId;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
