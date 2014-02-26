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
package org.jbpm.formModeler.components.editor;

import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.config.DataHolderManager;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.core.processing.*;
import org.jbpm.formModeler.core.wrappers.HTMLi18n;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

import org.slf4j.Logger;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Component to edit forms in a WYSIWYG way
 */
@SessionScoped
@Named("wysiwygfe")
public class WysiwygFormEditor extends BaseUIComponent {

    @Inject
    private DataHolderManager dataHolderManager;

    public static WysiwygFormEditor lookup() {
        return (WysiwygFormEditor) CDIBeanLocator.getBeanByType(WysiwygFormEditor.class);
    }

    private Logger log = LoggerFactory.getLogger(WysiwygFormEditor.class);

    @Inject
    private FormTemplateEditor formTemplateEditor;

    @Inject
    private FormEditorContextManager formEditorContextManager;

    @Inject
    @Config("/formModeler/components/WysiwygFormEdit/component.jsp")
    private String componentIncludeJSP;

    @Inject
    @Config("/formModeler/components/WysiwygFormEdit/show.jsp")
    private String baseComponentJSP;

    private static final String PARAM_CTX_UID = "ctxUID";

    public static final String TOP_FIELD_MODIFIER = "topModifier";
    public static final String LEFT_FIELD_MODIFIER = "leftModifier";
    public static final String RIGHT_FIELD_MODIFIER = "rightModifier";
    public static final String BOTTOM_FIELD_MODIFIER = "bottomModifier";

    public static final String EDITION_OPTION_FIELDTYPES = "fieldTypes";
    public static final String EDITION_OPTION_FORM_PROPERTIES = "formProperties";
    public static final String EDITION_OPTION_BINDINGS_FIELDS = "dataHoldersFields";
    public static final String EDITION_OPTION_BINDINGS_SOURCES = "dataHoldersSources";

    public static final String EDITION_OPTION_IMG_FIELDTYPES = "general/AddFieldsByType.png";
    public static final String EDITION_OPTION_IMG_FORM_PROPERTIES = "general/FormProperties.png";
    public static final String EDITION_OPTION_IMG_BINDINGS_FIELDS = "general/FieldsBindings.png";
    public static final String EDITION_OPTION_IMG_BINDINGS_SOURCES = "general/Bindings.png";

    public static final String EDITION_OPTION_VIS_MODE_FIELDTYPES = "shared";
    public static final String EDITION_OPTION_VIS_MODE_FORM_PROPERTIES = "shared";
    public static final String EDITION_OPTION_VIS_MODE_BINDINGS_FIELDS = "shared";
    public static final String EDITION_OPTION_VIS_MODE_BINDINGS_SOURCE = "full";

    public static final String ACTION_TO_DO = "actionToDo";
    public static final String ACTION_CHANGE_FIELD_TYPE = "changeFieldType";
    public static final String ACTION_SAVE_FIELD_PROPERTIES = "saveFieldProperties";
    public static final String ACTION_CANCEL_FIELD_EDITION = "cancelFieldEdition";

    public static final String ACTION_REMOVE_DATA_HOLDER = "removeDataHolder";
    public static final String ACTION_ADD_DATA_HOLDER = "addDataHolder";
    public static final String ACTION_ADD_DATA_HOLDER_FIELDS = "addDataHolderFields";

    public static final String PARAMETER_HOLDER_ID = "holderId";
    public static final String PARAMETER_HOLDER_INPUT_ID = "holderInputId";
    public static final String PARAMETER_HOLDER_OUTPUT_ID = "holderOutputId";
    public static final String PARAMETER_HOLDER_TYPE = "holderType";
    public static final String PARAMETER_HOLDER_COMBO_VALUE = "holderComboValue";
    public static final String PARAMETER_HOLDER_INPUT_VALUE = "holderInputValue";
    public static final String PARAMETER_FIELD_NAME = "fieldName";
    public static final String PARAMETER_FIELD_CLASS = "className";
    public static final String PARAMETER_HOLDER_RENDERCOLOR = "holderRenderColor";

    private FormEditorContext editionContext;

    public FormEditorContext getEditionContext() {
        return editionContext;
    }

    public String getRenderMode() {
        return getEditionContext().getRenderMode();
    }

    public void setRenderMode(String renderMode) {
        getEditionContext().setRenderMode(renderMode);
    }

    public FormManager getFormManager() {
        return FormCoreServices.lookup().getFormManager();
    }

    public FormProcessor getFormProcessor() {
        return FormProcessingServices.lookup().getFormProcessor();
    }

    public String getBaseComponentJSP() {
        return baseComponentJSP;
    }

    public String getBeanJSP() {
        return componentIncludeJSP;
    }

    public String getLastDataHolderUsedId() {
        return editionContext.getLastDataHolderUsedId();
    }

    public void setLastDataHolderUsedId(String lastDataHolderUsedId) {
        getEditionContext().setLastDataHolderUsedId(lastDataHolderUsedId);
    }

    public Boolean getDisplayBindings() {
        return getEditionContext().getDisplayBindings();
    }

    public void setDisplayBindings(Boolean displayBindings) {
        getEditionContext().setDisplayBindings( displayBindings);
    }

    public Boolean getDisplayGrid() {
        return getEditionContext().getDisplayGrid();
    }

    public void setDisplayGrid(Boolean displayGrid) {
        getEditionContext().setDisplayGrid(displayGrid);
    }

    public Form getCurrentForm() {
        return getEditionContext().getForm();
    }

    public String getNamespace() {
        return getEditionContext().getUID();
    }

    public FormTemplateEditor getFormTemplateEditor() {
        return formTemplateEditor;
    }

    public void setFormTemplateEditor(FormTemplateEditor formTemplateEditor) {
        this.formTemplateEditor = formTemplateEditor;
    }

    public FormEditorContext getCurrentEditionContext() {
        return getEditionContext();
    }

    public String getCurrentEditionOption() {
        if(getEditionContext().getCurrentEditionOption() == null){
            if(getCurrentForm().getFormFields() !=null && getCurrentForm().getFormFields().size()>0){
                setCurrentEditionOption(EDITION_OPTION_FIELDTYPES);
            }else{
                setCurrentEditionOption(EDITION_OPTION_BINDINGS_SOURCES);
            }
        }
        return getEditionContext().getCurrentEditionOption();
    }

    public void setCurrentEditionOption(String currentEditionOption) {
        getEditionContext().setCurrentEditionOption(currentEditionOption);
    }

    public int getCurrentEditFieldPosition() {
        return getEditionContext().getCurrentEditFieldPosition();
    }

    public void setCurrentEditFieldPosition(int currentEditFieldPosition) {
        getEditionContext().setCurrentEditFieldPosition(currentEditFieldPosition);
        Field field = getCurrentEditField();
        if (field != null) setFieldTypeToView(field.getFieldType().getCode());
    }

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public FieldTypeManager getFieldTypesManager() {
        return FormCoreServices.lookup().getFieldTypeManager();
    }

    public BindingManager getBindingManager() {
        return FormCoreServices.lookup().getBindingManager();
    }

    public String getFieldTypeToView() {
        return getEditionContext().getFieldTypeToView();
    }

    public void setFieldTypeToView(String fieldTypeToView) {
        getEditionContext().setFieldTypeToView(fieldTypeToView);
    }

    public int getLastMovedFieldPosition() {
        return getEditionContext().getLastMovedFieldPosition();
    }

    public void setLastMovedFieldPosition(int lastMovedFieldPosition) {
        getEditionContext().setLastMovedFieldPosition(lastMovedFieldPosition);
    }

    public CommandResponse handle(CommandRequest commandRequest, String string) throws Exception {
        return super.handle(commandRequest, string);
    }

    public Field getCurrentEditField() {
        return getFieldInPosition(getCurrentEditFieldPosition());
    }

    protected Field getFieldInPosition(int position) {
        if (position != -1) {
            Form form = getCurrentForm();
            if (form != null) {
                for (Field field : form.getFormFields()) {
                    if (field.getPosition() == position) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void doStart(CommandRequest commandRequest) {

        String ctxUID = commandRequest.getRequestObject().getParameter(PARAM_CTX_UID);

        if (!StringUtils.isEmpty(ctxUID)) {
            setEditionContext(ctxUID);
            setLastMovedFieldPosition(-1);
            setCurrentEditFieldPosition(-1);
            setFieldTypeToView(null);
        }
    }

    protected void setEditionContext(String ctxUID) {
        editionContext = formEditorContextManager.getFormEditorContext(ctxUID);
        getExtraActionParams().put(PARAM_CTX_UID, ctxUID);
    }

    protected void checkEditionContext(CommandRequest request) {
        String ctxUID = request.getRequestObject().getParameter(PARAM_CTX_UID);

        if (StringUtils.isEmpty(ctxUID)) throw new IllegalArgumentException("NULL edition context id");

        if (editionContext == null || !editionContext.getUID().equals(ctxUID)) {
            setEditionContext(ctxUID);
        }
    }

    public void actionDelete(CommandRequest request) throws Exception {
        checkEditionContext(request);

        Long pos = Long.decode(request.getParameter("position"));
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldNextToDeleted = getFieldInPosition(pos.intValue() + 1);
            if (fieldNextToDeleted != null) {
                Field fieldToDelete = getFieldInPosition(pos.intValue());
                if (!Boolean.TRUE.equals(fieldToDelete.getGroupWithPrevious())) {
                    fieldNextToDeleted.setGroupWithPrevious(fieldToDelete.getGroupWithPrevious());
                }
            }
            getFormManager().deleteField(form, pos.intValue());
            if (getCurrentEditFieldPosition() == pos.intValue()) setCurrentEditFieldPosition(-1);
            else if (getCurrentEditFieldPosition() > pos.intValue()) setCurrentEditFieldPosition( getCurrentEditFieldPosition() - 1);
        }
    }

    public void actionStartEdit(CommandRequest request) throws Exception {
        checkEditionContext(request);
        Integer pos = Integer.decode(request.getParameter("position"));
        setCurrentEditFieldPosition(pos.intValue());

        Field editField = getCurrentEditField();

        Form formToEdit = getFormForFieldEdition(editField);
        if (formToEdit != null) {
            String editNamespace = getFieldEditionNamespace(editField);
            getFormProcessor().clear(formToEdit, editNamespace);
            getFormProcessor().read(formToEdit, editNamespace, editField.asMap());
        }
        getEditionContext().setOriginalFieldType(editField.getFieldType());
    }

    protected String getFieldEditionNamespace(Field field) {
        if (field == null) return "";

        String editionNamespace = formEditorContextManager.generateFieldEditionNamespace(editionContext.getUID(), field);

        return editionNamespace;
    }

    public String getCurrentFieldEditionNamespace() {
        return getFieldEditionNamespace(getCurrentEditField());
    }

    protected void addFieldToForm(Form form, String typeId) throws Exception {
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            FieldType fType = getFieldTypesManager().getTypeByCode(typeId);
            getFormManager().addFieldToForm(form, fType);
        }
    }

    public void actionAddFieldToForm(CommandRequest request) throws Exception {
        checkEditionContext(request);
        final String fieldType = request.getParameter("fieldType");
        Form form = getCurrentForm();
        addFieldToForm(form, fieldType);
    }

    public void actionAddDecoratorToForm(CommandRequest request) throws Exception {
        checkEditionContext(request);
        final String fieldType = request.getParameter("fieldType");
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            final String name = generateDecoratorName(form);
            I18nSet label = new I18nSet();
            String lang = LocaleManager.lookup().getDefaultLang();
            FieldType fType = getFieldTypesManager().getTypeByCode(fieldType);
            Field formField = getFormManager().addFieldToForm(form, name, fType, label);

            if ("HTMLLabel".equals(fType.getCode())) {
                HTMLi18n val = new HTMLi18n();
                val.setValue(lang, "HTML");
                formField.setHtmlContent(val);
            }
        }
    }

    protected synchronized String generateDecoratorName(Form form) {
        TreeSet names = new TreeSet();
        for (Field pff : form.getFormFields()) {
            names.add(pff.getFieldName());
        }

        String name = ":decorator_0";
        for (int i = 1; names.contains(name); name = ":decorator_" + (i++))
            ;
        return name;
    }

    public Form getFormForFieldEdition(Field field) throws Exception {
        if (getFieldTypeToView() != null) {
            return getFormManager().getFormForFieldEdition(getFieldTypesManager().getTypeByCode(getFieldTypeToView()));
        }
        return getFormManager().getFormForFieldEdition(field.getFieldType());
    }

    public void actionSaveFieldProperties(final CommandRequest request) throws Exception {
        checkEditionContext(request);

        String action = request.getRequestObject().getParameter(ACTION_TO_DO);

        Field editField = getCurrentEditField();
        Map parameterMap = request.getRequestObject().getParameterMap();
        Map filesMap = request.getFilesByParamName();

        if (editField == null) {
            log.error("Cannot update unexistant field.");
        } else {
            String editNamespace = getFieldEditionNamespace(editField);
            if (ACTION_CANCEL_FIELD_EDITION.equals(action)) {
                Form editForm = getFormForFieldEdition(editField);
                editField.setFieldType(getEditionContext().getOriginalFieldType());
                getFormProcessor().clear(editForm, editNamespace);
                getEditionContext().setOriginalFieldType(null);
                setCurrentEditFieldPosition(-1);
            } else {
                //Use custom edit form
                Form editForm = getFormForFieldEdition(editField);
                getFormProcessor().setValues(editForm, editNamespace, parameterMap, filesMap);
                FormStatusData data = getFormProcessor().read(editForm, editNamespace);

                if (ACTION_CHANGE_FIELD_TYPE.equals(action)) {
                    setFieldTypeToView( ((String[]) parameterMap.get("fieldType"))[0]);
                    editField.setFieldType(getFieldTypesManager().getTypeByCode(getFieldTypeToView(), editionContext.getOriginalFieldType().getFieldClass()));
                    Form formToEdit = getFormForFieldEdition(editField);
                    if (formToEdit != null) {
                        getFormProcessor().clear(formToEdit, editNamespace);
                        getFormProcessor().read(formToEdit, editNamespace, data.getCurrentValues());
                    }

                } else {

                    if (data.isValid()) {

                        /*
                        * TODO: fix that
                        */
                        Set names = editField.getPropertyNames();

                        for (Iterator it = data.getCurrentValues().keySet().iterator(); it.hasNext(); ) {
                            String propertyName = (String) it.next();
                            if (names.contains(propertyName)) {
                                Object value = data.getCurrentValue(propertyName);
                                try {
                                    if("fieldName".equals(propertyName)){
                                        if(!editField.getFieldName().equals((String)value)){
                                            if(getCurrentForm().getField((String)value) !=null) return;
                                        }
                                    }
                                    getBindingManager().setPropertyValue(editField, propertyName, value);
                                } catch (Exception e) {
                                    log.error("Error setting property '" + propertyName + "' on field " + editField.getFieldName(), e);
                                }

                            }
                        }

                        setCurrentEditFieldPosition(-1);
                        getFormProcessor().clear(editForm, editNamespace);
                        getFormProcessor().read(editForm, editNamespace, data.getCurrentValues());

                    }
                }
            }
        }
    }

    public synchronized void actionMoveField(CommandRequest request) throws Exception {
        checkEditionContext(request);

        String selectedField = request.getRequestObject().getParameter("selectedField");
        String newPosition = request.getRequestObject().getParameter("newPosition");
        String modifier = request.getRequestObject().getParameter("modifier");
        String promote = request.getRequestObject().getParameter("promote");

        if (StringUtils.isEmpty(selectedField) || StringUtils.isEmpty(newPosition) || StringUtils.isEmpty(modifier) || StringUtils.isEmpty(promote))
            return;

        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {

            int origPosition = Integer.parseInt(selectedField);
            int destPosition = Integer.parseInt(newPosition);

            boolean groupWithPrevious = RIGHT_FIELD_MODIFIER.equals(modifier);
            boolean nextGrouped = LEFT_FIELD_MODIFIER.equals(modifier);

            setLastMovedFieldPosition(destPosition);

            if (getCurrentEditFieldPosition() == origPosition) setCurrentEditFieldPosition( getLastMovedFieldPosition());

            if (Boolean.parseBoolean(promote)) {
                getFormManager().promoteField(form, origPosition, destPosition, groupWithPrevious, nextGrouped);
                if (getCurrentEditFieldPosition()< origPosition && destPosition <= getCurrentEditFieldPosition())
                    setCurrentEditFieldPosition(getCurrentEditFieldPosition() +1);
            } else {
                getFormManager().degradeField(form, origPosition, destPosition, groupWithPrevious, nextGrouped);
                if (getCurrentEditFieldPosition()> origPosition && destPosition >= getCurrentEditFieldPosition())
                    setCurrentEditFieldPosition(getCurrentEditFieldPosition()-1);
            }
        }
    }

    public synchronized void actionMoveFirst(CommandRequest request) throws Exception {
        checkEditionContext(request);
        int fieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            getFormManager().moveTop(form, fieldPosition);
            setLastMovedFieldPosition(0);
            if (getCurrentEditFieldPosition() == fieldPosition) setCurrentEditFieldPosition(getLastMovedFieldPosition());
            else if (getCurrentEditFieldPosition()> -1 && fieldPosition > getCurrentEditFieldPosition())
                setCurrentEditFieldPosition(getCurrentEditFieldPosition()+1);
        }
    }

    public synchronized void actionMoveLast(CommandRequest request) throws Exception {
        checkEditionContext(request);
        int fieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            getFormManager().moveBottom(form, fieldPosition);
            setLastMovedFieldPosition( form.getFormFields().size() - 1);
            if (getCurrentEditFieldPosition() == fieldPosition) setCurrentEditFieldPosition(getLastMovedFieldPosition());
            else if (fieldPosition < getCurrentEditFieldPosition()) setCurrentEditFieldPosition(getCurrentEditFieldPosition()-1);
        }

    }

    public synchronized void actionSaveCurrentForm(CommandRequest request) throws Exception {
        checkEditionContext(request);
        saveCurrentForm(request.getRequestObject().getParameterMap());
    }

    public synchronized void actionSwitchRenderMode(CommandRequest request) throws Exception {
        checkEditionContext(request);

        String renderMode = request.getRequestObject().getParameter("renderMode");
        String displayBindings = request.getRequestObject().getParameter("displayBindings");
        String displayGrid = request.getRequestObject().getParameter("displayGrid");

        if ((displayBindings != null) && !Boolean.parseBoolean(displayBindings)) {
            setDisplayBindings(Boolean.FALSE);
        } else {
            setDisplayBindings(Boolean.TRUE);
        }

        if ((displayGrid != null) && !Boolean.parseBoolean(displayGrid)) {
            setDisplayGrid(Boolean.FALSE);
        } else {
            setDisplayGrid(Boolean.TRUE);
        }

        if (Form.RENDER_MODE_WYSIWYG_DISPLAY.equals(renderMode) || Form.RENDER_MODE_WYSIWYG_FORM.equals(renderMode))
            setRenderMode(renderMode);
    }

    public synchronized void actionChangeMainOption(CommandRequest request) throws Exception {
        checkEditionContext(request);

        String option = request.getRequestObject().getParameter("newMainOption");
        setCurrentEditionOption(option);
    }

    public void saveCurrentForm(Map parameterMap) throws Exception {
        Map map = fillFormFromForm(parameterMap);
        String name = (String) map.get("name");
        String displayMode = (String) map.get("displayMode");
        String displayModeAligned = (String) map.get("displayModeAligned");
        String labelMode = (String) map.get("labelMode");
        Long status = (Long) map.get("status");

        Form form = getCurrentForm();
        form.setName(name);

        if(!Form.DISPLAY_MODE_TEMPLATE.equals(displayMode)){
            if(Form.DISPLAY_MODE_ALIGNED.equals(displayModeAligned)) form.setDisplayMode(Form.DISPLAY_MODE_ALIGNED);
            else form.setDisplayMode(Form.DISPLAY_MODE_NONE);
        } else {
            form.setDisplayMode(Form.DISPLAY_MODE_TEMPLATE);
        }

        //form.setDisplayMode(displayMode);
        form.setLabelMode(labelMode);
        form.setStatus(status);

        if(!Form.DISPLAY_MODE_TEMPLATE.equals(displayMode)){
            getFormTemplateEditor().setFormId(null);
        }
        String[] editTemplateParams = (String[]) parameterMap.get("editTemplate");
        if (editTemplateParams != null && editTemplateParams.length > 0 && "true".equals(editTemplateParams[0])) {
            form.setDisplayMode(Form.DISPLAY_MODE_TEMPLATE);
            getFormTemplateEditor().setFormId(form.getId());
            getFormTemplateEditor().setTemplateContent(form.getFormTemplate());
            Long formId = form.getId();
            if (formId != null) getFormTemplateEditor().setFormId(formId);
            getEditionContext().setShowTemplateEdition( true);
        }
    }

    protected Map fillFormFromForm(Map parameterMap) {
        Map m = new HashMap();
        String[] name = (String[]) parameterMap.get("name");
        String[] displayMode = (String[]) parameterMap.get("displayMode");
        String[] displayModeAligned = (String[]) parameterMap.get("displayModeAligned");
        String[] labelMode = (String[]) parameterMap.get("labelMode");

        String[] status = (String[]) parameterMap.get("status");
        String[] copyingFrom = (String[]) parameterMap.get("copyingFrom");

        if (status == null || status.length == 0) {
            status = new String[]{String.valueOf(FormManagerImpl.FORMSTATUS_NORMAL)};
        }

        m.put("name", (name != null && name.length > 0) ? name[0] : null);
        m.put("displayModeAligned", (displayModeAligned != null && displayModeAligned.length > 0) ? displayModeAligned[0] : Form.DISPLAY_MODE_NONE);
        m.put("displayMode", (displayMode != null && displayMode.length > 0) ? displayMode[0] : "default");
        m.put("labelMode", (labelMode != null && labelMode.length > 0) ? labelMode[0] : "undefined");
        m.put("status", Long.decode(status[0]));
        m.put("copyingFrom", (copyingFrom != null && !"".equals(copyingFrom[0].trim())) ? (Long.decode(copyingFrom[0])) : null);
        return m;
    }

    public void actionUnGroupWithPrevious(CommandRequest request) throws Exception {
        checkEditionContext(request);
        groupField(request, false);
    }

    public void actionGroupWithPrevious(CommandRequest request) throws Exception {
        checkEditionContext(request);
        groupField(request, true);
    }

    protected void groupField(CommandRequest request, final boolean groupIt) throws Exception {
        setLastMovedFieldPosition( Integer.decode(request.getParameter("position")).intValue());
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(getLastMovedFieldPosition());
            if (fieldToMove != null) {
                fieldToMove.setGroupWithPrevious(Boolean.valueOf(groupIt));
            } else {
                log.error("Cannot modify unexistant field");
            }
        }
    }

    public synchronized void actionAddFieldFromDataHolder(CommandRequest request) throws Exception {
        checkEditionContext(request);
        addDataHolderFieldToForm(request.getRequestObject().getParameterMap());
    }

    public synchronized void actionFormDataHolders(CommandRequest request) throws Exception {
        checkEditionContext(request);

        String action = request.getRequestObject().getParameter(ACTION_TO_DO);

        if (ACTION_ADD_DATA_HOLDER.equals(action)) {
            addDataHolder(request.getRequestObject());
        } else if (ACTION_REMOVE_DATA_HOLDER.equals(action)) {
            removeDataHolder(request.getRequestObject().getParameterMap());
        } else if (ACTION_ADD_DATA_HOLDER_FIELDS.equals(action)) {
            addAllDataHolderFieldsToForm(request.getRequestObject().getParameterMap());
        }

    }

    public void addDataHolder(HttpServletRequest request) throws Exception {
        String holderType = request.getParameter(PARAMETER_HOLDER_TYPE);
        String holderId = request.getParameter(PARAMETER_HOLDER_ID);
        String holderInputId = request.getParameter(PARAMETER_HOLDER_INPUT_ID);
        String holderOutId = request.getParameter(PARAMETER_HOLDER_OUTPUT_ID);
        String holderRenderColor = request.getParameter(PARAMETER_HOLDER_RENDERCOLOR);
        String holderInputValue = request.getParameter(PARAMETER_HOLDER_INPUT_VALUE);
        String holderComboValue = request.getParameter(PARAMETER_HOLDER_COMBO_VALUE);

        String holderValue = StringUtils.defaultIfEmpty(holderInputValue, holderComboValue);

        DataHolderBuildConfig config = new DataHolderBuildConfig(holderId, holderInputId, holderOutId, holderRenderColor, holderValue);
        config.addAttribute("path", getCurrentEditionContext().getPath());

        DataHolder holder = dataHolderManager.createDataHolderByType(holderType, config);

        getFormManager().addDataHolderToForm(getCurrentForm(), holder);
    }


    public void removeDataHolder(Map parameterMap) throws Exception {
        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);

        String holderId = null;
        if (holderIdArray != null && holderIdArray.length > 0) holderId = holderIdArray[0];

        getFormManager().removeDataHolderFromForm(getCurrentForm(),holderId);
    }

    public void addAllDataHolderFieldsToForm(Map parameterMap) throws Exception {

        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);

        String holderId = null;
        if (holderIdArray != null && holderIdArray.length > 0) holderId = holderIdArray[0];

        if (holderId != null) {
            getFormManager().addAllDataHolderFieldsToForm(getCurrentForm(),holderId);
            setLastDataHolderUsedId(holderId);
        }
    }

    public void addDataHolderFieldToForm(Map parameterMap) throws Exception {

        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);
        String[] fieldNameArray = (String[]) parameterMap.get(PARAMETER_FIELD_NAME);
        String[] fieldClassArray = (String[]) parameterMap.get(PARAMETER_FIELD_CLASS);

        String bindingId = null;
        String fieldName = null;
        String fieldClass = null;

        if (holderIdArray != null && holderIdArray.length > 0) bindingId = holderIdArray[0];
        if (fieldNameArray != null && fieldNameArray.length > 0) fieldName = fieldNameArray[0];
        if (fieldClassArray != null && fieldClassArray.length > 0) fieldClass = fieldClassArray[0];

        if (bindingId != null) {
            getFormManager().addDataFieldHolder(getCurrentForm(),bindingId,fieldName,fieldClass);
            setLastDataHolderUsedId(bindingId);
        }
    }


    public boolean isShowingTemplateEdition() {
        if (getFormTemplateEditor() != null) {
            return getFormTemplateEditor().isOn() && getEditionContext().getShowTemplateEdition();
        }
        return false;
    }

    public void actionSaveTemplate(CommandRequest request) throws Exception {
        checkEditionContext(request);

        String loadTemplate = request.getRequestObject().getParameter("loadTemplate");
        String templateContent = request.getRequestObject().getParameter("templateContent");
        String genModeTemplate = request.getRequestObject().getParameter("genModeTemplate");
        getFormTemplateEditor().setTemplateContent(templateContent);


        if (getFormTemplateEditor().isCancel()) {
            getFormTemplateEditor().setFormId(null);
        } else {
            getCurrentForm().setFormTemplate(getFormTemplateEditor().getTemplateContent());
            //FormCoreServices.lookup().getFormManager().saveTemplateForForm(getFormTemplateEditor().getFormId(), getFormTemplateEditor().getTemplateContent());
        }
        if(loadTemplate!=null && Boolean.valueOf(loadTemplate).booleanValue()){
            getFormTemplateEditor().setLoadTemplate(true);
            getFormTemplateEditor().setGenMode(genModeTemplate);
            getEditionContext().setShowTemplateEdition(true);
        } else{
            getFormTemplateEditor().setLoadTemplate(false);
            getFormTemplateEditor().setFormId(null);
            getEditionContext().setShowTemplateEdition(false);
        }
    }
}
