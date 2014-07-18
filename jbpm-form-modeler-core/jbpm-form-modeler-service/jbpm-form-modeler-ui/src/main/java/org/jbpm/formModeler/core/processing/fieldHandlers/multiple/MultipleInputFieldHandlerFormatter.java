package org.jbpm.formModeler.core.processing.fieldHandlers.multiple;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatter;
import org.jbpm.formModeler.core.processing.fieldHandlers.FieldHandlerParametersReader;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("MultipleInputFieldHandlerFormatter")
public class MultipleInputFieldHandlerFormatter extends DefaultFieldHandlerFormatter {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(request);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        String currentNamespace = paramsReader.getCurrentNamespace();
        Object fieldValue = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();

        boolean readOnly = field.getReadonly() || paramsReader.isFieldReadonly();

        Object[] values = (Object[]) fieldValue;

        FieldType bagType = getFieldTypeManager().getTypeByClass(field.getBag());

        if (bagType == null) return;

        FieldHandler handler = getFieldHandlersManager().getHandler(bagType);

        String uid = namespaceManager.squashInputName(fieldName);
        setAttribute("uid", uid);
        setAttribute("formId", form.getId());
        setAttribute("namespace", currentNamespace);
        setAttribute("fieldName", fieldName);
        renderFragment("outputStart");

        if (!ArrayUtils.isEmpty(values)) {
            renderFragment("tableStart");
            renderFragment("startHeader");
            if (!readOnly) renderFragment("actionsColumn");
            renderFragment("itemsColumn");
            renderFragment("endHeader");
            for (int i = 0; i < values.length; i++) {
                renderFragment("startRow");
                if (!readOnly) {
                    setAttribute("uid", uid);
                    setAttribute("fieldName", fieldName);
                    setAttribute("index", i);
                    setAttribute("readOnly", readOnly);
                    renderFragment("rowAction");
                }

                renderFragment("inputRow");

                setRenderingAttributes(field, fieldName + FormProcessor.NAMESPACE_SEPARATOR + i, currentNamespace, values[i], readOnly, paramsReader.isWrongField());
                if (readOnly) includePage(handler.getPageToIncludeForDisplaying());
                else includePage(handler.getPageToIncludeForRendering());

                renderFragment("endRow");
            }
            renderFragment("tableEnd");
        }

        renderFragment("beforeEnd");

        if (!readOnly) {
            renderFragment("startAdd");
            setRenderingAttributes(field, fieldName, currentNamespace, null, readOnly, paramsReader.isWrongField());
            includePage(handler.getPageToIncludeForRendering());
            String addItemButtonText = field.getAddItemText().getValue(getLocaleManager().getCurrentLang());
            if (StringUtils.isEmpty(addItemButtonText)) addItemButtonText = "Add new item";
            setAttribute("readOnly", readOnly);
            setAttribute("addItemButtonText", addItemButtonText);
            setAttribute("fieldName", fieldName);
            setAttribute("uid", uid);
            renderFragment("endAdd");
        }
        renderFragment("outputEnd");

    }

    protected void setRenderingAttributes(Field field, String fieldName, String namespace, Object value, boolean isReadOnly, boolean isWrongField) {
        setAttribute(FormRenderingFormatter.ATTR_FIELD, field);
        setAttribute(FormRenderingFormatter.ATTR_VALUE, value);
        setAttribute(FormRenderingFormatter.ATTR_INPUT_VALUE, value != null ? value.toString() : "");
        setAttribute(FormRenderingFormatter.ATTR_FIELD_IS_WRONG, isWrongField);
        setAttribute(FormRenderingFormatter.ATTR_NAMESPACE, namespace);
        setAttribute(FormRenderingFormatter.ATTR_NAME, fieldName);
        setAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY, isReadOnly);
    }
}
