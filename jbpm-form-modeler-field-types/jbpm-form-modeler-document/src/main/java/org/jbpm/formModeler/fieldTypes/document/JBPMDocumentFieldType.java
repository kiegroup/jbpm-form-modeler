package org.jbpm.formModeler.fieldTypes.document;

import org.jbpm.formModeler.core.fieldTypes.ComplexFieldType;
import org.jbpm.formModeler.fieldTypes.document.handling.JBPMDocumentFieldTypeHandler;

import java.util.Locale;
import java.util.ResourceBundle;

public class JBPMDocumentFieldType extends ComplexFieldType {
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
