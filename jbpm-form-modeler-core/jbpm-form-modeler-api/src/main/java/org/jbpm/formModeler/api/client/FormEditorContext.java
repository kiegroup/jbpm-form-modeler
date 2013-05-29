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

import org.jbpm.formModeler.api.model.Form;


public class FormEditorContext {
    private FormRenderContext renderContext;
    private Form originalForm;
    private Object path;

    public FormEditorContext(FormRenderContext ctx, Object path) {
        this.renderContext = ctx;
        this.path = path;
    }

    public Object getPath() {
        return path;
    }

    public void setPath(Object path) {
        this.path = path;
    }

    public Form getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(Form originalForm) {
        this.originalForm = originalForm;
    }

    public FormRenderContext getRenderContext() {
        return renderContext;
    }

    public Form getForm() {
        return renderContext.getForm();
    }

    public FormEditorContextTO getFormEditorContextTO() {
        return new FormEditorContextTO(renderContext.getUID(), renderContext.getForm().getId(), renderContext.getForm().getName(), path);
    }
}
