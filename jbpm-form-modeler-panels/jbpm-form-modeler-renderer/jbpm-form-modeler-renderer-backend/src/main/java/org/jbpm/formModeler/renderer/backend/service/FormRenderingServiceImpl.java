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
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.renderer.service.FormRenderingService;
import org.apache.commons.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@Service
@ApplicationScoped
public class FormRenderingServiceImpl implements FormRenderingService {
    @Inject
    private Log log;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderContextManagerImpl formRenderContextManager;


    @Override
    public FormRenderContextTO startRendering(Long formId, Map<String, Object> bindingData) {
        Form form = formManager.getFormById(formId);

        return startRendering(form, bindingData);
    }

    @Override
    public FormRenderContextTO startRendering(Form form, Map<String, Object> bindingData) {
        if (form != null) {

            FormRenderContext ctx = formRenderContextManager.newContext(form, bindingData);

            return ctx.getFormRenderingContextTO();
        }

        return null;
    }
}
