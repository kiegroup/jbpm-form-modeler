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
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Form;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("WysiwygFormFormatter")
public class WysiwygFormFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(WysiwygFormFormatter.class);

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        WysiwygFormEditor editor = WysiwygFormEditor.lookup();
        Form form = editor.getCurrentForm();
        String namespace = editor.getNamespace();

        try {
            if (form != null) {
                if (Form.RENDER_MODE_WYSIWYG_FORM.equals(editor.getRenderMode())) {
                    FormProcessingServices.lookup().getFormProcessor().clear(form, namespace);
                }

                setAttribute("form", form);
                setAttribute("namespace", namespace);
                setAttribute("renderMode", editor.getRenderMode());
                setAttribute("displayBindings", editor.getDisplayBindings());
                String displayMode = form.getDisplayMode();
                if (Form.DISPLAY_MODE_TEMPLATE.equals(displayMode)) {
                    setAttribute("showDisplayWarningMessage", true);
                    setAttribute("message", "templateDisplayWarning");
                }
                setAttribute("displayMode", displayMode);
                renderFragment("outputForm");
            }
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new FormatterException(e);
        }
    }
}
