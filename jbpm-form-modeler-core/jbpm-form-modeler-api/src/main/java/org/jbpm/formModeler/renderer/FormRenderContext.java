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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.util.helpers.CDIHelper;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Portable
public class FormRenderContext {
    private String UID;
    private Long formId;
    private Map<String, Object> bindingData;
    private FormRenderListener formRenderListener;

    public FormRenderContext() {
    }

    public FormRenderContext(String uid, Long formId, Map<String, Object> bindingData, FormRenderListener formRenderListener) {
        this.UID = uid;
        this.formId = formId;
        this.bindingData = bindingData;
        this.formRenderListener = formRenderListener;
        FormProcessor formProcessor = (FormProcessor) CDIHelper.getBeanByType(FormProcessor.class);

        formProcessor.read(formId, uid, getParameterMap());
    }

    private Map getParameterMap() {
        Map values = new HashMap();

        if (bindingData != null) {
            Form form = getForm();

            Set<DataHolder> holders = form.getHolders();

            for (DataHolder holder : holders) {
                Object value = bindingData.get(holder.getId());
                if (value != null) {
                    Set<DataFieldHolder> fieldHodlers = holder.getFieldHolders();
                    for (DataFieldHolder fieldHolder : fieldHodlers) {
                        values.put(fieldHolder.getId(), holder.readValue(fieldHolder.getId()));
                    }
                }
            }
        }

        return values;
    }

    public String getUID() {
        return UID;
    }

    public Form getForm() {
        return ((FormManager)CDIHelper.getBeanByType(FormManager.class)).getFormById(formId);
    }

    public Long getFormId() {
        return formId;
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
