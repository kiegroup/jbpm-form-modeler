/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.formModeler.core.processing.fieldHandlers.date;

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

@ApplicationScoped
@Named("DateFieldHandlerFormatter")
public class DateFieldHandlerFormatter extends DefaultFieldHandlerFormatter {

    public static final String FLAT_SEPARATOR = "_";

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);
        Field field = paramsReader.getCurrentField();

        Form form = paramsReader.getCurrentForm();
        String namespace = paramsReader.getCurrentNamespace();
        Object value = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();

        setDefaultAttributes(field, form, namespace);

        Boolean isReadonly = paramsReader.isFieldReadonly();

        if (isReadonly) setAttribute("readonly", isReadonly);

        FieldHandlersManager fieldHandlersManager = FormProcessingServices.lookup().getFieldHandlersManager();
        DateFieldHandler dateFieldHandler;

        if (StringUtils.isEmpty(field.getBag())) dateFieldHandler = (DateFieldHandler) fieldHandlersManager.getHandler(field.getFieldType());
        else {
            FieldType bagType = getFieldTypeManager().getTypeByClass(field.getBag());
            dateFieldHandler = (DateFieldHandler) fieldHandlersManager.getHandler(bagType);
        }

        String inputPattern = dateFieldHandler.getDefaultPattern();

        if (!StringUtils.isEmpty(dateFieldHandler.getDefaultPatterTimeSuffix())) {
            inputPattern += " " + dateFieldHandler.getDefaultPatterTimeSuffix();
        }

        SimpleDateFormat sdf = new SimpleDateFormat(inputPattern, LocaleManager.currentLocale());
        String dateValue = "";
        if (value != null) dateValue = sdf.format(value);

        String uid = namespaceManager.squashInputName(fieldName);

        setAttribute("uid", uid);
        setAttribute("flatUid", getFlatUid(uid));
        setAttribute("name", fieldName);
        setAttribute("value", dateValue);
        setAttribute("inputPattern", dateFieldHandler.getDefaultJQueryPattern());
        setAttribute("timePattern", dateFieldHandler.getDefaultPatterTimeSuffix());
        setAttribute("onChangeScript", field.getOnChangeScript());
        renderFragment("output");
    }

    public String getFlatUid(String uid) {
        uid = StringUtils.replace(uid, FormProcessor.NAMESPACE_SEPARATOR, FLAT_SEPARATOR);
        return StringUtils.replace(uid, FormProcessor.CUSTOM_NAMESPACE_SEPARATOR, FLAT_SEPARATOR);
    }
}
