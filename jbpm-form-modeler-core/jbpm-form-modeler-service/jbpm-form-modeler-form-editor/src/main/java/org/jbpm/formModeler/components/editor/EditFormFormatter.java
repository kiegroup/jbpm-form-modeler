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
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.model.Form;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditFormFormatter extends Formatter {

    @Inject
    private Log log;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Form form = WysiwygFormEditor.lookup().getCurrentEditForm();

            setFormAttributes(form);
            renderFragment("outputStart");

            setFormAttributes(form);
            renderFragment("outputNameInput");

            renderFragment("outputStatusInputStart");

            int[] statuses = new int[]{FormManager.FORMSTATUS_NORMAL, FormManager.FORMSTATUS_HIDDEN};
            for (int i = 0; i < statuses.length; i++) {
                int status = statuses[i];
                setAttribute("optionValue", status);
                setAttribute("selected", form.getStatus().intValue() == status ? "selected" : "");
                renderFragment("outputStatusInputOption");
            }
            renderFragment("outputStatusInputEnd");


            setFormAttributes(form);
            renderFragment("outputProcessorInput");

            if (form.getDisplayMode() == null || "".equals(form.getDisplayMode())) {
                form.setDisplayMode("default");
            }
            renderFragment("outputDisplayModeStart");
            setAttribute("checked", form.getDisplayMode().equals(Form.DISPLAY_MODE_DEFAULT) ? "checked" : "");
            renderFragment("outputDefaultDisplayMode");
            setAttribute("checked", form.getDisplayMode().equals(Form.DISPLAY_MODE_ALIGNED) ? "checked" : "");
            renderFragment("outputAlignedDisplayMode");
            setAttribute("checked", form.getDisplayMode().equals(Form.DISPLAY_MODE_NONE) ? "checked" : "");
            renderFragment("outputNoneDisplayMode");
            //setAttribute("checked", form.getDisplayMode().equals(Form.DISPLAY_MODE_TEMPLATE) ? "checked" : "");
            //renderFragment("outputTemplateDisplayMode");

            renderFragment("outputLabelModeStart");
            String[] possibleLabelModes = {
                    Form.LABEL_MODE_UNDEFINED,
                    Form.LABEL_MODE_BEFORE,
                    Form.LABEL_MODE_AFTER,
                    Form.LABEL_MODE_LEFT,
                    Form.LABEL_MODE_RIGHT,
                    Form.LABEL_MODE_HIDDEN,
            };
            for (int i = 0; i < possibleLabelModes.length; i++) {
                String labelMode = possibleLabelModes[i];
                setAttribute("labelMode", labelMode);
                boolean selected = labelMode.equals(form.getLabelMode());
                if (form.getLabelMode() == null || form.getLabelMode().equals(""))
                    selected = (i == 0);
                renderFragment("outputLabelMode" + (selected ? "Selected" : ""));
            }
            renderFragment("outputLabelModeEnd");

            renderFragment("outputDisplayModeEnd");

            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error:", e);
        }

    }

    protected void setFormAttributes(Form form) {
        setAttribute("form", form);
        setAttribute("formDisplayMode", form.getDisplayMode());
        setAttribute("formStatus", form.getStatus());
        setAttribute("formName", form.getName());
    }
}
