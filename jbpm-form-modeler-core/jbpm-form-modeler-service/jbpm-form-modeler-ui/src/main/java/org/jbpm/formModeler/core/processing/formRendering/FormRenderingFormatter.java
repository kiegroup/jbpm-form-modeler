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
package org.jbpm.formModeler.core.processing.formRendering;

import org.slf4j.Logger;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.UIDGenerator;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.components.handling.MessagesComponentHandler;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.FormDisplayInfo;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;

/**
 * Renders a form.
 */
@Named("FormRenderingFormatter")
public class FormRenderingFormatter extends Formatter {

    public static final String ATTR_FIELD = "_ddm_currentField";
    public static final String ATTR_NAMESPACE = "_ddm_currentNamespace";
    public static final String ATTR_VALUE = "_ddm_currentValue";
    public static final String ATTR_INPUT_VALUE = "_ddm_currentInputValue";
    public static final String ATTR_NAME = "_ddm_currentName";
    public static final String ATTR_FIELD_IS_WRONG = "_ddm_currentFieldIsWrong";
    public static final String ATTR_FORM_RENDER_MODE = "_ddm_current_renderMode";
    public static final String ATTR_VALUE_IS_DYNAMIC_OBJECT = "_ddm_valueIsObject";
    public static final String ATTR_VALUE_IS_DYNAMIC_OBJECT_ARRAY = "_ddm_valueIsObjectArray";
    public static final String ATTR_DYNAMIC_OBJECT_ID = "_ddm_currentValueIds";
    public static final String ATTR_DYNAMIC_OBJECT_ENTITY_NAME = "_ddm_currentValueEntityName";
    public static final String ATTR_FIELD_IS_READONLY = "_ddm_fieldIsReadonly";
    public static final String FIELD_CONTAINER_STYLE = "padding-top: 3px; padding-right:3px;";

    private Logger log = LoggerFactory.getLogger(FormRenderingFormatter.class);

    @Inject
    private FormErrorMessageBuilder formErrorMessageBuilder;

    @Inject
    protected FieldI18nResourceObtainer fieldI18nResourceObtainer;

    @Inject @Config("/formModeler/defaultFormErrors.jsp")
    private String errorsPage;

    private String[] formModes = new String[]{Form.RENDER_MODE_FORM, Form.RENDER_MODE_WYSIWYG_FORM};
    private String[] displayModes = new String[]{Form.RENDER_MODE_DISPLAY, Form.RENDER_MODE_WYSIWYG_DISPLAY};

    protected transient Form formToPaint;
    protected transient String namespace;
    protected transient String renderMode;
    protected transient Boolean isReadonly = Boolean.FALSE;
    protected transient FormStatusData formStatusData;

    public UIDGenerator getUidGenerator() {
        return UIDGenerator.lookup();
    }

    public FormManager getFormManager() {
        return FormCoreServices.lookup().getFormManager();
    }

    public FormProcessor getFormProcessor() {
        return FormProcessingServices.lookup().getFormProcessor();
    }

    public FieldHandlersManager getFieldHandlersManager() {
        return FormProcessingServices.lookup().getFieldHandlersManager();
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        Object formObject = getParameter("form");

        if (formObject != null) formToPaint = (Form) formObject;
        else {
            log.error("Form not found");
            return;
        }

        renderMode = (String) getParameter("renderMode");     //Default is form
        String labelMode = (String) getParameter("labelMode");           //Default is before;
        String reusingStatus = (String) getParameter("reuseStatus"); //Default is true;
        String forceLabelModeParam = (String) getParameter("forceLabelMode"); //Default is false
        String displayModeParam = (String) getParameter("displayMode");
        String subForm = (String) getParameter("isSubForm");
        String multiple = (String) getParameter("isMultiple");

        Boolean readonly = (Boolean) getParameter("isReadonly");

        if (readonly != null) isReadonly = readonly;

        boolean isSubForm = subForm != null && Boolean.valueOf(subForm).booleanValue();
        boolean isMultiple = multiple != null && Boolean.valueOf(multiple).booleanValue();

        namespace = (String) getParameter("namespace");

        if (StringUtils.isEmpty(namespace)) {
            log.warn("Empty namespace is no longer permitted. Will use a default namespace value, for backwards compatibility", new Exception());
            namespace = FormProcessor.DEFAULT_NAMESPACE;
        } else if (!Character.isJavaIdentifierStart(namespace.charAt(0))) {
            log.warn("Namespace "+namespace+" starts with an illegal character. It may cause unexpected behaviour of form under IE.");
        }
        Object formValues = getParameter("formValues");
        if(formValues!=null){
            getFormProcessor().clear(formToPaint,namespace);
            getFormProcessor().read(formToPaint,namespace,(Map)formValues);
        }
        // Default render mode is FORM
        renderMode = renderMode == null ? Form.RENDER_MODE_FORM : renderMode;

        //Default label mode depends on render mode
        labelMode = labelMode == null ? Form.LABEL_MODE_BEFORE : labelMode;
        //if (Form.RENDER_MODE_DISPLAY.equals(renderMode)) {
        //    labelMode = Form.LABEL_MODE_HIDDEN;
        //}

        boolean reuseStatus = reusingStatus == null || Boolean.valueOf(reusingStatus).booleanValue();
        boolean forceLabelMode = forceLabelModeParam != null && Boolean.valueOf(forceLabelModeParam).booleanValue();
        namespace = namespace == null ? "" : namespace;

        try {
            if (log.isDebugEnabled()) {
                log.debug("Printing form " + formToPaint.getId() + ". Mode: " + renderMode + ".");
            }
            String formLabelMode = formToPaint.getLabelMode();
            if (formLabelMode != null && !"".equals(formLabelMode) && !Form.LABEL_MODE_UNDEFINED.equals(formLabelMode)) {
                if (!forceLabelMode)
                    labelMode = formLabelMode;
            }

            if(formValues!=null){
                getFormProcessor().clear(formToPaint,namespace);
                formStatusData = getFormProcessor().read(formToPaint,namespace,(Map)formValues);
            } else{
                formStatusData = getFormProcessor().read(formToPaint, namespace);
            }

            String displayMode = formToPaint.getDisplayMode();
            if (displayModeParam != null)
                displayMode = displayModeParam;
            FormDisplayInfo displayInfo = null;
            if (displayMode != null) {
                for (Iterator it = formToPaint.getFormDisplayInfos().iterator(); it.hasNext();) {
                    FormDisplayInfo info = (FormDisplayInfo) it.next();
                    if (info.getDisplayMode().equals(displayMode)) {
                        displayInfo = info;
                        break;
                    }
                }
            }

            if (log.isDebugEnabled())
                log.debug("About to display form " + formToPaint.getId() + " in namespace " + namespace);
            display(formToPaint, namespace, displayMode, displayInfo, renderMode, labelMode, isSubForm, isMultiple);

        } catch (Exception e) {
            log.error("Error:", e);
            throw new FormatterException("Error", e);
        }
    }

    protected void setFormFieldErrors(String namespace, Form form) {
        MessagesComponentHandler messagesComponentHandler = MessagesComponentHandler.lookup();
        if (namespace != null && form != null) {
            try {
                messagesComponentHandler.getErrorsToDisplay().addAll(formErrorMessageBuilder.getWrongFormErrors(namespace, form));
            } catch (Exception e) {
                log.error("Error getting error messages for object " + form.getId() + ": ", e);
            }
        }
    }

    protected void display(Form form, String namespace, String displayMode, FormDisplayInfo displayInfo, String renderMode, String labelMode, boolean isSubForm, boolean isMultiple) {

        if (!isSubForm || (isSubForm && isMultiple)) {
            setFormFieldErrors(namespace, form);
            includePage(errorsPage);
        }
        if (displayMode == null) {
            defaultDisplay(form, namespace, renderMode, labelMode, Form.DISPLAY_MODE_DEFAULT);
        }
        else {
            if (Form.DISPLAY_MODE_DEFAULT.equals(displayMode)) {
                defaultDisplay(form, namespace, renderMode, labelMode, Form.DISPLAY_MODE_DEFAULT);
            } else if (Form.DISPLAY_MODE_ALIGNED.equals(displayMode)) {
                defaultDisplay(form, namespace, renderMode, labelMode, Form.DISPLAY_MODE_ALIGNED);
            } else if (Form.DISPLAY_MODE_NONE.equals(displayMode)) {
                defaultDisplay(form, namespace, renderMode, labelMode, Form.DISPLAY_MODE_NONE);
            } else if (Form.DISPLAY_MODE_TEMPLATE.equals(displayMode)) {
                templateDisplay(form, namespace, renderMode);
            } else {
                log.error("Unsupported display mode.");
            }
        }
    }

    public void afterRendering(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        super.afterRendering(httpServletRequest, httpServletResponse);

        // If form was used just to show something, clear the form status.
        if (Form.RENDER_MODE_DISPLAY.equals(renderMode) || Form.RENDER_MODE_TEMPLATE_EDIT.equals(renderMode)) {
            if (formToPaint != null) {
                getFormProcessor().clear(formToPaint, namespace);
            }
        }
    }

    protected void templateDisplay(final Form form, final String namespace, final String renderMode) {
        List renderingInstructions = FormProcessingServices.lookup().getFormTemplateHelper().getRenderingInstructions(form.getFormTemplate());
        FormRenderer renderer = new FormRenderer() {
            public void writeToOut(String text) {
                FormRenderingFormatter.this.writeToOut(text);
            }

            public void renderField(String fieldName) {
                Field field = form.getField(fieldName);
                if (field != null) {
                    //setAttribute("field", field.getForm().getFormFields().iterator().next());
                    setAttribute("field", field);
                    FormRenderingFormatter.this.renderFragment("beforeFieldInTemplateMode");
                    FormRenderingFormatter.this.renderField(field, namespace, renderMode);
                    FormRenderingFormatter.this.renderFragment("afterFieldInTemplateMode");
                } else {
                    setAttribute("fieldName", fieldName);
                    FormRenderingFormatter.this.renderFragment("beforeFieldInTemplateMode");
                    FormRenderingFormatter.this.writeToOut(Form.TEMPLATE_FIELD + "{" + fieldName + "}");
                    FormRenderingFormatter.this.renderFragment("afterFieldInTemplateMode");
                }
            }

            public void renderLabel(String fieldName) {
                Field field = form.getField(fieldName);
                if (field != null) {
                    FormRenderingFormatter.this.renderFragment("beforeLabelInTemplateMode");
                    //FormRenderingFormatter.this.renderLabel(
                    //        field.getForm().getFormFields().iterator().next(),
                    //        namespace,
                    //        renderMode
                    //);
                    FormRenderingFormatter.this.renderLabel(
                            field,
                            namespace,
                            renderMode
                    );
                    FormRenderingFormatter.this.renderFragment("afterLabelInTemplateMode");
                } else {
                    FormRenderingFormatter.this.renderFragment("beforeLabelInTemplateMode");
                    FormRenderingFormatter.this.writeToOut(Form.TEMPLATE_LABEL + "{" + fieldName + "}");
                    FormRenderingFormatter.this.renderFragment("afterLabelInTemplateMode");
                }
            }
        };
        for (int i = 0; i < renderingInstructions.size(); i++) {
            TemplateRenderingInstruction instruction = (TemplateRenderingInstruction) renderingInstructions.get(i);
            instruction.doRender(renderer);
        }
        displayFooter(form);
    }

    protected void renderField(Field field, String namespace, String renderMode) {
        beforeRenderField(field, namespace, renderMode);
        boolean fieldHasErrors = formStatusData.getWrongFields().contains(field.getFieldName());
        String renderPage = "";
        FieldHandler fieldHandler = getFieldHandlersManager().getHandler(field.getFieldType());

        if (Arrays.asList(formModes).contains(renderMode)) {
            renderPage = fieldHandler.getPageToIncludeForRendering();
        } else if (Arrays.asList(displayModes).contains(renderMode)) {
            renderPage = fieldHandler.getPageToIncludeForDisplaying();
        }
        if (!"".equals(renderPage)) {
            Boolean fieldIsRequired = field.getFieldRequired();
            boolean fieldRequired = fieldIsRequired != null && fieldIsRequired.booleanValue();
            if (fieldHasErrors) renderFragment("beforeWrongField");
            if (fieldRequired) renderFragment("beforeRequiredField");
            Object value = formStatusData.getCurrentValue(field.getFieldName());
            boolean isStringType = String.class.getName().equals(field.getFieldType().getFieldClass());
            if (value == null && isStringType) {
                Map currentInputValues = formStatusData.getCurrentInputValues();
                String[] values = null;
                if (currentInputValues != null) {
                    values = ((String[]) currentInputValues.get(namespace + FormProcessor.NAMESPACE_SEPARATOR + field.getForm().getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName()));
                }
                value = values != null && values.length > 0 ? values[0] : value;
            }

            setRenderingAttributes(field, namespace, value, formStatusData, fieldHasErrors);
            // If disabled and/or readonly parameters were received from a subformformatter, pass them on to the included
            // fields (only relevant when they're set to true)
            if (isReadonly) setAttribute(ATTR_FIELD_IS_READONLY, isReadonly);
            includePage(renderPage);
            if (fieldRequired) renderFragment("afterRequiredField");
            if (fieldHasErrors) renderFragment("afterWrongField");
        } else {
            if (Form.RENDER_MODE_TEMPLATE_EDIT.equals(renderMode)) {
                writeToOut(Form.TEMPLATE_FIELD + "{" + field.getFieldName() + "}");
            } else
                log.warn("Invalid render mode " + renderMode);
        }
        afterRenderField(field, namespace, renderMode);
    }

    protected void beforeRenderField(Field field, String namespace, String renderMode) {
        String uid = getFormManager().getUniqueIdentifier(field.getForm(), namespace, field, field.getFieldName());
        String fieldTypeCss = field.getFieldType().getCssStyle();
        String fieldCss = field.getCssStyle();
        Object overridenValue = getFormProcessor().getAttribute(field.getForm(), namespace, field.getFieldName() + ".cssStyle");
        String css = fieldTypeCss;
        if (!StringUtils.isEmpty(fieldCss)) {
            css = fieldCss;
        }
        if (overridenValue != null) {
            css = (String) overridenValue;
        }
        css = StringUtils.defaultString(css);
        css = StringUtils.remove(css, ' ');
        String styleToWrite = FIELD_CONTAINER_STYLE;
        StringTokenizer strtk = new StringTokenizer(css, ";");
        while (strtk.hasMoreTokens()) {
            String tk = strtk.nextToken();
            if ("display:none".equals(tk)) {
                styleToWrite = tk;
                break;
            }
        }
        writeToOut("<div style=\"" + styleToWrite + "\" id=\"" + uid + "_container\">");
    }

    protected void afterRenderField(Field field, String namespace, String renderMode) {
        writeToOut("</div>");
    }

    protected void renderLabel(Field field, String namespace, String renderMode) {
        beforeRenderLabel(field, namespace, renderMode);
        String inputId = field.getFieldType().getUniqueIdentifier(
                getUidGenerator().getUniqueIdentifiersPreffix(),
                namespace,
                field.getForm(),
                field,
                field.getFieldName()
        );
        String labelCssStyle = null;
        String labelCssClass = null;
        try {
            labelCssStyle = field.getLabelCSSStyle();
            labelCssClass = field.getLabelCSSClass();
            //Check if label style was overriden by formulas.
            Object style = getFormProcessor().getAttribute(field.getForm(), namespace, field.getFieldName() + ".labelCSSStyle");
            if (style != null)
                labelCssStyle = style.toString();

        } catch (Exception e) {
            log.error("Error: ", e);
        }

        if (Form.RENDER_MODE_TEMPLATE_EDIT.equals(renderMode)) {
            writeToOut(Form.TEMPLATE_LABEL + "{" + field.getFieldName() + "}");
        } else {
            boolean fieldHasErrors = formStatusData.getWrongFields().contains(field.getFieldName());
            String label = fieldI18nResourceObtainer.getFieldLabel(field);
            Boolean fieldIsRequired = field.getFieldRequired();
            boolean fieldRequired = fieldIsRequired != null && fieldIsRequired.booleanValue() && !Form.RENDER_MODE_DISPLAY.equals(fieldIsRequired);

            String labelValue = StringEscapeUtils.escapeHtml(StringUtils.defaultString(label));

            writeToOut("<span id=\"" + inputId + "_label\"");
            writeToOut(" class='dynInputStyle " + StringUtils.defaultString(labelCssClass) + "' ");
            if (labelCssStyle != null) writeToOut(" style='" + labelCssStyle + "' ");
            writeToOut(" >");

            if (fieldHasErrors) writeToOut("<span class=\"skn-error\">");
            if (!StringUtils.isEmpty(inputId) && !StringUtils.isEmpty(labelValue) && !Form.RENDER_MODE_DISPLAY.equals(renderMode))
                writeToOut("<label for=\"" + StringEscapeUtils.escapeHtml(inputId) + "\">");
            if (fieldRequired) writeToOut("*");
            writeToOut(labelValue);
            if (!StringUtils.isEmpty(inputId) && !StringUtils.isEmpty(labelValue) && !Form.RENDER_MODE_DISPLAY.equals(renderMode))
                writeToOut("</label>");
            if (fieldRequired) renderFragment("afterRequiredLabel");
            if (fieldHasErrors) writeToOut("</span>");

            writeToOut("</span>");

        }
        afterRenderLabel(field, namespace, renderMode);
    }

    protected void beforeRenderLabel(Field field, String namespace, String renderMode) {
        String uid = getFormManager().getUniqueIdentifier(field.getForm(), namespace, field, field.getFieldName());
        String fieldCss = field.getLabelCSSStyle();
        Object overridenValue = getFormProcessor().getAttribute(field.getForm(), namespace, field.getFieldName() + ".labelCSSStyle");
        String css = fieldCss;
        if (overridenValue != null) {
            css = (String) overridenValue;
        }
        css = StringUtils.defaultString(css);
        css = StringUtils.remove(css, ' ');
        String styleToWrite = FIELD_CONTAINER_STYLE;
        StringTokenizer strtk = new StringTokenizer(css, ";");
        while (strtk.hasMoreTokens()) {
            String tk = strtk.nextToken();
            if ("display:none".equals(tk)) {
                styleToWrite = tk;
                break;
            }
        }
        writeToOut("<div style=\"" + styleToWrite + "\" id=\"" + uid + "_label_container\">");
    }

    protected void afterRenderLabel(Field field, String namespace, String renderMode) {
        writeToOut("</div>");
    }

    /**
     * Default display. One field after each other
     *
     * @param form
     * @param renderMode
     */
    protected void defaultDisplay(Form form, String namespace, String renderMode, String labelMode, String mode) {
        Set<Field> fields = form.getFormFields();
        List<Field> sortedFields = new ArrayList(fields);
        Collections.sort(sortedFields, new Field.Comparator());
        FormStatusData formStatusData = getFormProcessor().read(form, namespace);

        setAttribute("width", deduceWidthForForm(form, renderMode, labelMode, mode));
        renderFragment("outputStart");
        renderFragment("formHeader");
        /*Calculate colspans*/
        List colspans = new ArrayList();
        List fieldGroups = new ArrayList();
        fieldGroups.add(new ArrayList());
        for (Field field : sortedFields) {
            List currentList = (List) fieldGroups.get(fieldGroups.size() - 1);
            if (!Boolean.TRUE.equals(field.getGroupWithPrevious())) {
                fieldGroups.add(currentList = new ArrayList());
            }
            currentList.add(field);
        }
        for (int i = 0; i < fieldGroups.size(); i++) {
            List list = (List) fieldGroups.get(i);
            if (!list.isEmpty())
                colspans.add(new BigInteger(String.valueOf(list.size())));
        }

        BigInteger mcm = calculateMCM(colspans);
        BigInteger max = calculateMax(colspans);

        /*Render fields with colspans*/
        List groupList = new ArrayList();

        boolean first = true;

        for (int i = 0; i < sortedFields.size(); i++) {
            Field field = sortedFields.get(i);
            groupList.add(field);
            if (i < sortedFields.size() - 1) {
                Field nextField = sortedFields.get(i + 1);
                if (nextField.getGroupWithPrevious() != null && nextField.getGroupWithPrevious().booleanValue()) {
                    continue;
                }
            }
            if (i > 0 && Form.DISPLAY_MODE_NONE.equals(mode)) {
                renderFragment("outputEnd");
                setAttribute("width", deduceWidthForForm(form, renderMode, labelMode, mode));
                renderFragment("outputStart");
            }
            defaultDisplayGroup(form, groupList, mcm.intValue(), max.intValue(), renderMode, labelMode, formStatusData, mode, namespace, i, first);
            groupList.clear();
            first = false;
        }

        displayFooter(form);
        renderFragment("outputEnd");
    }


    protected void displayFooter(Form form) {
        String displayMode = form.getDisplayMode();
        if (Form.RENDER_MODE_FORM.equals(renderMode)) {
            String formRefresherFieldName = namespace + FormProcessor.NAMESPACE_SEPARATOR + form.getId() + FormProcessor.NAMESPACE_SEPARATOR + ":initialFormRefresher";
            setAttribute("name", formRefresherFieldName);
            setAttribute("uid", getUidGenerator().getUniqueIdentifiersPreffix() + FormProcessor.NAMESPACE_SEPARATOR + formRefresherFieldName);
            if (Form.DISPLAY_MODE_TEMPLATE.equals(displayMode)) {
                includePage("/formModeler/defaultFormFooter.jsp");
            } else {
                renderFragment("formFooter");
            }
        }
    }

    /**
     * Deduce width for a form.
     * @return Deduced width for a form.
     */
    protected String deduceWidthForForm(Form form, String renderMode, String labelMode, String mode) {
        if (Form.DISPLAY_MODE_TEMPLATE.equals(mode))
            return null;  //In these modes, it doesn't matter
        if (Form.RENDER_MODE_DISPLAY.equals(renderMode)) { //Showing data
            if (Form.DISPLAY_MODE_NONE.equals(mode)) {
                return "";
            } else {
                return "100%";
            }
        } else { //Entering data
            return "1%";
        }
    }

    protected void defaultDisplayGroup(Form form, List groupMembers, int maxCols, int maxMembers, String renderMode, String labelMode, FormStatusData formStatusData, String mode, String namespace, int position, boolean first) {
        int fieldColspan = maxCols / groupMembers.size();
        int fieldWidth = (100 * fieldColspan) / maxCols;
        if (Form.DISPLAY_MODE_ALIGNED.equals(mode)) {
            fieldColspan = maxCols / maxMembers;
        }
        if (Form.DISPLAY_MODE_NONE.equals(mode)) {
            fieldColspan = 1;
        }
        setAttribute("groupPosition", position);
        setAttribute("field", groupMembers.get(0));
        setAttribute("colspan", maxCols);
        setAttribute("isFirst", first);
        renderFragment("groupStart");
        for (int i = 0; i < groupMembers.size(); i++) {
            if (i == groupMembers.size() - 1 && Form.DISPLAY_MODE_ALIGNED.equals(mode)) {
                fieldColspan = maxCols - i * maxCols / maxMembers;
                fieldWidth = (100 * fieldColspan) / maxCols;
            }
            renderInputElement((Field) groupMembers.get(i), fieldColspan, fieldWidth, namespace, renderMode, labelMode, i);
        }
        setAttribute("field", groupMembers.get(groupMembers.size()-1));
        setAttribute("colspan", maxCols);
        renderFragment("groupEnd");
    }

    protected void renderInputElement(Field field, int fieldColspan, int fieldWidth, String namespace, String renderMode, String labelMode, int index) {
        setAttribute("field", field);
        setAttribute("colspan", fieldColspan);
        setAttribute("width", fieldWidth);
        setAttribute("index", index);
        boolean labelInSameLine = Form.LABEL_MODE_LEFT.equals(labelMode) || Form.LABEL_MODE_RIGHT.equals(labelMode);
        renderFragment("beforeInputElement");

        // TODO: improve alignment for checkbox label
        if((field.getFieldType().getCode().equals("CheckBox") || field.getFieldType().getCode().equals("CheckBoxPrimitiveBoolean")) && !Form.LABEL_MODE_LEFT.equals(labelMode)){
            labelMode =Form.LABEL_MODE_AFTER;
            labelInSameLine=true;
        }

        if (Form.LABEL_MODE_BEFORE.equals(labelMode) || Form.LABEL_MODE_LEFT.equals(labelMode)) {
            setAttribute("colspan", fieldColspan);
            setAttribute("width", fieldWidth);

            setBindingAttributes(field);

            renderFragment("beforeLabel");

            renderLabel(field, namespace, renderMode);
            renderFragment("afterLabel");
            if (!labelInSameLine)
                renderFragment("lineBetweenLabelAndField");
        }
        setAttribute("field", field);
        setAttribute("colspan", fieldColspan);
        setAttribute("width", fieldWidth);
        renderFragment("beforeField");
        renderField(field, namespace, renderMode);
        setAttribute("field", field);
        renderFragment("afterField");

        if (Form.LABEL_MODE_AFTER.equals(labelMode) || Form.LABEL_MODE_RIGHT.equals(labelMode)) {
            if (!labelInSameLine)
                renderFragment("lineBetweenLabelAndField");
            setAttribute("colspan", fieldColspan);
            setAttribute("width", fieldWidth);

            setBindingAttributes(field);

            renderFragment("beforeLabel");
            renderLabel(field, namespace, renderMode);
            renderFragment("afterLabel");
        }
        setAttribute("field", field);
        renderFragment("afterInputElement");
    }

    private void setBindingAttributes(Field field) {

        DataHolder inputHolder = formToPaint.getDataHolderFromInputExpression(field.getInputBinding());
        boolean hasInputBinding = !StringUtils.isEmpty(field.getInputBinding());

        String color = "#444444";

        setAttribute("hasInputBinding", hasInputBinding);
        if (hasInputBinding) {
            setAttribute("inputBindingColor", inputHolder != null ? inputHolder.getRenderColor() : color);
        }

        DataHolder outputHolder = formToPaint.getDataHolderFromOutputExpression(field.getOutputBinding());
        boolean hasOutputBinding = !StringUtils.isEmpty(field.getOutputBinding());

        if (hasOutputBinding && hasInputBinding) {
            if (inputHolder != null && inputHolder.equals(outputHolder)) hasOutputBinding = false;
            else if (inputHolder == null && outputHolder == null) hasOutputBinding = false;
        }

        setAttribute("hasOutputBinding", hasOutputBinding);
        if (hasOutputBinding) {
            setAttribute("outputBindingColor", outputHolder != null ? outputHolder.getRenderColor() : color);
        }
    }

    protected BigInteger calculateMCM(List colspans) {
        if (colspans == null || colspans.isEmpty()) {
            return new BigInteger("1");
        } else if (colspans.size() == 1) {
            return (BigInteger) colspans.get(0);
        } else if (colspans.size() == 2) {
            BigInteger b1 = (BigInteger) colspans.get(0);
            BigInteger b2 = (BigInteger) colspans.get(1);
            return b1.multiply(b2).divide(b1.gcd(b2));
        } else { //Size > 2
            int halfLength = colspans.size() / 2;
            List firstHalf = colspans.subList(0, halfLength);
            List secondHalf = colspans.subList(halfLength, colspans.size());
            BigInteger b1 = calculateMCM(firstHalf);
            BigInteger b2 = calculateMCM(secondHalf);
            return b1.multiply(b2).divide(b1.gcd(b2));
        }
    }

    protected BigInteger calculateMax(List colspans) {
        BigInteger max = new BigInteger("0");
        for (int i = 0; i < colspans.size(); i++) {
            BigInteger number = (BigInteger) colspans.get(i);
            max = max.compareTo(number) < 0 ? number : max;
        }
        return max;
    }

    protected void setRenderingAttributes(Field field, String namespace, Object value, FormStatusData formStatusData, boolean isWrongField) {
        String fieldName = namespace + FormProcessor.NAMESPACE_SEPARATOR + field.getForm().getId() + FormProcessor.NAMESPACE_SEPARATOR + field.getFieldName();
        setAttribute(ATTR_FIELD, field);
        setAttribute(ATTR_VALUE, value);
        setAttribute(ATTR_INPUT_VALUE, formStatusData.getCurrentInputValue(fieldName));
        setAttribute(ATTR_FIELD_IS_WRONG, isWrongField);
        setAttribute(ATTR_NAMESPACE, namespace);
        setAttribute(ATTR_NAME, fieldName);
        setAttribute(ATTR_VALUE_IS_DYNAMIC_OBJECT, false);
        setAttribute(ATTR_VALUE_IS_DYNAMIC_OBJECT_ARRAY, false);
        setAttribute(ATTR_DYNAMIC_OBJECT_ID, null);
        setAttribute(ATTR_DYNAMIC_OBJECT_ENTITY_NAME, null);
        setAttribute(ATTR_FORM_RENDER_MODE, renderMode);
    }
}