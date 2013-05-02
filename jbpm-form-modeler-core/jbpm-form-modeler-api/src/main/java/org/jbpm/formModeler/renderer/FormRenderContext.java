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
package org.jbpm.formModeler.renderer;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;

import java.util.Map;

public class FormRenderContext {
    private String UID;
    private Form form;
    private Map<String, Object> bindingData;
    private FormRenderListener formRenderListener;

    public FormRenderContext(String uid, Form form, Map<String, Object> bindingData, FormRenderListener formRenderListener) {
        this.UID = uid;
        this.form = form;
        this.bindingData = bindingData;
        this.formRenderListener = formRenderListener;

        FormProcessor formProcessor = (FormProcessor) CDIHelper.getBeanByType(FormProcessor.class);
        formProcessor.read(uid);
    }

    public String getUID() {
        return UID;
    }

    public Form getForm() {
        return form;
    }

    public Map<String, Object> getBindingData() {
        return bindingData;
    }
    public FormRenderListener getFormRenderListener() {
        return formRenderListener;
    }

    public void setFormRenderListener(FormRenderListener formRenderListener) {
        this.formRenderListener = formRenderListener;
    }
}
