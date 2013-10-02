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
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("WysiwygEditFormTemplateFormatter")
public class WysiwygEditFormTemplateFormatter extends Formatter {
    private Logger log = LoggerFactory.getLogger(WysiwygEditFormTemplateFormatter.class);

    @Inject
    private FormTemplateEditor formTemplateEditor;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            WysiwygFormEditor editor = WysiwygFormEditor.lookup();
            FormTemplateEditor templateEditor = formTemplateEditor;
            setAttribute("templateContent", templateEditor.getTemplateContent());
            setAttribute("templateToLoad", templateEditor.getTemplateToLoad());
            setAttribute("loadTemplate", Boolean.valueOf(templateEditor.isLoadTemplate()));
            setAttribute("genMode", templateEditor.getGenMode());
            setAttribute("formId", templateEditor.getFormId());
            setAttribute("form", templateEditor.getForm());
            renderFragment("output");
        }catch (Exception e){
            log.error("Something unexpected has happened editing Form template", e);
        }
    }
}
