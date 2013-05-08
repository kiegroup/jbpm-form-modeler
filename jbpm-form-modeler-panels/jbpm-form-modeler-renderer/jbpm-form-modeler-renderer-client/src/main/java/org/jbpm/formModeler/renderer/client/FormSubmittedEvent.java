package org.jbpm.formModeler.renderer.client;


import com.google.gwt.event.dom.client.DomEvent;

public class FormSubmittedEvent extends DomEvent<FormSubmittedHandler> {
    private static final DomEvent.Type<FormSubmittedHandler> TYPE = new Type<FormSubmittedHandler>("Submitted Rendered Form", new FormSubmittedEvent());
    private String ctxUID;
    private int errors;

    public static Type<FormSubmittedHandler> getType() {
        return TYPE;
    }

    protected FormSubmittedEvent() {
    }

    @Override
    public final Type<FormSubmittedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FormSubmittedHandler handler) {
        handler.afterSubmit(this);
    }

    public void setCtxUID(String ctxUID) {
        this.ctxUID = ctxUID;
    }

    public String getCtxUID() {
        return ctxUID;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public int getErrors() {
        return errors;
    }
}
