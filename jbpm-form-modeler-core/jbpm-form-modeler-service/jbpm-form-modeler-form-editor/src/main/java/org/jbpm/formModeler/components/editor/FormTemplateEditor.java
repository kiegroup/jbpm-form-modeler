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

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.api.model.Form;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

//@SessionScoped
@ApplicationScoped
public class FormTemplateEditor extends BeanHandler {

    @Inject
    private Log log;

    private Long formId;
    private String templateContent;
    private boolean cancel;
    private boolean persist;
    private boolean loadTemplate;
    private String templateToLoad;

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
        return FormCoreServices.lookup().getFormManager().getFormById(formId);
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
                FormCoreServices.lookup().getFormManager().saveTemplateForForm(getFormId(), getTemplateContent());
                setFormId(null);
            }
        }
    }
}
