package org.jbpm.formModeler.service.bb.mvc.components.handling;

import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

import javax.inject.Inject;

public abstract class BaseUIComponent extends UIBeanHandler {

    private boolean firstTime;

    public abstract String getBaseComponentJSP();
    public abstract void doStart(CommandRequest request);

    @Inject
    private LocaleManager localeManager;

    @Override
    public synchronized CommandResponse handle(CommandRequest request, String action) throws Exception {
        CurrentComponentRenderer componentRenderer = CurrentComponentRenderer.lookup();

        componentRenderer.setCurrentComponent(this);
        setEnabledForActionHandling(true);

        CommandResponse response = super.handle(request, action);
        String ajaxParam = request.getRequestObject().getParameter("ajaxAction");
        boolean isAjax = ajaxParam != null && Boolean.valueOf(ajaxParam).booleanValue();

        if (firstTime || !isAjax) {
            response = null;
            firstTime = false;
        }

        return response;
    }

    public void actionStart(CommandRequest request) {
        firstTime = true;
        doStart(request);
        localeManager.setCurrentLang(request.getRequestObject().getParameter("locale"));
    }
}
