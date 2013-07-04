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
package org.jbpm.formModeler.api.client;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.events.FormSubmitFailEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;

import java.util.Map;

@Remote
public interface FormRenderContextManager {
    public static final String CTX_PREFFIX = "formRenderCtx_";

    FormRenderContext newContext(Form form, Map<String, Object> ctx);
    FormRenderContext newContext(Form form, Map<String, Object> inputData, Map<String, Object> outputData);
    FormRenderContext newContext(Form form, Map<String, Object> inputData, Map<String, Object> outputData, Map<String, Object> forms);
    FormRenderContext getFormRenderContext(String UID);
    FormRenderContext getRootContext(String UID);

    void removeContext(String ctxUID);
    void removeContext(FormRenderContext context);

    void fireContextSubmitError(FormSubmitFailEvent event);
    void fireContextSubmit(FormSubmittedEvent event);

    void persistContext(FormRenderContext ctx) throws Exception;
    void persistContext(String ctxUID) throws Exception;

}
