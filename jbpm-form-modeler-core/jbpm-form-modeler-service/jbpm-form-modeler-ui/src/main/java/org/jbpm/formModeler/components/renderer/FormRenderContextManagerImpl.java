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
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SessionScoped
public class FormRenderContextManagerImpl implements FormRenderContextManager, Serializable {
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
    public FormRenderContext newContext(Form form, Map<String, Object> ctx) {
        return newContext(form, ctx, new HashMap<String, Object>());
    }

    @Override
    public FormRenderContext newContext(Form form, Map<String, Object> inputData, Map<String, Object> outputData) {
        String uid = CTX_PREFFIX + form.getId() + "_" + System.currentTimeMillis();

        return buildContext(uid, form, inputData, outputData);
    }

    @Override
    public FormRenderContext newContext(Form form, Map<String, Object> inputData, Map<String, Object> outputData, Map<String, Object> forms) {
        String uid = CTX_PREFFIX + form.getId() + "_" + System.currentTimeMillis();
        return buildContext(uid, form, inputData, outputData, forms);
    }

    private FormRenderContext buildContext(String uid, Form form, Map<String,Object> inputData, Map<String,Object> outputData, Map<String,Object> forms) {
        FormRenderContext ctx = new FormRenderContext(uid, form, inputData, outputData);
        ctx.setContextForms(forms);
        formRenderContextMap.put(uid, ctx);
        formProcessor.read(ctx.getUID());
        return ctx;
    }

    private FormRenderContext buildContext(String uid, Form form, Map<String, Object> inputData, Map<String, Object> outputData) {
        return buildContext(uid, form, inputData, outputData, new HashMap<String, Object>());
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
}
