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

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.jbpm.formModeler.api.model.Form;

import javax.servlet.http.HttpServletRequest;

public class FieldHandlerParametersReader {

    private HttpServletRequest httpServletRequest;

    public FieldHandlerParametersReader(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public Form getCurrentForm() {
        Field field = (Field) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD);
        if (field != null)
            return field.getForm();
        return null;
    }

    public int getCurrentPosition() {
        Field field = (Field) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD);
        if (field != null)
            return field.getPosition();
        return -1;
    }

    public Field getCurrentField() {
        Field field = (Field) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD);
        if (field != null)
            return field;
        return null;
    }

    public Object getCurrentFieldValue() {
        return httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_VALUE);
    }

    public String getCurrentFieldName() {
        return (String) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_NAME);
    }

    public String getCurrentNamespace() {
        return (String) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_NAMESPACE);
    }

    public boolean isWrongField() {
        Boolean b = (Boolean) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD_IS_WRONG);
        return b != null && b.booleanValue();
    }

    public String getInputValue() {
        return (String) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_INPUT_VALUE);
    }

    public Boolean isFieldReadonly() {
        Boolean b = (Boolean) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY);
        return b != null ? b : Boolean.FALSE;
    }

    public String getCurrentRenderMode() {
        return (String) httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FORM_RENDER_MODE);
    }

    public boolean isEditingForm() {
        String renderMode = getCurrentRenderMode();
        return Form.RENDER_MODE_WYSIWYG_FORM.equals(renderMode) || Form.RENDER_MODE_WYSIWYG_DISPLAY.equals(renderMode);
    }
}
