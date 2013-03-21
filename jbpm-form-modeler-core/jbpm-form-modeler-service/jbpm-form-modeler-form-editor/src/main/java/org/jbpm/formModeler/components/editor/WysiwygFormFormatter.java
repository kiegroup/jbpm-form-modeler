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

import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.FormProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WysiwygFormFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WysiwygFormFormatter.class.getName());

    private WysiwygFormEditor editor;
    private FormProcessor defaultFormProcessor;

    public WysiwygFormEditor getEditor() {
        return editor;
    }

    public void setEditor(WysiwygFormEditor editor) {
        this.editor = editor;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        Form form = editor.getCurrentForm();
        String namespace = "wysiwyg_form_preview";

        try {
            if (form != null) {
                if (Form.RENDER_MODE_WYSIWYG_FORM.equals(editor.getRenderMode()))defaultFormProcessor.clear(form.getId(), namespace);

                setAttribute("form", form);
                setAttribute("namespace", namespace);
                setAttribute("renderMode", editor.getRenderMode());
                String displayMode = form.getDisplayMode();
                if (Form.DISPLAY_MODE_TEMPLATE.equals(displayMode)) {
                    setAttribute("showDisplayWarningMessage", true);
                    setAttribute("message", "templateDisplayWarning");
                } else if (Form.DISPLAY_MODE_CUSTOM.equals(displayMode)) {
                    setAttribute("showDisplayWarningMessage", true);
                    setAttribute("message", "customJSPDisplayWarning");
                }
                setAttribute("displayMode", displayMode);
                renderFragment("outputForm");
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new FormatterException(e);
        }

    }

    public FormProcessor getDefaultFormProcessor() {
        return defaultFormProcessor;
    }

    public void setDefaultFormProcessor(FormProcessor defaultFormProcessor) {
        this.defaultFormProcessor = defaultFormProcessor;
    }
}
