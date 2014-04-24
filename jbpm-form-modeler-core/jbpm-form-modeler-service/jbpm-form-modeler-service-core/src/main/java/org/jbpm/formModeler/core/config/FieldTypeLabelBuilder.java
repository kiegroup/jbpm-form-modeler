package org.jbpm.formModeler.core.config;

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.fieldTypes.ComplexFieldType;
import org.jbpm.formModeler.service.LocaleChangedEvent;
import org.jbpm.formModeler.service.LocaleManager;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ResourceBundle;

@SessionScoped
public class FieldTypeLabelBuilder implements Serializable {

    @Inject
    private LocaleManager localeManager;
    private ResourceBundle bundle;

    public String getFieldTypeLabel(FieldType fieldType) {
        if (fieldType instanceof ComplexFieldType) {
            return ((ComplexFieldType)fieldType).getDescription(localeManager.getCurrentLocale());
        }

        return bundle.getString("fieldType." + fieldType.getCode());
    }

    protected void onChangeLocale(@Observes LocaleChangedEvent localeChangedEvent) {
        bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.config.fieldTypes.messages", localeManager.getCurrentLocale());
    }
}
