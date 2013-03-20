package org.jbpm.formModeler.service.bb.mvc.components.handling;

import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

public abstract class BaseUIComponent extends UIComponentHandlerFactoryElement {
    public abstract String getBaseComponentJSP();

    @Override
    public synchronized CommandResponse handle(CommandRequest request, String action) throws Exception {
        CurrentComponentRenderer.lookup().setCurrentComponent(this);
        setEnabledForActionHandling(true);
        return super.handle(request, action);
    }
}
