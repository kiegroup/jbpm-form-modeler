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
package org.jbpm.formModeler.components.editor;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


@SessionScoped
public class FormEditorContextManagerImpl implements FormEditorContextManager {
    public static final String EDIT_FIELD_LITERAL = "editingFormFieldId";

    @Inject
    private FormRenderContextManager formRenderContextManager;


    protected Map<String, FormEditorContext> formEditorContextMap = new HashMap<String, FormEditorContext>();

    @Override
    public FormEditorContext newContext(Form form, String path) {
        FormRenderContext ctx = formRenderContextManager.newContext(form, new HashMap<String, Object>());
        FormEditorContext formEditorContext = new FormEditorContext(ctx, path);
        formEditorContextMap.put(ctx.getUID(), formEditorContext);
        return formEditorContext;
    }

    @Override
    public void removeEditingForm(String ctxUID) {
        formEditorContextMap.remove(ctxUID);
        formRenderContextManager.removeContext(ctxUID);
    }

    @Override
    public FormEditorContext getFormEditorContext(String UID) {
        return formEditorContextMap.get(UID);
    }

    @Override
    public String generateFieldEditionNamespace(String UID, Field field) {
        return UID + FormProcessor.NAMESPACE_SEPARATOR + EDIT_FIELD_LITERAL + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + field.getId();
    }

    @Override
    public FormEditorContext getRootEditorContext(String UID) {
        if (StringUtils.isEmpty(UID)) return null;
        int separatorIndex = UID.indexOf(FormProcessor.NAMESPACE_SEPARATOR);
        if (separatorIndex != -1) UID = UID.substring(0, separatorIndex);
        return formEditorContextMap.get(UID);
    }
}
