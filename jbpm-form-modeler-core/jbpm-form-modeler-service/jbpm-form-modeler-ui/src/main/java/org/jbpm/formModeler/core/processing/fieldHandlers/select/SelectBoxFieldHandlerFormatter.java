package org.jbpm.formModeler.core.processing.fieldHandlers.select;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.config.SelectValuesProvider;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

@Named("SelectBoxFieldHandlerFormatter")
public class SelectBoxFieldHandlerFormatter extends DefaultFieldHandlerFormatter {
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

        String fieldName = paramsReader.getCurrentFieldName();

        String uid = namespaceManager.squashInputName(fieldName);

        Boolean isReadonly = paramsReader.isFieldReadonly() || field.getReadonly();

        setAttribute("size", 1);
        setAttribute("name", fieldName);
        setAttribute("uid", uid);
        setDefaultAttributes(field, paramsReader.getCurrentForm(), paramsReader.getCurrentNamespace());
        if (isReadonly) setAttribute("readonly", isReadonly);
        setAttribute("onChangeScript", field.getOnChangeScript());
        renderFragment("outputStart");
        renderFragment("outputOption");

        if (!StringUtils.isEmpty(field.getCustomFieldType())) {

            Object value = paramsReader.getCurrentFieldValue();

            SelectValuesProvider provider = (SelectValuesProvider) CDIBeanLocator.getBeanByNameOrType(field.getCustomFieldType());

            Map<String, String> fieldRange = provider.getSelectOptions(field, (String)value, formRenderContextManager.getRootContext(fieldName), getLocale());

            if (fieldRange != null && !fieldRange.isEmpty()) {

                String keyValueStr = StringEscapeUtils.escapeHtml(StringUtils.defaultString(value == null ? "" : String.valueOf(value)));

                for (Iterator iter = fieldRange.keySet().iterator(); iter.hasNext();) {
                    Object key = iter.next();
                    setAttribute("key", key);
                    String valueKey = fieldRange.get(key);
                    setAttribute("value", valueKey);
                    if (keyValueStr != null && keyValueStr.equals(key.toString())) {
                        renderFragment("outputSelectedOption");
                    } else {
                        renderFragment("outputOption");
                    }
                }
            }
        }
        renderFragment("outputEnd");
    }
}
