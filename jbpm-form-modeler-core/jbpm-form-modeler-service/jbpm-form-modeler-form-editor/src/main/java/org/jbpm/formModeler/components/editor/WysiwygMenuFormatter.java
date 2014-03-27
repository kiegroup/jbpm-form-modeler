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

@Named("WysiwygMenuFormatter")
public class WysiwygMenuFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(WysiwygMenuFormatter.class);

    protected String[] options = {
            WysiwygFormEditor.EDITION_OPTION_BINDINGS_SOURCES,
            WysiwygFormEditor.EDITION_OPTION_BINDINGS_FIELDS,
            WysiwygFormEditor.EDITION_OPTION_FIELDTYPES,
            WysiwygFormEditor.EDITION_OPTION_FORM_PROPERTIES};

    protected String[] optionsImg = {
            WysiwygFormEditor.EDITION_OPTION_IMG_BINDINGS_SOURCES,
            WysiwygFormEditor.EDITION_OPTION_IMG_BINDINGS_FIELDS,
            WysiwygFormEditor.EDITION_OPTION_IMG_FIELDTYPES,
            WysiwygFormEditor.EDITION_OPTION_IMG_FORM_PROPERTIES};

    protected String [] optionVis = {
            WysiwygFormEditor.EDITION_OPTION_VIS_MODE_BINDINGS_SOURCE,
            WysiwygFormEditor.EDITION_OPTION_VIS_MODE_BINDINGS_FIELDS,
            WysiwygFormEditor.EDITION_OPTION_VIS_MODE_FIELDTYPES,
            WysiwygFormEditor.EDITION_OPTION_VIS_MODE_FORM_PROPERTIES};

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            WysiwygFormEditor editor = WysiwygFormEditor.lookup();
            renderFragment("outputStart");

            setAttribute("formName", editor.getCurrentForm().getName());
            renderFragment("outputHeader");

            renderFragment("beforeOptions");

            renderFragment("optionsOutputStart");

            String render = "shared";
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                String optionImg = optionsImg[i];
                setAttribute("optionImage", optionImg);
                setAttribute("optionName", option);
                if (option.equals(editor.getCurrentEditionOption())){
                    render= optionVis[i];
                    renderFragment( "outputSelectedOption" );
                } else {
                    renderFragment("outputOption");
                }

            }

            setAttribute("renderMode", editor.getRenderMode());
            setAttribute("displayBindings", editor.getDisplayBindings());
            setAttribute("displayGrid", editor.getDisplayGrid());
            if("shared".equals(render) ){
                setAttribute("displayCheckbox", Boolean.TRUE);
            } else {
                setAttribute("displayCheckbox", Boolean.FALSE);

            }
            renderFragment("optionsOutputEnd");


            setAttribute("editionPage", "menu/" + editor.getCurrentEditionOption() + ".jsp");

            if("shared".equals(render) ){
                if(editor.isShowingTemplateEdition()){
                    setAttribute("editionZone", "menu/editFormTemplate.jsp");
                    renderFragment("outputWithEditionZone");
                } else {
                    setAttribute("displayGrid", editor.getDisplayGrid());
                    renderFragment("outputWithFormEditionPage");
                }
            }
            else
                renderFragment("outputWithoutFormEditionPage");
            setAttribute("editionNamespace", editor.getCurrentFieldEditionNamespace());
            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
}