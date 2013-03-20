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

import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;

/**
 *
 */
public class RangeInputTextFieldHandlerFormatter extends DefaultFieldHandlerFormatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(RangeInputTextFieldHandlerFormatter.class.getName());

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

        String formula = null;
        boolean forceShow = false;        

        if (log.isDebugEnabled())
            log.debug("Range Formula: [" + formula + "]");

            setDefaultAttributes(field, form, namespace);
            value = applyPattern(field, value);
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
            renderFragment(forceShow ? "outputForceShowMode" : "output");

    }

    protected Object applyPattern(Field field, Object value) {
        if (value != null) {
            if (value instanceof Double) {
                String pattern = field.getPattern();
                if (pattern == null || "".equals(pattern))
                    pattern = field.getFieldType().getPattern();
                if (pattern != null && !"".equals(value)) {
                    DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(getLocale());
                    nf.applyPattern(pattern);
                    value = nf.format(((Double) value).doubleValue());
                }
            }
            if (value instanceof Object[]) {
                String pattern = field.getPattern();
                if (pattern == null || "".equals(pattern))
                    pattern = field.getFieldType().getPattern();
                if (pattern != null) {
                    DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(getLocale());
                    nf.applyPattern(pattern);
                    Object[] values = (Object[]) value;
                    for (int i = 0; i < values.length; i++) {
                        Object object = values[i];
                        if (object != null && object instanceof Double) {
                            Double aDouble = (Double) object;
                            values[i] = nf.format(aDouble.doubleValue());
                        }
                    }
                    value = values;
                }
            }
        }
        return value;
    }

}
