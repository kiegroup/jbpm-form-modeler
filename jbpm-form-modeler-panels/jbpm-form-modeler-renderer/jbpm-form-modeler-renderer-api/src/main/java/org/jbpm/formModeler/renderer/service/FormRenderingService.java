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
package org.jbpm.formModeler.renderer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.renderer.FormRenderContext;
import org.jbpm.formModeler.renderer.FormRenderContextManager;
import org.jbpm.formModeler.renderer.FormRenderListener;

import java.lang.Long;
import java.lang.Object;
import java.lang.String;
import java.util.List;
import java.util.Map;

@Remote
public interface FormRenderingService extends FormRenderContextManager {
    List<FormTO> getAllForms();

    void loadForm(Long id, Long formId);
    FormRenderContext startRendering(Long formId, Map<String, Object> bindingData, FormRenderListener formRenderListener);
}
