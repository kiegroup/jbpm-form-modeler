package org.jbpm.formModeler.fieldTypes.document;

import org.jbpm.document.Document;
import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;
import org.jbpm.formModeler.fieldTypes.document.handling.JBPMDocumentFieldTypeHandler;

import java.util.Locale;
import java.util.ResourceBundle;

public class JBPMDocumentFieldType extends PlugableFieldType {
    public static final String CODE = "Document";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public String getManagerClass() {
        return JBPMDocumentFieldTypeHandler.class.getName();
    }

    @Override
    public String getFieldClass() {
        return Document.class.getName();
    }

    @Override
    public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.fieldTypes.document.messages", locale);
        return bundle.getString("description");
    }
}
