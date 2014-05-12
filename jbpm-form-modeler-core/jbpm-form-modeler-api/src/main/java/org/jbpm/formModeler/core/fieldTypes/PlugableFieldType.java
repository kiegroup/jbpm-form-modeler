package org.jbpm.formModeler.core.fieldTypes;

import org.jbpm.formModeler.api.model.FieldType;

import java.util.Locale;

public abstract class PlugableFieldType extends FieldType {

    public abstract String getCode();
    public abstract String getManagerClass();
    public abstract String getFieldClass();
    public abstract String getDescription(Locale locale);
}
