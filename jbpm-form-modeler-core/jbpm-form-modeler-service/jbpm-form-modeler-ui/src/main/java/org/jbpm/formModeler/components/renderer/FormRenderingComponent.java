/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.api.events.FormSubmitFailEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("frc")
public class FormRenderingComponent extends BaseUIComponent {
    private Logger log = LoggerFactory.getLogger(FormRenderingComponent.class);

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
    private Event<ContextRenderedEvent> contextRenderedEventEvent;

    private FormRenderContext ctx;

    @Override
    public void doStart(CommandRequest commandRequest) {

        String ctxUID = commandRequest.getRequestObject().getParameter("ctxUID");

        if (StringUtils.isEmpty(ctxUID)) return;

        ctx = formRenderContextManager.getFormRenderContext(ctxUID);
    }


    @Override
    public void beforeRenderBean() {
        if (ctx != null) ctx.setInUse(true);
    }

    @Override
    public void afterRenderBean() {
        if (ctx != null) {
            ctx.setInUse(false);
            contextRenderedEventEvent.fire(new ContextRenderedEvent(ctx.getUID()));
        }
    }

    public void removeContextEvent(@Observes ContextRemovedEvent event) {
        if (ctx != null && ctx.getUID().equals(event.getCtxUID())) ctx = null;
    }

    public void actionSubmitForm(CommandRequest request) {
        String ctxUID = request.getRequestObject().getParameter("ctxUID");

        if (ctx == null || ctx.getUID().equals(ctxUID)) ctx = formRenderContextManager.getFormRenderContext(ctxUID);
        if (ctx == null) return;
        try {
            Form form = ctx.getForm();

            formProcessor.setValues(form, ctxUID, request.getRequestObject().getParameterMap(), request.getFilesByParamName());
            FormStatusData fsd = formProcessor.read(ctxUID);

            ctx.setErrors(fsd.getWrongFields().size());

            String persist = request.getRequestObject().getParameter("persistForm");
            if (fsd.isValid()) {
                ctx.setSubmit(true);
                if (Boolean.parseBoolean(persist)) formRenderContextManager.persistContext(ctx);
            }

            formRenderContextManager.fireContextSubmit(new FormSubmittedEvent(new FormRenderContextTO(ctx)));
        } catch (Exception e) {
            log.error("Error submitting form: ", e);
            formRenderContextManager.fireContextSubmitError(new FormSubmitFailEvent(new FormRenderContextTO(ctx), e.getMessage()));
        }
    }

    public CommandResponse actionDoResize(CommandRequest request) {
        String ctxUID = request.getRequestObject().getParameter("ctxUID");

        if (ctx == null || ctx.getUID().equals(ctxUID)) ctx = formRenderContextManager.getFormRenderContext(ctxUID);
        if (ctx != null) {
            String width = request.getRequestObject().getParameter("width");
            String height = request.getRequestObject().getParameter("height");

            if (!StringUtils.isEmpty(width) && !StringUtils.isEmpty(height)) {
                ResizeFormcontainerEvent event = new ResizeFormcontainerEvent();
                event.setContext(new FormRenderContextTO(ctx));
                event.setWidth(Integer.decode(width));
                event.setHeight(Integer.decode(height));
                formRenderContextManager.fireContextFormResize(event);
            }
        }

        return new DoNothingResponse();
    }

    public String getCtxUID() {
        return ctx.getUID();
    }

    public Form getForm() {
        return ctx == null ? null : ctx.getForm();
    }

    public boolean isReadonly() {
        return ctx.isReadonly();
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
        return ctx.isSubmit();
    }
}
