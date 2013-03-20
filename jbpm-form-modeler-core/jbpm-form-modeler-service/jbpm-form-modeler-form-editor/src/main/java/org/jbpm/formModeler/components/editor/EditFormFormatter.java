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
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.model.Form;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class EditFormFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(EditFormFormatter.class.getName());

    private WysiwygFormEditor wysiwygFormEditor;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            Form formulary = wysiwygFormEditor.getCurrentEditForm();

            setFormularyAttributes(formulary);
            renderFragment("outputStart");

            setFormularyAttributes(formulary);
            renderFragment("outputNameInput");

            renderFragment("outputStatusInputStart");

            int[] statuses = new int[]{FormManager.FORMSTATUS_NORMAL, FormManager.FORMSTATUS_HIDDEN};
            for (int i = 0; i < statuses.length; i++) {
                int status = statuses[i];
                setAttribute("optionValue", status);
                setAttribute("selected", formulary.getStatus().intValue() == status ? "selected" : "");
                renderFragment("outputStatusInputOption");
            }
            renderFragment("outputStatusInputEnd");


            setFormularyAttributes(formulary);
            renderFragment("outputProcessorInput");

            setFormularyAttributes(formulary);
            renderFragment("outputDefaultFormInput");

            setAttribute("defaultView", formulary.getDefaultView() != null && formulary.getDefaultView().booleanValue());
            renderFragment("outputDefaultViewFormInput");

            setAttribute("shortView", formulary.getShortView() != null && formulary.getShortView().booleanValue());
            renderFragment("outputShortViewFormInput");

            setAttribute("creationView", formulary.getCreationView() != null && formulary.getCreationView().booleanValue());
            renderFragment("outputCreationViewFormInput");

            setAttribute("searchView", formulary.getSearchView() != null && formulary.getSearchView().booleanValue());
            renderFragment("outputSearchViewFormInput");

            setAttribute("resultView", formulary.isResultView());
            renderFragment("outputResultViewFormInput");

            if (formulary.getDisplayMode() == null || "".equals(formulary.getDisplayMode())) {
                formulary.setDisplayMode("default");
            }
            renderFragment("outputDisplayModeStart");
            setAttribute("checked", formulary.getDisplayMode().equals(Form.DISPLAY_MODE_DEFAULT) ? "checked" : "");
            renderFragment("outputDefaultDisplayMode");
            setAttribute("checked", formulary.getDisplayMode().equals(Form.DISPLAY_MODE_ALIGNED) ? "checked" : "");
            renderFragment("outputAlignedDisplayMode");
            setAttribute("checked", formulary.getDisplayMode().equals(Form.DISPLAY_MODE_NONE) ? "checked" : "");
            renderFragment("outputNoneDisplayMode");
            setAttribute("checked", formulary.getDisplayMode().equals(Form.DISPLAY_MODE_TEMPLATE) ? "checked" : "");
            renderFragment("outputTemplateDisplayMode");
            setAttribute("checked", formulary.getDisplayMode().equals(Form.DISPLAY_MODE_CUSTOM) ? "checked" : "");
            setAttribute("pageValue", formulary.getCustomRenderPage());
            renderFragment("outputCustomDisplayMode");

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
                boolean selected = labelMode.equals(formulary.getLabelMode());
                if (formulary.getLabelMode() == null || formulary.getLabelMode().equals(""))
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

    protected void setFormularyAttributes(Form formulary) {
        setAttribute("formulary", formulary);
        setAttribute("formDisplayMode", formulary.getDisplayMode());
        setAttribute("formStatus", formulary.getStatus());
        setAttribute("formName", formulary.getName());
        setAttribute("default", formulary.getDefault() != null && formulary.getDefault().booleanValue());
    }

    public WysiwygFormEditor getWysiwygFormEditor() {
        return wysiwygFormEditor;
    }

    public void setWysiwygFormEditor(WysiwygFormEditor wysiwygFormEditor) {
        this.wysiwygFormEditor = wysiwygFormEditor;
    }
}
