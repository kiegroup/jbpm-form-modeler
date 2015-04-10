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

import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.events.FormSubmitFailEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.events.ResizeFormcontainerEvent;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SessionScoped
public class FormRenderContextManagerImpl implements FormRenderContextManager, Serializable {
    @Inject
    private FormProcessor formProcessor;

    @Inject
    private Event<FormSubmitFailEvent> formSubmitFailEvent;

    @Inject
    private Event<FormSubmittedEvent> formSubmittedEvent;

    @Inject
    private Event<ResizeFormcontainerEvent> resizeFormcontainerEvent;

    @Inject
    private Event<ContextRemovedEvent> contextRemovedEventEvent;

    protected Map<String, FormRenderContext> formRenderContextMap = new HashMap<String, FormRenderContext>();

    protected List<String> contextToRemove = new ArrayList<String>();

    @Override
    public void persistContext(FormRenderContext ctx) throws Exception {
        if (ctx == null) throw new IllegalArgumentException("Unable to persist null context");
        formProcessor.persist(ctx);
    }

    @Override
    public void persistContext(String ctxUID) throws Exception {
        persistContext(getFormRenderContext(ctxUID));
    }

    @Override
    public void removeContext(String ctxUID) {
        removeContext(getFormRenderContext(ctxUID));
    }

    @Override
    public void removeContext(FormRenderContext context) {
        if (context != null) {
            if (!context.isInUse())
                doRemovecontext(context.getUID());
            else
                contextToRemove.add(context.getUID());

        }
    }

    protected void doRemovecontext(String ctxUID) {
        FormRenderContext context = formRenderContextMap.get(ctxUID);
        if (context == null) return;
        context.clear();
        contextToRemove.remove(ctxUID);
        formProcessor.clear(context);
        formRenderContextMap.remove(context.getUID());
        contextRemovedEventEvent.fire(new ContextRemovedEvent(ctxUID));
    }

    public void removeContextEvent(@Observes ContextRenderedEvent event) {
        if (contextToRemove.contains(event.getCtxUID())) doRemovecontext(event.getCtxUID());
    }

    @Override
    public FormRenderContext newContext(Form form, String deploymentId, Map<String, Object> ctx) {
        return newContext(form, deploymentId, ctx, new HashMap<String, Object>());
    }

    @Override
    public FormRenderContext newContext(Form form, String deploymentId, Map<String, Object> inputData, Map<String, Object> outputData) {
        String uid = CTX_PREFFIX + form.getId() + "_" + System.currentTimeMillis();

        return buildContext(uid, form, deploymentId, inputData, outputData);
    }

    private FormRenderContext buildContext(String uid, Form form, String deploymentId, Map<String,Object> inputData, Map<String,Object> outputData, Map<String,Object> forms) {
        FormRenderContext ctx = new FormRenderContext(uid, form, inputData, outputData);
        ctx.setDeploymentId( deploymentId );
        ctx.setContextForms( forms );
        formRenderContextMap.put( uid, ctx );
        formProcessor.read( ctx.getUID() );
        return ctx;
    }

    private FormRenderContext buildContext(String uid, Form form, String deploymentId, Map<String, Object> inputData, Map<String, Object> outputData) {
        return buildContext(uid, form, deploymentId, inputData, outputData, new HashMap<String, Object>());
    }

    @Override
    public FormRenderContext getFormRenderContext(String UID) {
        return formRenderContextMap.get(UID);
    }

    @Override
    public FormRenderContext getRootContext(String UID) {
        int separatorIndex = UID.indexOf(FormProcessor.NAMESPACE_SEPARATOR);
        if (separatorIndex != -1) UID = UID.substring(0, separatorIndex);
        return formRenderContextMap.get(UID);
    }

    @Override
    public void fireContextSubmitError(FormSubmitFailEvent event) {
        if (event != null) formSubmitFailEvent.fire(event);
    }

    @Override
    public void fireContextSubmit(FormSubmittedEvent event) {
        if (event != null) formSubmittedEvent.fire(event);
    }

    @Override
    public void fireContextFormResize(ResizeFormcontainerEvent event) {
        if (event != null) resizeFormcontainerEvent.fire(event);
    }
}
