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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.checkers.FormCheckResult;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.checkers.SubformChecker;
import org.jbpm.formModeler.core.processing.formRendering.FieldI18nResourceObtainer;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.*;

@Named("CreateDynamicObjectFieldFormatter")
public class CreateDynamicObjectFieldFormatter extends DefaultFieldHandlerFormatter {
    private static transient Logger log = LoggerFactory.getLogger(CreateDynamicObjectFieldFormatter.class);
    public static final String PARAM_DISPLAYPAGE = "displayPage";

    public static final String PROPERTY_DISALLOW_CREATE_NEW = "disallowCreateNew";
    public static final String PROPERTY_MODIFICABLE = "modificable";
    public static final String PROPERTY_DELETEABLE = "deleteable";
    public static final String PROPERTY_VISUALIZABLE = "visualizable";
    public static final String PROPERTY_PREVIEW_ITEMS = "previewItems";
    public static final String PROPERTY_SEPARATOR = "htmlContent";

    @Inject
    protected FieldI18nResourceObtainer fieldI18nResourceObtainer;

    protected Boolean isReadonly;

    protected String renderMode;


    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        Boolean displayPage = Boolean.valueOf((String) getParameter(PARAM_DISPLAYPAGE));
        FieldHandlerParametersReader paramsReader = new FieldHandlerParametersReader(httpServletRequest);

        Field field = paramsReader.getCurrentField();
        Form form = paramsReader.getCurrentForm();
        String currentNamespace = paramsReader.getCurrentNamespace();
        Object value = paramsReader.getCurrentFieldValue();
        String fieldName = paramsReader.getCurrentFieldName();
        renderMode = paramsReader.getCurrentRenderMode();

        isReadonly = paramsReader.isFieldReadonly();

        CreateDynamicObjectFieldHandler fHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());

        Form createForm = fHandler.getCreateForm(field, currentNamespace);

        Set checkers = fHandler.getSubformCheckers();
        for(Iterator it = checkers.iterator(); it.hasNext();) {
            SubformChecker checker = (SubformChecker) it.next();
            FormCheckResult result = checker.checkForm(createForm);
            if (!result.isValid()) {
                setAttribute("error", result.getMessageKey());
                renderFragment("renderError");
                return;
            }
        }

        if (!fHandler.checkSubformDepthAllowed(form, currentNamespace)) return;

        if (!displayPage.booleanValue() && field != null && Boolean.TRUE.equals(field.getReadonly())) {
            includePage(fHandler.getPageToIncludeForDisplaying());
            return;
        }

        Integer previewIndex = null;
        Map previewIndexes = (Map) getFormProcessor().getAttribute(form, currentNamespace, FormStatusData.PREVIEW_FIELD_POSITIONS);
        if (previewIndexes != null) {
            previewIndex = ((Integer) previewIndexes.get(field.getFieldName()));
        }

        Integer editIndex = null;
        Map editIndexes = (Map) getFormProcessor().getAttribute(form, currentNamespace, FormStatusData.EDIT_FIELD_POSITIONS);
        if (editIndexes != null) {
            editIndex = ((Integer) editIndexes.get(field.getFieldName()));
        }

        setDefaultAttributes(field, form, currentNamespace);
        String height = (field.getHeight() != null && !"".equals(field.getHeight())) ? field.getHeight() : "100";
        setAttribute("uid", getFormManager().getUniqueIdentifier(form, currentNamespace, field, field.getFieldName()));

        setAttribute("tableEnterMode", Boolean.TRUE.equals(field.getEnableTableEnterData()));

        int count = 0;

        if (value != null) {
            count = ((Object[]) value).length;
        }

        setAttribute("count", count);
        setAttribute("name", fieldName);

        setAttribute("heightDesired", height);
        renderFragment("outputStart");

        if (previewIndex != null) {
            //Preview item
            String rowNamespace = currentNamespace +
                    FormProcessor.NAMESPACE_SEPARATOR + form.getId() +
                    FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName() + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + previewIndex;
            getFormProcessor().read(form, rowNamespace, ((Map[]) value)[previewIndex]);

            previewItemInPosition(form, currentNamespace, previewIndex.intValue(), field, value);
        } else if (editIndex != null) {
            // Edit item
            String rowNamespace = currentNamespace +
                    FormProcessor.NAMESPACE_SEPARATOR + form.getId() +
                    FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName() + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + editIndex;
            getFormProcessor().read(form, rowNamespace, ((Map[]) value)[editIndex]);
            editItemInPosition(form, currentNamespace, editIndex.intValue(), field, value, fieldName);
        } else {
            //First render the table.
            renderFragment("beforeItemsTable");
            renderExistingItemsTable(form, currentNamespace, field, value);
            renderFragment("afterItemsTable");

            //Then render a new item formulary
            renderFragment("beforeNewItemForm");
            renderNewItemForm(form, field, currentNamespace, fieldName, renderMode);
            renderFragment("afterNewItemForm");
        }
        renderFragment("outputEnd");
    }

    protected void previewItemInPosition(Form parentForm, String currentNamespace, int position, Field field, Object value) {
        if (value != null) {
            if (value.getClass().isArray()) {
                CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());
                Form formToPreview = fieldHandler.getPreviewDataForm(field, currentNamespace);
                Map valueToPreview = (Map) Array.get(value, position);
                if (formToPreview != null) {
                    setAttribute("valueToPreview", valueToPreview);
                    setAttribute("form", formToPreview);
                    setAttribute("uid", getFormManager().getUniqueIdentifier(parentForm, currentNamespace, field, null));
                    setAttribute("index", position);
                    setAttribute("parentFormId", parentForm.getId());
                    setAttribute("namespace", currentNamespace);
                    String namespace = currentNamespace + FormProcessor.NAMESPACE_SEPARATOR + parentForm.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
                    setAttribute("namespace", namespace);
                    setAttribute("parentNamespace", currentNamespace);
                    setAttribute("field", field.getFieldName());

                    // Override the field's own disabled and readonly values with the ones coming from a parent formatter
                    // that contains it if they're set to true.
                    if (isReadonly) setAttribute("readonly", isReadonly);

                    //String rowNamespace = currentNamespace +
                    //        FormProcessor.NAMESPACE_SEPARATOR + parentForm.getId() +
                    //        FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName() + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + position;

                    //setAttribute("namespace", rowNamespace);
                    //setAttribute("parentNamespace", namespace);

                    //getFormProcessor().read(formToPreview, rowNamespace, valueToPreview);

                    renderFragment("previewItem");
                } else {
                    renderFragment("noShowDataForm");
                }
            }
        }
    }

    protected void editItemInPosition(Form parentForm, String currentNamespace, int position, Field field, Object value, String fieldName) {
        if (value != null) {
            if (value.getClass().isArray()) {
                CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());
                Form formToEdit = fieldHandler.getEditForm(field, currentNamespace);
                Map valueToEdit = (Map) Array.get(value, position);
                if (formToEdit != null) {
                    setAttribute("valueToEdit", valueToEdit);
                    setAttribute("name", fieldName);
                    setAttribute("form", formToEdit);
                    setAttribute("uid", getFormManager().getUniqueIdentifier(parentForm, currentNamespace, field, null));
                    setAttribute("index", position);
                    setAttribute("parentFormId", parentForm.getId());
                    String namespace = currentNamespace + FormProcessor.NAMESPACE_SEPARATOR + parentForm.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
                    setAttribute("namespace", namespace);
                    setAttribute("parentNamespace", currentNamespace);
                    setAttribute("field", field.getFieldName());
                    // Override the field's own disabled and readonly values with the ones coming from a parent formatter
                    // that contains it if they're set to true.
                    if (isReadonly) setAttribute("readonly", isReadonly);

                    String rowNamespace = namespace + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + position;

                    getFormProcessor().clear(formToEdit,rowNamespace);
                    getFormProcessor().read(formToEdit, rowNamespace, valueToEdit);

                    // setAttribute("namespace", rowNamespace);
                    //setAttribute("parentNamespace", namespace);
                    //getFormProcessor().read(formToEdit, rowNamespace, valueToEdit);

                    renderFragment("editItem");
                }
            }
        }
    }

    protected void renderNewItemForm(Form form, Field field, String currentNamespace, String fieldName, String renderMode) {
        CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());
        Form enterDataForm = fieldHandler.getCreateForm(field, currentNamespace);
        boolean disallowCreateNew = Boolean.TRUE.equals(field.getHideCreateItem());
        if (enterDataForm != null && !disallowCreateNew) {
            setAttribute("form", enterDataForm);
            String namespace = currentNamespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
            setAttribute("namespace", namespace + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + "create");
            String uid = getFormManager().getUniqueIdentifier(form, currentNamespace, field, fieldName);
            if (uid.startsWith(FormProcessor.NAMESPACE_SEPARATOR)) uid = uid.substring(1);
            setAttribute("uid", uid);
            setAttribute("name", fieldName);
            setAttribute("entityName", "");
            FormStatusData status = getFormProcessor().read(form, currentNamespace);
            boolean expanded = false;
            if (Boolean.TRUE.equals(field.getHideContent())) {
                expanded = true;
                setAttribute("noCancelButton", true);
            } else {
                Set expandedFields = (Set) status.getAttributes().get(FormStatusData.EXPANDED_FIELDS);
                expanded = expandedFields != null && expandedFields.contains(field.getFieldName());
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
            renderFragment("outputEnterDataForm");
        }
    }

    protected void renderExistingItemsTable(Form parentForm, String currentNamespace, Field field, Object value) {
        Boolean displayPage = Boolean.valueOf((String) getParameter(PARAM_DISPLAYPAGE));
        CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());

        Form form;

        form = fieldHandler.calculateFieldForm(field, field.getTableSubform(), currentNamespace);

        if (value != null) { //If it is an array, convert it to a List.
            if (value.getClass().isArray()) {
                List l = new ArrayList();
                for (int i = 0; i < Array.getLength(value); i++) {
                    l.add(Array.get(value, i));
                }
                value = l;
            }
        }

        List values = (List) value;
        if (values != null && !values.isEmpty()) {
            setAttribute("className", "skn-table_border");
            String uid = getFormManager().getUniqueIdentifier(parentForm, currentNamespace, field, field.getFieldName());
            setAttribute("uid", uid);
            renderFragment("tableStart");
            boolean modificable = !isReadonly && Boolean.TRUE.equals(field.getUpdateItems());
            boolean deleteable = !isReadonly && Boolean.TRUE.equals(field.getDeleteItems());
            boolean visualizable = Boolean.TRUE.equals(field.getVisualizeItem());

            int colspan = 0;
            if (modificable) colspan++;
            if (deleteable) colspan++;
            if (visualizable) colspan++;

            setAttribute("colspan", colspan);
            renderFragment("headerStart");
            Set<Field> formFields = form.getFormFields();
            List<Field> sortedFields = new ArrayList(formFields);
            Collections.sort(sortedFields, new Field.Comparator());
            for (Field formField : sortedFields) {
                String colLabel = StringEscapeUtils.escapeHtml(fieldI18nResourceObtainer.getFieldLabel(formField));
                setAttribute("colLabel", StringUtils.defaultString(colLabel, formField.getFieldName()));
                String colName = formField.getFieldName();
                setAttribute("colName", colName);
                renderFragment("outputColumnName");
            }
            renderFragment("headerEnd");

            try {
                for (int i = 0; i < values.size(); i++) {
                    setAttribute("index", i);
                    setAttribute("deleteable", deleteable);
                    setAttribute("modificable", modificable);
                    setAttribute("visualizable", visualizable);
                    setAttribute("uid", uid);
                    setAttribute("parentFormId", parentForm.getId());
                    setAttribute("parentNamespace", currentNamespace);
                    setAttribute("field", field.getFieldName());
                    renderFragment("outputSubformActions");

                    Object o = values.get(i);
                    setAttribute("formValues", o);
                    setAttribute("form", form);

                    String rowNamespace = currentNamespace +
                            FormProcessor.NAMESPACE_SEPARATOR + parentForm.getId() +
                            FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName() + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + i;

                    getFormProcessor().read(form, rowNamespace, (Map) o);

                    setAttribute("namespace", rowNamespace);

                    if (Boolean.TRUE.equals(field.getEnableTableEnterData()))
                        setAttribute("renderMode", renderMode);
                    else setAttribute("renderMode", Form.RENDER_MODE_DISPLAY);
                    if (isReadonly) setAttribute("readonly", isReadonly);
                    setAttribute("labelMode", Form.LABEL_MODE_HIDDEN);
                    renderFragment("tableRow");

                }
            } catch (Exception e) {
                log.error("Error: ", e);
            }
            renderFragment("tableEnd");
        }
    }
}
