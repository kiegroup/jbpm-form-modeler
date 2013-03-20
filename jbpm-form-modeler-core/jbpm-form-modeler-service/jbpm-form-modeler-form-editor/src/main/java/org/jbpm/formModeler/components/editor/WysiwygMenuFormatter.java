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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WysiwygMenuFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WysiwygMenuFormatter.class.getName());

    private WysiwygFormEditor editor;

    public WysiwygFormEditor getEditor() {
        return editor;
    }

    public void setEditor(WysiwygFormEditor editor) {
        this.editor = editor;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            renderFragment("outputStart");

            setAttribute("formName", editor.getCurrentEditForm().getName());
            renderFragment("outputHeader");

            renderFragment("beforeOptions");

            renderFragment("optionsOutputStart");
            String[] options = {WysiwygFormEditor.EDITION_OPTION_FIELDTYPES, WysiwygFormEditor.EDITION_OPTION_ENTITYFIELDS, WysiwygFormEditor.EDITION_OPTION_FORM_PROPERTIES, WysiwygFormEditor.EDITION_OPTION_FORM_EDITION_PROPERTIES};
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                setAttribute("optionName", option);
                renderFragment(option.equals(editor.getCurrentEditionOption()) ? "outputSelectedOption" : "outputOption");
            }
            renderFragment("optionsOutputEnd");

            setAttribute("editionPage", "menu/" + editor.getCurrentEditionOption() + ".jsp");
            renderFragment("outputEditionPage");

            renderFragment("afterOptions");
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
}
