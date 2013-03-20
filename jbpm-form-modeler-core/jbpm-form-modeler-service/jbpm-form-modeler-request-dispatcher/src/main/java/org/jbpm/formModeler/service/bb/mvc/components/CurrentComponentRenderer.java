package org.jbpm.formModeler.service.bb.mvc.components;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.components.handling.UIComponentHandlerFactoryElement;

import java.lang.Override;
import java.lang.String;

public class CurrentComponentRenderer extends UIComponentHandlerFactoryElement {
    private BaseUIComponent currentComponent;

    @Override
    public String getComponentIncludeJSP() {
        if (currentComponent != null) return currentComponent.getBaseComponentJSP();
        return "";
    }

    public BaseUIComponent getCurrentComponent() {
        return currentComponent;
    }

    public void setCurrentComponent(BaseUIComponent currentComponent) {
        this.currentComponent = currentComponent;
    }

    public static final CurrentComponentRenderer lookup() {
        return (CurrentComponentRenderer) Factory.lookup("org.jbpm.formModeler.service.mvc.components.CurrentComponentRenderer");
    }
}
