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
package org.jbpm.formModeler.core.processing.fieldHandlers;

import org.jbpm.formModeler.core.wrappers.Link;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LinkFieldHandlerFormatter extends DefaultFieldHandlerFormatter {

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(httpServletRequest);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        Object value = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();
        int position = paramsReader.getCurrentPosition();
        String namespace = paramsReader.getCurrentNamespace();
        boolean wrong = paramsReader.isWrongField();
        String inputValue = paramsReader.getInputValue();

        Boolean isDisabled = paramsReader.isFieldDisabled();
        Boolean isReadonly = paramsReader.isFieldReadonly();

        if (value != null) {
            if (((Link) value).getLink() == null)
                ((Link) value).setLink("");

            if (((Link) value).getName() == null)
                ((Link) value).setName("");

        } else {
            value = new Link("", "");
        }

        String name = "", link = "";
        if ("".equals(((Link) value).getLink())) {
            link = getFormProcessor().read(form, "").getCurrentInputValue(fieldName + "_link");
            ((Link) value).setLink(link == null ? "" : link);

        }
        if ("".equals(((Link) value).getName())) {
            name = getFormProcessor().read(form, "").getCurrentInputValue(fieldName + "_name");
            ((Link) value).setName(name == null ? "" : name);

        }

        setDefaultAttributes(field, form, namespace);

        setAttribute("value", value);
        setAttribute("wrong", wrong);
        setAttribute("inputValue", inputValue);
        setAttribute("position", position);
        setAttribute("name", fieldName);
        setAttribute("lang", getLang());
        setAttribute("uid", getFormManager().getUniqueIdentifier(form, namespace, field, fieldName));
        // Override the field's own disabled and readonly values with the ones coming from a parent formatter
        // that contains it if they're set to true.
        if (isDisabled) setAttribute("disabled", isDisabled);
        if (isReadonly) setAttribute("readonly", isReadonly);
        renderFragment("output");
    }
}
