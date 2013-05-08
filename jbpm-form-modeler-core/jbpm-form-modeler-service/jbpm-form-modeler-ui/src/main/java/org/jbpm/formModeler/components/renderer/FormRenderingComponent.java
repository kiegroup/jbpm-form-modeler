/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.components.renderer;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.events.FormRenderEvent;
import org.jbpm.formModeler.api.events.FormSubmitFailEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.processing.FormRenderContextManager;
import org.jbpm.formModeler.api.processing.FormStatusData;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;
import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.processing.FormRenderContextManager;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@ApplicationScoped
@Named("frc")
public class FormRenderingComponent extends BaseUIComponent {
    @Inject
    @Config("/formModeler/components/renderer/component.jsp")
    private String baseComponentJSP;

    @Inject
    @Config("/formModeler/components/renderer/show.jsp")
    private String componentIncludeJSP;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Inject
    private FormProcessor formProcessor;

    @Inject
    Event<FormRenderEvent> formRenderEvent;

    private String ctxUID;
    private Form form;
    private boolean submited = false;

    public void doStart(CommandRequest commandRequest) {

        this.ctxUID = commandRequest.getRequestObject().getParameter("ctxUID");

        if (StringUtils.isEmpty(ctxUID)) return;

        FormRenderContext ctx = formRenderContextManager.getFormRenderContext(ctxUID);

        submited = false;

        this.form = ctx.getForm();

    }

    public void actionSubmitForm(CommandRequest request) {
        String ctxUID = request.getRequestObject().getParameter("ctxUID");
        FormRenderContext ctx = formRenderContextManager.getFormRenderContext(ctxUID);
        if (ctx == null) return;
        try {
            ctx.setSubmit(true);
            Form form = ctx.getForm();

            formProcessor.setValues(form, ctxUID, request.getRequestObject().getParameterMap(), request.getFilesByParamName());
            FormStatusData fsd = formProcessor.read(ctxUID);

            ctx.setErrors(fsd.getWrongFields().size());
            submited = true;

            ctx.setSubmit(false);
            formRenderEvent.fire(new FormSubmittedEvent(ctx.getFormRenderingContextTO()));
        } catch (Exception e) {
            formRenderEvent.fire(new FormSubmitFailEvent(ctx.getFormRenderingContextTO(), e.getMessage()));
        }


    }

    public void actionIsProcessed(CommandRequest request) throws IOException {
        String ctxUID = request.getRequestObject().getParameter("ctxUID");
        FormRenderContext ctx = formRenderContextManager.getFormRenderContext(ctxUID);

        request.getResponseObject().getWriter().print(ctx.isSubmit());
    }

    public String getCtxUID() {
        return ctxUID;
    }

    public void setCtxUID(String ctxUID) {
        this.ctxUID = ctxUID;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public void setBaseComponentJSP(String baseComponentJSP) {
        this.baseComponentJSP = baseComponentJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    @Override
    public String getBaseComponentJSP() {
        return baseComponentJSP;
    }

    @Override
    public String getBeanJSP() {
        return componentIncludeJSP;
    }

    public FormProcessor getFormProcessor() {
        return formProcessor;
    }

    public void setFormProcessor(FormProcessor formProcessor) {
        this.formProcessor = formProcessor;
    }

    public static FormRenderingComponent lookup() {
        return (FormRenderingComponent) CDIBeanLocator.getBeanByType(FormRenderingComponent.class);
    }

    public boolean isSubmited() {
        return submited;
    }

    public void setSubmited(boolean submited) {
        this.submited = submited;
    }
}
