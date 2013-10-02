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

import org.slf4j.Logger;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.api.model.Form;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
//@ApplicationScoped
@Named("FormTemplateEditor")
public class FormTemplateEditor extends BeanHandler {

    private Logger log = LoggerFactory.getLogger(FormTemplateEditor.class);


    private String templateContent;
    private boolean cancel;
    private boolean persist;
    private boolean loadTemplate;
    private String templateToLoad;
    private String genMode;

    private boolean showTemplate = false;
    WysiwygFormEditor wysiwygFormEditor= WysiwygFormEditor.lookup();

    public boolean isOn() {
        return showTemplate;
    }

    public void setShowTemplate(boolean showTemplate) {
        this.showTemplate = showTemplate;
    }

    public Long getFormId() throws Exception{
        return getForm().getId();
    }

    public void setFormId(Long formId) {
        if (formId != null) {
            try {
                setTemplateContent(getForm().getFormTemplate());
            } catch (Exception e) {
                log.error("Error: ", e);
            }
            this.showTemplate =true;
        }
        else{
            this.showTemplate =false;
        }
    }

    public Form getForm() throws Exception {
        return wysiwygFormEditor.getCurrentForm();
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

    public String getGenMode() {
        return genMode;
    }

    public void setGenMode(String genMode) {
        this.genMode = genMode;
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
        if (!isCancel()) {
            if (isPersist()) {
                wysiwygFormEditor.getCurrentForm().setFormTemplate(getTemplateContent());
                setFormId(null);
            }
        }
        setShowTemplate(false);
    }
}
