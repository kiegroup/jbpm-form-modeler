package org.jbpm.formModeler.core.processing.fieldHandlers.multiple;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Enumeration;

@ApplicationScoped
@Named("org.jbpm.formModeler.core.processing.fieldHandlers.multiple.MultipleInputHandler")
public class MultipleInputHandler extends BeanHandler {

    @Inject
    private SubformFinderService subformFinderService;

    @Inject
    private FormProcessor formProcessor;

    public void actionAddItem(CommandRequest request) throws Exception {
        Enumeration parameterNames = request.getRequestObject().getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if (parameterName.endsWith(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "addItem")) {
                String parameterValue = request.getParameter(parameterName);
                if ("true".equals(parameterValue)) {
                    String paramRoot = parameterName.substring(0, parameterName.lastIndexOf(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "addItem"));

                    String namespace = request.getParameter(paramRoot + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "namespace");
                    String formId = request.getParameter(paramRoot + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "formId");

                    Form parentForm = subformFinderService.getFormById(Long.decode(formId), namespace);
                    formProcessor.setValues(parentForm, namespace, request.getRequestObject().getParameterMap(), request.getFilesByParamName());
                    return;
                }
            }
        }
    }

    public void actionDeleteItem(CommandRequest request) throws Exception {
        Enumeration parameterNames = request.getRequestObject().getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            if (parameterName.endsWith(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "deleteItem")) {
                String parameterValue = request.getParameter(parameterName);
                if (!"-1".equals(parameterValue)) {
                    String paramRoot = parameterName.substring(0, parameterName.lastIndexOf(FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "deleteItem"));

                    String namespace = request.getParameter(paramRoot + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "namespace");
                    String formId = request.getParameter(paramRoot + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "formId");

                    Form parentForm = subformFinderService.getFormById(Long.decode(formId), namespace);
                    formProcessor.setValues(parentForm, namespace, request.getRequestObject().getParameterMap(), request.getFilesByParamName());
                    return;
                }
            }
        }
    }

    @Override
    public boolean isEnabledForActionHandling() {
        return true;
    }
}
