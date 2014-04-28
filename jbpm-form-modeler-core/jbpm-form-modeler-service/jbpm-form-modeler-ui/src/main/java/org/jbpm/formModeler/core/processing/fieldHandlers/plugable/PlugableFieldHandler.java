package org.jbpm.formModeler.core.processing.fieldHandlers.plugable;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.FieldHandler;

public abstract class PlugableFieldHandler implements FieldHandler {

    public abstract String getInputHTML(Object value, Field field, String inputName, String namespace, Boolean readonly);
    public abstract String getShowHTML(Object value, Field field, String inputName, String namespace);

    @Override
    public String getPageToIncludeForRendering() {
        return "/formModeler/fieldHandlers/Plugable/input.jsp";
    }

    @Override
    public String getPageToIncludeForDisplaying() {
        return "/formModeler/fieldHandlers/Plugable/show.jsp";
    }
}
