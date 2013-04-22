package org.jbpm.formModeler.service.bb.mvc.components.handling;

import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

public abstract class BaseUIComponent extends UIComponentHandlerFactoryElement {

    private boolean firstTime;

    public abstract String getBaseComponentJSP();
    public abstract void doStart(CommandRequest request);

    @Override
    public synchronized CommandResponse handle(CommandRequest request, String action) throws Exception {
        CurrentComponentRenderer componentRenderer = CurrentComponentRenderer.lookup();

        componentRenderer.setCurrentComponent(this);
        setEnabledForActionHandling(true);

        CommandResponse response = super.handle(request, action);

        if (!this.equals(componentRenderer.getCurrentComponent()) || firstTime) {
            response = null;
            firstTime = false;
        }

        return response;
    }

    public void actionStart(CommandRequest request) {
        firstTime = true;
        doStart(request);
    }
}
