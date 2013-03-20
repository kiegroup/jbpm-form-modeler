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

import org.jbpm.formModeler.service.bb.mvc.components.handling.HandlerFactoryElement;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.model.Form;

public class FormTemplateEditor extends HandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormTemplateEditor.class.getName());

    private Long formId;
    private String templateContent;
    private boolean cancel;
    private boolean persist;
    private boolean loadTemplate;
    private String templateToLoad;
    private FormManagerImpl formManagerImpl;

    @Override
    public void start() throws Exception {
        super.start();
        formManagerImpl = FormManagerImpl.lookup();
    }

    public boolean isOn() {
        return formId != null;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
        if (formId != null) {
            try {
                setTemplateContent(getForm().getFormTemplate());
            } catch (Exception e) {
                log.error("Error: ", e);
            }
        }
    }

    public Form getForm() throws Exception {
        return formManagerImpl.getFormById(formId);
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public boolean isLoadTemplate() {
        return loadTemplate;
    }

    public void setLoadTemplate(boolean loadTemplate) {
        this.loadTemplate = loadTemplate;
    }

    public String getTemplateToLoad() {
        return templateToLoad;
    }

    public void setTemplateToLoad(String templateToLoad) {
        this.templateToLoad = templateToLoad;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public boolean containsField(String fieldName) {
        return templateContent != null && templateContent.indexOf(Form.TEMPLATE_FIELD + "{" + fieldName + "}") > -1;
    }

    public boolean containsLabel(String fieldName) {
        return templateContent != null && templateContent.indexOf(Form.TEMPLATE_LABEL + "{" + fieldName + "}") > -1;
    }

    public void actionSaveTemplate(CommandRequest request) throws Exception {
        if (isCancel()) {
            setFormId(null);
        } else {
            if (isPersist()) {
                formManagerImpl.saveTemplateForForm(getFormId(), getTemplateContent());
                setFormId(null);
            }
        }
    }
}
