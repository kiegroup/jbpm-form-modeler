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

import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Formatter for simple fields
 */
@ApplicationScoped
@Named("SimpleFieldHandlerFormatter")
public class SimpleFieldHandlerFormatter extends DefaultFieldHandlerFormatter {

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

        Boolean isReadonly = paramsReader.isFieldReadonly();

        setDefaultAttributes(field, form, namespace);
        value = applyPattern(field, value);
        setAttribute("localeManager", getLocaleManager());
        setAttribute("value", value);
        setAttribute("wrong", wrong);
        setAttribute("inputValue", inputValue);
        setAttribute("formId", form != null ? form.getId() : null);
        setAttribute("formNamespace", namespace);
        setAttribute("position", position);
        setAttribute("name", fieldName);
        setAttribute("formFieldName", field != null ? field.getFieldName() : null);
        setAttribute("lang", getLang());
        setAttribute("isEditMode", paramsReader.isEditingForm());
        setAttribute("uid", getFormManager().getUniqueIdentifier(form, namespace, field, fieldName));

        // Override the field's own readonly values with the ones coming from a parent formatter
        // that contains it if they're set to true.
        if (isReadonly) setAttribute("readonly", isReadonly);
        renderFragment("output");
    }

    protected Object applyPattern(Field field, Object value) {
        if (value instanceof Double) {
            String pattern = field.getPattern();
            if (pattern == null || "".equals(pattern))
                pattern = field.getFieldType().getPattern();
            if (pattern != null && !"".equals(value)) {
                DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(new Locale(LocaleManager.currentLang()));
                nf.applyPattern(pattern);
                return nf.format(((Double) value).doubleValue());
            }
        }
        if (value instanceof Double[]) {
            String pattern = field.getPattern();
            if (pattern == null || "".equals(pattern))
                pattern = field.getFieldType().getPattern();
            if (pattern != null) {
                DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(new Locale(LocaleManager.currentLang()));
                nf.applyPattern(pattern);
                Double[] values = (Double[]) value;
                Object[] _res = new Object[values.length];
                for (int i = 0; i < values.length; i++) {
                    Double d = values[i];
                    if (d != null) {
                        _res[i] = nf.format(d.doubleValue());
                    }
                }
                return _res;
            }
        }
        return value;
    }
}
