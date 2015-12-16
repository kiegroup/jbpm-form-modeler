/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("MultipleSubformCreateItemFormatter")
public class MultipleSubformCreateItemFormatter extends DefaultFieldHandlerFormatter {

    @Inject
    protected SubFormHelper helper;

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {

        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader( request );

        Field field = paramsReader.getCurrentField();
        String currentNamespace = paramsReader.getCurrentNamespace();
        String fieldNS = paramsReader.getCurrentFieldName();

        String renderMode = paramsReader.getCurrentRenderMode();

        boolean isReadonly = paramsReader.isFieldReadonly();

        CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());
        Form enterDataForm = fieldHandler.getCreateForm(field, currentNamespace);
        boolean disableCreateNew = Boolean.TRUE.equals(field.getHideCreateItem());
        if (enterDataForm != null && !disableCreateNew) {
            setAttribute("form", enterDataForm);
            setAttribute("namespace", fieldNS + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");

            setAttribute("uid", namespaceManager.squashInputName(fieldNS));
            setAttribute("name", fieldNS);
            setAttribute("entityName", "");
            boolean expanded = false;
            if (Boolean.TRUE.equals(field.getHideContent())) {
                expanded = true;
                setAttribute("noCancelButton", true);
            } else {
                expanded = fieldNS.equals( helper.getExpandedField( fieldNS ) );
            }
            setAttribute("expanded", expanded);


            String newItemButtonText = field.getNewItemText().getValue(getLocaleManager().getCurrentLang());
            if (StringUtils.isEmpty(newItemButtonText)) newItemButtonText = "Create";
            setAttribute("newItemButtonText", newItemButtonText);

            String addItemButtonText = field.getAddItemText().getValue(getLocaleManager().getCurrentLang());
            if (StringUtils.isEmpty(addItemButtonText)) addItemButtonText = "Add new item";
            setAttribute("addItemButtonText", addItemButtonText);

            String cancelButtonText = field.getCancelItemText().getValue(getLocaleManager().getCurrentLang());
            if (StringUtils.isEmpty(cancelButtonText)) cancelButtonText = "Cancel";
            setAttribute("cancelButtonText", cancelButtonText);

            // Override the field's own disabled and readonly values with the ones coming from a parent formatter
            // that contains it if they're set to true.
            if (isReadonly) setAttribute("readonly", isReadonly);
            setAttribute("renderMode", renderMode);
            renderFragment("output");
        }
    }
}
