package org.jbpm.formModeler.editor.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class FormTO {
    private Long formId;
    private String formName;

    public FormTO() {
    }

    public FormTO(Long formId, String formName) {
        this.formId = formId;
        this.formName = formName;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
}
