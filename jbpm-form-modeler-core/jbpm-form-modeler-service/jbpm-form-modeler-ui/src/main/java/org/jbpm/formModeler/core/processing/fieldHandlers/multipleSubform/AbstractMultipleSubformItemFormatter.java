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

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Map;

public abstract class AbstractMultipleSubformItemFormatter extends DefaultFieldHandlerFormatter {

    @Inject
    protected SubFormHelper helper;

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        String currentNamespace = paramsReader.getCurrentNamespace();
        Object value = paramsReader.getCurrentFieldValue();

        String fieldNS = paramsReader.getCurrentFieldName();

        Integer position = getItemPosition(fieldNS);

        boolean isReadonly = paramsReader.isFieldReadonly();

        String fieldUID = namespaceManager.squashInputName(fieldNS);

        if ( position != null && value != null ) {
            if (value.getClass().isArray()) {
                String rowNamespace = fieldNS + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + position;
                getFormProcessor().read(form, rowNamespace, ((Map[]) value)[position]);

                Form formToRender = getForm(field, currentNamespace);
                Map valueToEdit = (Map) Array.get(value, position);
                if (formToRender != null) {
                    setAttribute("value", valueToEdit);
                    setAttribute("name", fieldNS);
                    setAttribute("form", formToRender);
                    setAttribute("uid", fieldUID);
                    setAttribute("index", position);
                    setAttribute("parentFormId", form.getId());
                    setAttribute("namespace", fieldNS);
                    setAttribute("parentNamespace", currentNamespace);
                    setAttribute("field", field.getFieldName());

                    // Override the field's own disabled and readonly values with the ones coming from a parent formatter
                    // that contains it if they're set to true.
                    if (isReadonly) setAttribute("readonly", isReadonly);

                    renderFragment("output");
                } else {
                    renderFragment("noShowDataForm");
                }
            }
        }

    }

    protected abstract Integer getItemPosition( String namespace );
    protected abstract Form getForm( Field field, String currentNamespace);
}
