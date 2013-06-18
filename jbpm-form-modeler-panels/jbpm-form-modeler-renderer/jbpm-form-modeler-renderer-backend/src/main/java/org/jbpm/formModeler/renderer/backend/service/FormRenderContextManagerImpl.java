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
package org.jbpm.formModeler.renderer.backend.service;


import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.events.FormSubmitFailEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service
@ApplicationScoped
public class FormRenderContextManagerImpl implements FormRenderContextManager {
    @Inject
    private FormProcessor formProcessor;

    @Inject
    private Event<FormSubmitFailEvent> formSubmitFailEvent;

    @Inject
    private Event<FormSubmittedEvent> formSubmittedEvent;

    protected Map<String, FormRenderContext> formRenderContextMap = new HashMap<String, FormRenderContext>();

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
            formRenderContextMap.remove(context.getUID());
            formProcessor.clear(context);
        }
    }

    @Override
    public FormRenderContext newContext(Form form, Map<String, Object> bindingData) {
        String uid = CTX_PREFFIX + form.getId() + "_" + System.currentTimeMillis();

        return buildContext(uid, form, bindingData);
    }

    @Override
    public FormRenderContext newContext(String ctxPreffix, Form form, Map<String, Object> bindingData) {
        String uid = ctxPreffix + "_" + form.getId();
        FormRenderContext ctx = formRenderContextMap.get(uid);

        if (ctx == null) ctx = buildContext(uid, form, bindingData);

        return ctx;
    }

    private FormRenderContext buildContext(String uid, Form form, Map<String, Object> bindingData) {
        FormRenderContext ctx = new FormRenderContext(uid, form, bindingData);
        formRenderContextMap.put(uid, ctx);
        formProcessor.read(ctx.getUID());
        return ctx;
    }

    @Override
    public FormRenderContext getFormRenderContext(String UID) {
        return formRenderContextMap.get(UID);
    }

    @Override
    public Map getContextData(String UID) {
        return getFormRenderContext(UID).getBindingData();
    }

    @Override
    public void fireContextSubmitError(FormSubmitFailEvent event) {
        if (event != null) formSubmitFailEvent.fire(event);
    }

    @Override
    public void fireContextSubmit(FormSubmittedEvent event) {
        if (event != null) formSubmittedEvent.fire(event);
    }
}
