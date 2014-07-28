package org.jbpm.formModeler.api.model;

import org.jbpm.formModeler.api.client.FormRenderContext;

import java.util.Locale;
import java.util.Map;

public interface SelectValuesProvider {
    String getIdentifier();
    Map<String, String> getSelectOptions(Field field, String value, FormRenderContext renderContext, Locale locale);
}
