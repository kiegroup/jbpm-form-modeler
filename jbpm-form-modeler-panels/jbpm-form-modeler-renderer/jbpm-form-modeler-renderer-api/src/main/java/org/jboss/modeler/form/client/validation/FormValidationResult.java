package org.jboss.modeler.form.client.validation;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Portable
public class FormValidationResult implements Serializable {
    List<String> messages = new ArrayList<String>();
    List<String> errors = new ArrayList<String>();

    public FormValidationResult(){}

    public boolean isValid() {
        return errors.isEmpty();
    }

    public void addMessage(String message) {
        if (!isEmpty(message)) messages.add(message);
    }

    public void addError(String error) {
        if (!isEmpty(error)) errors.add(error);
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<String> getErrors() {
        return errors;
    }
}
