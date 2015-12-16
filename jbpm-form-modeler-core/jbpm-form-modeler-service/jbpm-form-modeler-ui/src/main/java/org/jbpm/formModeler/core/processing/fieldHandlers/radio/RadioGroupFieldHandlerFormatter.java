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

package org.jbpm.formModeler.core.processing.fieldHandlers.radio;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.config.SelectValuesProvider;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Named("RadioGroupFieldHandlerFormatter")
public class RadioGroupFieldHandlerFormatter extends DefaultFieldHandlerFormatter {
    public static final String PARAM_MODE = "show_mode";
    public static final String MODE_SHOW = "show";
    public static final String MODE_INPUT = "input";

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        String mode = (String) getParameter(PARAM_MODE);

        if (MODE_INPUT.equals(mode)) renderInput(request);
        else renderShow(request);

    }
    public void renderShow(HttpServletRequest request) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);

        Field field = paramsReader.getCurrentField();
        if (StringUtils.isEmpty(field.getCustomFieldType())) return;

        Object value = paramsReader.getCurrentFieldValue();

        String fieldName = paramsReader.getCurrentFieldName();

        SelectValuesProvider provider = (SelectValuesProvider) CDIBeanLocator.getBeanByNameOrType(field.getCustomFieldType());

        Map<String, String> fieldRange = provider.getSelectOptions(field, (String)value, formRenderContextManager.getRootContext(fieldName), getLocale());

        if (fieldRange == null || fieldRange.isEmpty()) return;

        String text = fieldRange.get(value);

        if (StringUtils.isEmpty(text)) return;

        setAttribute("value", text);
        renderFragment("output");
    }

    public void renderInput(HttpServletRequest request) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);

        Field field = paramsReader.getCurrentField();

        if (StringUtils.isEmpty(field.getCustomFieldType())) return;

        Object value = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();

        SelectValuesProvider provider = (SelectValuesProvider) CDIBeanLocator.getBeanByNameOrType(field.getCustomFieldType());

        Map<String, String> fieldRange = provider.getSelectOptions(field, (String)value, formRenderContextManager.getRootContext(fieldName), getLocale());

        if (fieldRange == null || fieldRange.isEmpty()) return;

        String uid = namespaceManager.squashInputName(fieldName);

        Boolean isReadonly = paramsReader.isFieldReadonly() || field.getReadonly();

        String keyValueStr = String.valueOf(value);

        setAttribute("name", fieldName);
        setAttribute("uid", uid);
        setAttribute("value", keyValueStr);
        if (isReadonly) setAttribute("readonly", isReadonly);
        renderFragment("outputStart");

        if (field.getVerticalAlignment()) renderVertical(field, fieldName, paramsReader.isFieldReadonly(), uid, fieldRange, StringUtils.defaultString((String) value));
        else renderHorizontal(field, fieldName, paramsReader.isFieldReadonly(), uid, fieldRange, StringUtils.defaultString((String) value));

        renderFragment("outputEnd");

    }

    protected void renderHorizontal(Field field, String fieldName, Boolean isReadonly, String uid, Map<String, String> fieldRange, String value) {
        int index = 0;

        int maxElements = getMaxElements(fieldRange, field);

        int cellCount = 0;

        for (Iterator iter = fieldRange.keySet().iterator(); iter.hasNext();) {
            if (cellCount == 0) renderFragment("startRow");
            String key = (String) iter.next();
            String keyValue = fieldRange.get(key);
            renderFragment("startCell");
            renderRadio(key, keyValue, key.equals(value), fieldName, uid + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + index++, isReadonly, field);
            renderFragment("endCell");
            cellCount ++;
            if (cellCount == maxElements) {
                cellCount = 0;
                renderFragment("endRow");
            }
        }
    }

    protected void renderVertical(Field field, String fieldName, Boolean isReadonly, String uid, Map<String, String> fieldRange, String value) {

        int maxElements = getMaxElements(fieldRange, field);

        List<List<String>> radioTable = new ArrayList<List<String>>();

        int col = 0;

        for (Iterator iter = fieldRange.keySet().iterator(); iter.hasNext();) {
            if (radioTable.size() == col) {
                radioTable.add(new ArrayList<String>());
            }
            List<String> currentColumn = radioTable.get(col);

            currentColumn.add(iter.next().toString());

            if (currentColumn.size() == maxElements) col ++;
        }

        int index = 0;

        for (int row = 0; row < maxElements; row++) {
            renderFragment("startRow");
            for (int i = 0; i < radioTable.size(); i++) {
                List<String> column = radioTable.get(i);

                renderFragment("startCell");

                if (column.size() > row) {
                    String key = column.get(row);
                    renderRadio(key, fieldRange.get(key), key.equals(value), fieldName, uid + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + index++, isReadonly, field);
                }
                renderFragment("endCell");
            }
            renderFragment("endRow");
        }
    }

    protected void renderRadio(String key, String keyValue, Boolean checked, String fieldName, String uid, Boolean isReadonly, Field field) {
        setAttribute("name", fieldName);
        setAttribute("uid", uid);
        setAttribute("key", key);
        setAttribute("value", keyValue);
        setAttribute("checked", checked);
        setAttribute("readonly", isReadonly);
        setAttribute("onChangeScript", field.getOnChangeScript());
        setAttribute("cssStyle", field.getCssStyle());
        setAttribute("styleclass", field.getStyleclass());
        renderFragment("outputRadio");
    }

    protected int getMaxElements(Map<String, String> fieldRange, Field field) {
        int maxElements = (field.getMaxlength() != null) ? field.getMaxlength().intValue() : fieldRange.keySet().size();

        if (maxElements < 1) maxElements = fieldRange.keySet().size();

        return maxElements;
    }
}
