package org.jbpm.formModeler.core.config;

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.Field;

import java.util.Locale;
import java.util.Map;

public interface SelectValuesProvider {
    String getIdentifier();
    Map<String, String> getSelectOptions(Field field, String value, FormRenderContext renderContext, Locale locale);
}
