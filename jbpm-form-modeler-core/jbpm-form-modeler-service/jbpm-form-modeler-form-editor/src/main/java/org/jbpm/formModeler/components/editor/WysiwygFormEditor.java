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

import org.jbpm.formModeler.api.model.*;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.config.DataHolderManager;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.processing.*;
import org.jbpm.formModeler.core.wrappers.HTMLi18n;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Component to edit forms in a WYSIWYG way
 */
@ApplicationScoped
@Named("wysiwygfe")
public class WysiwygFormEditor extends BaseUIComponent {

    @Inject
    private DataHolderManager dataHolderManager;

    public static WysiwygFormEditor lookup() {
        return (WysiwygFormEditor) CDIBeanLocator.getBeanByType(WysiwygFormEditor.class);
    }

    @Inject
    private Log log;

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

    public static final String TOP_FIELD_MODIFIER = "topModifier";
    public static final String LEFT_FIELD_MODIFIER = "leftModifier";
    public static final String RIGHT_FIELD_MODIFIER = "rightModifier";
    public static final String BOTTOM_FIELD_MODIFIER = "bottomModifier";

    public static final String EDITION_OPTION_SAVE = "saveForm";
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
    public static final String PARAMETER_HOLDER_OUTPUT_ID = "holderOutputId";
    public static final String PARAMETER_HOLDER_INFO = "holderInfo";
    public static final String PARAMETER_HOLDER_TYPE = "holderType";
    public static final String PARAMETER_HOLDER_DM_INFO = "holderDMInfo";
    public static final String PARAMETER_HOLDER_PR_INFO = "holderPRInfo";
    public static final String PARAMETER_FIELD_NAME = "fieldName";
    public static final String PARAMETER_FIELD_TYPECODE = "typeCode";
    public static final String PARAMETER_HOLDER_RENDERCOLOR = "holderRenderColor";

    private int currentEditFieldPosition = -1;
    private boolean swapFields = true;
    private String fieldTypeToView = null;
    private String currentEditionOption = EDITION_OPTION_BINDINGS_SOURCES;
    private int lastMovedFieldPosition = -1;
    private boolean showReturnButton = false;
    private String renderMode = Form.RENDER_MODE_WYSIWYG_FORM;
    private Boolean displayBindings = Boolean.TRUE;
    private Boolean displayGrid = Boolean.TRUE;
    private Boolean showTemplateEdition = Boolean.FALSE;
    private FieldType originalFieldType;
    private String lastDataHolderUsedId = "";
    private FormEditorContext editionContext;

    public String getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode;
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
        return lastDataHolderUsedId;
    }

    public void setLastDataHolderUsedId(String lastDataHolderUsedId) {
        this.lastDataHolderUsedId = lastDataHolderUsedId;
    }

    public Boolean getDisplayBindings() {
        return displayBindings;
    }

    public void setDisplayBindings(Boolean displayBindings) {
        this.displayBindings = displayBindings;
    }

    public Boolean getDisplayGrid() {
        return displayGrid;
    }

    public void setDisplayGrid(Boolean displayGrid) {
        this.displayGrid = displayGrid;
    }

    public boolean isShowReturnButton() {
        return showReturnButton;
    }

    public void setShowReturnButton(boolean showReturnButton) {
        this.showReturnButton = showReturnButton;
    }

    public Form getCurrentForm() {
        return editionContext.getForm();
    }

    public String getNamespace() {
        return editionContext.getRenderContext().getUID();
    }

    public FormTemplateEditor getFormTemplateEditor() {
        return formTemplateEditor;
    }

    public void setFormTemplateEditor(FormTemplateEditor formTemplateEditor) {
        this.formTemplateEditor = formTemplateEditor;
    }

    public FormEditorContext getCurrentEditionContext() {
        return editionContext;
    }

    public String getCurrentEditionOption() {
        return currentEditionOption;
    }

    public void setCurrentEditionOption(String currentEditionOption) {
        this.currentEditionOption = currentEditionOption;
    }

    public int getCurrentEditFieldPosition() {
        return currentEditFieldPosition;
    }

    public void setCurrentEditFieldPosition(int currentEditFieldPosition) {
        this.currentEditFieldPosition = currentEditFieldPosition;
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

    public boolean isSwapFields() {
        return swapFields;
    }

    public void setSwapFields(boolean swapFields) {
        this.swapFields = swapFields;
    }

    public String getFieldTypeToView() {
        return fieldTypeToView;
    }

    public void setFieldTypeToView(String fieldTypeToView) {
        this.fieldTypeToView = fieldTypeToView;
    }

    public int getLastMovedFieldPosition() {
        return lastMovedFieldPosition;
    }

    public void setLastMovedFieldPosition(int lastMovedFieldPosition) {
        this.lastMovedFieldPosition = lastMovedFieldPosition;
    }

    public CommandResponse handle(CommandRequest commandRequest, String string) throws Exception {
        setLastMovedFieldPosition(-1);
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

        String ctxUID = commandRequest.getRequestObject().getParameter("ctxUID");

        if (!StringUtils.isEmpty(ctxUID)) {
            lastMovedFieldPosition = -1;
            setCurrentEditFieldPosition(-1);
            setFieldTypeToView(null);
            editionContext = formEditorContextManager.getFormEditorContext(ctxUID);
        }
    }

    public void actionDelete(CommandRequest request) throws Exception {
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
            if (currentEditFieldPosition == pos.intValue()) currentEditFieldPosition = -1;
            else if (currentEditFieldPosition > pos.intValue()) currentEditFieldPosition--;
        }
    }

    public void actionStartEdit(CommandRequest request) throws Exception {
        Integer pos = Integer.decode(request.getParameter("position"));
        setCurrentEditFieldPosition(pos.intValue());

        Field editField = getCurrentEditField();

        Form formToEdit = getFormForFieldEdition(editField);
        if (formToEdit != null) {
            getFormProcessor().clear(formToEdit, "edit_" + editField.getId());
            getFormProcessor().read(formToEdit, "edit_" + editField.getId(), editField.asMap());
        }
        originalFieldType = editField.getFieldType();
    }

    protected String getFieldEditionNamespace(Field field) {
        if (field == null) return "";

        String editionNamespace = formEditorContextManager.generateFieldEditionNamespace(editionContext.getRenderContext().getUID(), field);

        return editionNamespace;
    }

    public String getCurrentFieldEditionNamespace() {
        return getFieldEditionNamespace(getCurrentEditField());
    }

    public void actionSwapUp(CommandRequest request) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(lastMovedFieldPosition);
            if (fieldToMove != null) {
                Field previousField = getFieldInPosition(lastMovedFieldPosition - 1);
                if (previousField != null) {
                    Boolean b = previousField.getGroupWithPrevious();
                    previousField.setGroupWithPrevious(fieldToMove.getGroupWithPrevious());
                    fieldToMove.setGroupWithPrevious(b);
                    getFormManager().moveUp(form, lastMovedFieldPosition);
                } else {
                    fieldToMove.setGroupWithPrevious(Boolean.FALSE);
                }
                lastMovedFieldPosition--;
            } else {
                log.error("Cannot swap up unexistant field");
            }
        }
    }

    public void actionSwapDown(CommandRequest request) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(lastMovedFieldPosition);
            if (fieldToMove != null) {
                Field nextField = getFieldInPosition(lastMovedFieldPosition + 1);
                if (nextField == null) {
                    fieldToMove.setGroupWithPrevious(Boolean.FALSE);
                } else {
                    Boolean b = nextField.getGroupWithPrevious();
                    nextField.setGroupWithPrevious(fieldToMove.getGroupWithPrevious());
                    fieldToMove.setGroupWithPrevious(b);
                    getFormManager().moveDown(form, lastMovedFieldPosition);
                }
                lastMovedFieldPosition++;
            } else {
                log.error("Cannot swap down unexistant field");
            }
        }
    }


    public synchronized void actionMoveUp(CommandRequest request) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(lastMovedFieldPosition);
            if (fieldToMove != null) {
                if (Boolean.TRUE.equals(fieldToMove.getGroupWithPrevious())) {
                    Field previousField = getFieldInPosition(lastMovedFieldPosition - 1);
                    fieldToMove.setGroupWithPrevious(previousField.getGroupWithPrevious());
                    previousField.setGroupWithPrevious(Boolean.TRUE);
                    getFormManager().moveUp(form, lastMovedFieldPosition);
                } else {
                    Field nextField = getFieldInPosition(lastMovedFieldPosition + 1);
                    if (nextField != null) {
                        nextField.setGroupWithPrevious(Boolean.FALSE);
                    }
                    fieldToMove.setGroupWithPrevious(Boolean.TRUE);
                }
                lastMovedFieldPosition--;
            } else {
                log.error("Cannot move up unexistant field");
            }
        }
    }

    public synchronized void actionMoveDown(CommandRequest request) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(lastMovedFieldPosition);
            if (fieldToMove != null) {
                Field nextField = getFieldInPosition(lastMovedFieldPosition + 1);
                if (nextField == null) {
                    fieldToMove.setGroupWithPrevious(Boolean.FALSE);
                } else if (Boolean.TRUE.equals(nextField.getGroupWithPrevious())) {
                    nextField.setGroupWithPrevious(fieldToMove.getGroupWithPrevious());
                    fieldToMove.setGroupWithPrevious(Boolean.TRUE);
                    getFormManager().moveDown(form, lastMovedFieldPosition);
                } else {
                    nextField.setGroupWithPrevious(Boolean.TRUE);
                    fieldToMove.setGroupWithPrevious(Boolean.FALSE);
                }
                lastMovedFieldPosition++;
            } else {
                log.error("Cannot move down unexistant field");
            }
        }
    }

    public synchronized void actionPutInNewLine(CommandRequest request) throws Exception {
        final Integer pos = Integer.decode(request.getParameter("position"));
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            getFormManager().groupWithPrevious(form, pos.intValue(), false);
        }
    }

    public synchronized void actionPutInPreviousLine(CommandRequest request) throws Exception {
        final Integer pos = Integer.decode(request.getParameter("position"));
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            getFormManager().groupWithPrevious(form, pos.intValue(), true);
        }
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
        final String fieldType = request.getParameter("fieldType");
        Form form = getCurrentForm();
        addFieldToForm(form, fieldType);
    }

    public void actionAddDecoratorToForm(CommandRequest request) throws Exception {
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

        Field pField = getCurrentEditField();
        Map parameterMap = request.getRequestObject().getParameterMap();
        Map filesMap = request.getFilesByParamName();
        String action = request.getRequestObject().getParameter(ACTION_TO_DO);
        if (pField == null) {
            log.error("Cannot update unexistant field.");
        } else {
            if (ACTION_CANCEL_FIELD_EDITION.equals(action)) {
                Form editForm = getFormForFieldEdition(pField);
                pField.setFieldType(originalFieldType);
                getFormProcessor().clear(editForm, "edit_" + pField.getId());
                originalFieldType = null;
                currentEditFieldPosition = -1;
            } else {
                //Use custom edit form
                Form editForm = getFormForFieldEdition(pField);
                getFormProcessor().setValues(editForm, "edit_" + pField.getId(), parameterMap, filesMap);
                FormStatusData data = getFormProcessor().read(editForm, "edit_" + pField.getId());

                if (ACTION_CHANGE_FIELD_TYPE.equals(action)) {
                    fieldTypeToView = ((String[]) parameterMap.get("fieldType"))[0];
                    pField.setFieldType(getFieldTypesManager().getTypeByCode(getFieldTypeToView()));
                    Form formToEdit = getFormForFieldEdition(pField);
                    if (formToEdit != null) {
                        getFormProcessor().clear(formToEdit, "edit_" + pField.getId());
                        getFormProcessor().read(formToEdit, "edit_" + pField.getId(), data.getCurrentValues());
                    }

                } else {
                    if (data.isValid()) {

                        /*
                        * TODO: fix that
                        */
                        Set names = pField.getPropertyNames();

                        for (Iterator it = data.getCurrentValues().keySet().iterator(); it.hasNext(); ) {
                            String propertyName = (String) it.next();
                            if (names.contains(propertyName)) {
                                Object value = data.getCurrentValue(propertyName);
                                try {
                                    getBindingManager().setPropertyValue(pField, propertyName, value);
                                } catch (Exception e) {
                                    log.error("Error setting property '" + propertyName + "' on field " + pField.getFieldName(), e);
                                }

                            }
                        }

                        currentEditFieldPosition = -1;
                        pField.setFieldType(getFieldTypesManager().getTypeByCode(getFieldTypeToView()));
                        getFormProcessor().clear(editForm, "edit_" + pField.getId());
                        getFormProcessor().read(editForm, "edit_" + pField.getId(), data.getCurrentValues());
                    }
                }


            }

        }
    }

    public synchronized void actionMoveField(CommandRequest request) throws Exception {
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

            lastMovedFieldPosition = destPosition;

            if (currentEditFieldPosition == origPosition) currentEditFieldPosition = lastMovedFieldPosition;

            if (Boolean.parseBoolean(promote)) {
                getFormManager().promoteField(form, origPosition, destPosition, groupWithPrevious, nextGrouped);
                if (currentEditFieldPosition < origPosition && destPosition <= currentEditFieldPosition)
                    currentEditFieldPosition++;
            } else {
                getFormManager().degradeField(form, origPosition, destPosition, groupWithPrevious, nextGrouped);
                if (currentEditFieldPosition > origPosition && destPosition >= currentEditFieldPosition)
                    currentEditFieldPosition--;
            }
        }
    }

    public synchronized void actionMoveFirst(CommandRequest request) throws Exception {
        int fieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            getFormManager().moveTop(form, fieldPosition);
            lastMovedFieldPosition = 0;
            if (currentEditFieldPosition == fieldPosition) currentEditFieldPosition = lastMovedFieldPosition;
            else if (currentEditFieldPosition > -1 && fieldPosition > currentEditFieldPosition)
                currentEditFieldPosition++;
        }
    }

    public synchronized void actionMoveLast(CommandRequest request) throws Exception {
        int fieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            getFormManager().moveBottom(form, fieldPosition);
            lastMovedFieldPosition = form.getFormFields().size() - 1;
            if (currentEditFieldPosition == fieldPosition) currentEditFieldPosition = lastMovedFieldPosition;
            else if (fieldPosition < currentEditFieldPosition) currentEditFieldPosition--;
        }

    }

    /*
  public void actionAddComplexFieldToForm(CommandRequest request) throws Exception {
      String managerClass = request.getParameter("fieldClass");
      String name = request.getParameter("name");
      String label = request.getParameter("label");
      Long typeDbid = null;
      Form editForm = getCurrentForm();
      PropertyDefinition propDef = ddmManager.getPropertyType(name, editForm.getSubject());
      List fieldTypes = getFieldTypesManager().getSuitableFieldTypes(name, propDef);
      for (int i = 0; i < fieldTypes.size(); i++) {
          FieldType type = (FieldType) fieldTypes.get(i);
          if (type.getManagerClass().equals(managerClass)) {
              typeDbid = type.getDbid();
              break;
          }
      }
      if (typeDbid != null)
          addFieldToForm(editForm, typeDbid, label, name);
      else
          log.error("Could not add " + name + " field to form. Not found type for manager " + managerClass);
  }
    */

    public synchronized void actionSaveCurrentForm(CommandRequest request) throws Exception {
        saveCurrentForm(request.getRequestObject().getParameterMap());
    }

    public synchronized void actionSwitchRenderMode(CommandRequest request) throws Exception {
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
        String option = request.getRequestObject().getParameter("newMainOption");
        if (EDITION_OPTION_SAVE.equals(option)) formEditorContextManager.saveContext(editionContext.getRenderContext().getUID());
        else setCurrentEditionOption(option);
    }

    public void saveCurrentForm(Map parameterMap) throws Exception {
        Map map = fillFormFromForm(parameterMap);
        String name = (String) map.get("name");
        String displayMode = (String) map.get("displayMode");
        String labelMode = (String) map.get("labelMode");
        Long status = (Long) map.get("status");

        Form form = getCurrentForm();
        form.setName(name);
        form.setDisplayMode(displayMode);
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
            showTemplateEdition = true;
        }
    }

    protected Map fillFormFromForm(Map parameterMap) {
        Map m = new HashMap();
        String[] name = (String[]) parameterMap.get("name");
        String[] displayMode = (String[]) parameterMap.get("displayMode");
        String[] labelMode = (String[]) parameterMap.get("labelMode");
        String[] status = (String[]) parameterMap.get("status");
        String[] copyingFrom = (String[]) parameterMap.get("copyingFrom");

        if (status == null || status.length == 0) {
            status = new String[]{String.valueOf(FormManagerImpl.FORMSTATUS_NORMAL)};
        }

        m.put("name", (name != null && name.length > 0) ? name[0] : null);
        m.put("displayMode", (displayMode != null && displayMode.length > 0) ? displayMode[0] : "default");
        m.put("labelMode", (labelMode != null && labelMode.length > 0) ? labelMode[0] : "undefined");
        m.put("status", Long.decode(status[0]));
        m.put("copyingFrom", (copyingFrom != null && !"".equals(copyingFrom[0].trim())) ? (Long.decode(copyingFrom[0])) : null);
        return m;
    }

    /*
   public boolean isShowingTemplateEdition() {
       if (getFormTemplateEditor() != null) {
           return getFormTemplateEditor().isOn();
       }
       return false;
   } */

    public void actionUnGroupWithPrevious(CommandRequest request) throws Exception {
        groupField(request, false);
    }

    public void actionGroupWithPrevious(CommandRequest request) throws Exception {
        groupField(request, true);
    }


    protected void groupField(CommandRequest request, final boolean groupIt) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(lastMovedFieldPosition);
            if (fieldToMove != null) {
                fieldToMove.setGroupWithPrevious(Boolean.valueOf(groupIt));
            } else {
                log.error("Cannot modify unexistant field");
            }
        }
    }

    public synchronized void actionAddFieldFromDataHolder(CommandRequest request) throws Exception {
        addDataHolderFieldToForm(request.getRequestObject().getParameterMap());
    }

    public synchronized void actionFormDataHolders(CommandRequest request) throws Exception {
        String action = request.getRequestObject().getParameter(ACTION_TO_DO);
        if (ACTION_ADD_DATA_HOLDER.equals(action)) {
            addDataHolder(request.getRequestObject().getParameterMap());
        } else if (ACTION_REMOVE_DATA_HOLDER.equals(action)) {
            removeDataHolder(request.getRequestObject().getParameterMap());
        } else if (ACTION_ADD_DATA_HOLDER_FIELDS.equals(action)) {
            addAllDataHolderFieldsToForm(request.getRequestObject().getParameterMap());
        }

    }

    public void addDataHolder(Map parameterMap) throws Exception {
        String[] holderTypeArray = (String[]) parameterMap.get(PARAMETER_HOLDER_TYPE);
        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);
        String[] holderOutIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_OUTPUT_ID);
        String[] holderRenderColorArray = (String[]) parameterMap.get(PARAMETER_HOLDER_RENDERCOLOR);

        String holderType = null;
        if (holderTypeArray != null && holderTypeArray.length > 0) holderType = holderTypeArray[0];

        String holderId = null;
        if (holderIdArray != null && holderIdArray.length > 0) holderId = holderIdArray[0];

        String holderOutId = null;
        if (holderOutIdArray != null && holderOutIdArray.length > 0) holderOutId = holderOutIdArray[0];

        String holderRenderColor = null;
        if (holderRenderColorArray != null && holderRenderColorArray.length > 0)
            holderRenderColor = holderRenderColorArray[0];

        String holderInfo = null;
        String[] holderInfoArray = null;

        Map<String, Object> config = new HashMap<String, Object>();

        config.put("id", holderId);
        config.put("outId", holderOutId);
        config.put("color", holderRenderColor);
        if (Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL.equals(holderType)) {
            config.put("path", getCurrentEditionContext().getPath());
            holderInfoArray = (String[]) parameterMap.get(PARAMETER_HOLDER_DM_INFO);
        } else if (Form.HOLDER_TYPE_CODE_POJO_CLASSNAME.equals(holderType)) holderInfoArray = (String[]) parameterMap.get(PARAMETER_HOLDER_INFO);
        if (holderInfoArray != null && holderInfoArray.length > 0) holderInfo = holderInfoArray[0];

        config.put("value", holderInfo);

        DataHolder holder = dataHolderManager.createDataHolderByType(holderType, config);

        Form form = getCurrentForm();
        if (holder != null) form.setDataHolder(holder);
    }


    public void removeDataHolder(Map parameterMap) throws Exception {
        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);

        String holderId = null;
        if (holderIdArray != null && holderIdArray.length > 0) holderId = holderIdArray[0];

        if ((holderId != null)) {
            Form form = getCurrentForm();
            form.removeDataHolder(holderId);
        }
    }

    public void addAllDataHolderFieldsToForm(Map parameterMap) throws Exception {

        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);

        String holderId = null;
        if (holderIdArray != null && holderIdArray.length > 0) holderId = holderIdArray[0];

        if (holderId != null) {
            Form form = getCurrentForm();
            DataHolder holder = form.getDataHolderById(holderId);

            Set<DataFieldHolder> holderFields = holder.getFieldHolders();
            for (DataFieldHolder dataFieldHolder : holderFields) {
                addDataFieldHolder(form, holder, dataFieldHolder.getId(), getFieldTypesManager().getTypeByCode(dataFieldHolder.getType()));
            }

        }
    }

    public void addDataHolderFieldToForm(Map parameterMap) throws Exception {

        String[] holderIdArray = (String[]) parameterMap.get(PARAMETER_HOLDER_ID);
        String[] fieldNameArray = (String[]) parameterMap.get(PARAMETER_FIELD_NAME);
        String[] fieldTypeCodeArray = (String[]) parameterMap.get(PARAMETER_FIELD_TYPECODE);

        String bindingId = null;
        if (holderIdArray != null && holderIdArray.length > 0) bindingId = holderIdArray[0];

        String fieldName = null;
        if (fieldNameArray != null && fieldNameArray.length > 0) fieldName = fieldNameArray[0];

        String fieldTypeCode = null;
        if (fieldTypeCodeArray != null && fieldTypeCodeArray.length > 0) fieldTypeCode = fieldTypeCodeArray[0];


        if (bindingId != null) {
            Form form = getCurrentForm();
            DataHolder holder = form.getDataHolderById(bindingId);
            addDataFieldHolder(form, holder, fieldName, getFieldTypesManager().getTypeByCode(fieldTypeCode));
        }
    }

    private void addDataFieldHolder(Form form, DataHolder holder, String fieldName, FieldType fieldType) throws Exception {
        I18nSet label = new I18nSet();
        String defaultLang = LocaleManager.lookup().getDefaultLang();
        String dataHolderId = StringUtils.defaultIfEmpty(holder.getInputId(), holder.getOuputId());
        label.setValue(defaultLang, fieldName + " (" + dataHolderId + ")");

        String inputBinging = holder.getInputBinding(fieldName);
        String outputBinding = holder.getOuputBinding(fieldName);

        getFormManager().addFieldToForm(form, dataHolderId + "_" + fieldName, fieldType, label, inputBinging, outputBinding);
        setLastDataHolderUsedId(dataHolderId);
    }

    public boolean isShowingTemplateEdition() {
        if (getFormTemplateEditor() != null) {
            return getFormTemplateEditor().isOn() && showTemplateEdition;
        }
        return false;
    }

    public void actionSaveTemplate(CommandRequest request) throws Exception {
        String loadTemplate = request.getRequestObject().getParameter("loadTemplate");
        String templateContent = request.getRequestObject().getParameter("templateContent");
        String genModeTemplate = request.getRequestObject().getParameter("genModeTemplate");
        getFormTemplateEditor().setTemplateContent(templateContent);


        if (getFormTemplateEditor().isCancel()) {
            getFormTemplateEditor().setFormId(null);
        } else {
            //if (getFormTemplateEditor().isPersist()) {
                FormCoreServices.lookup().getFormManager().saveTemplateForForm(getFormTemplateEditor().getFormId(), getFormTemplateEditor().getTemplateContent());
                //getFormTemplateEditor().setFormId(null);
            //}
        }
        if(loadTemplate!=null && Boolean.valueOf(loadTemplate).booleanValue()){
            getFormTemplateEditor().setLoadTemplate(true);
            getFormTemplateEditor().setGenMode(genModeTemplate);
            showTemplateEdition =true;
        } else{
            getFormTemplateEditor().setLoadTemplate(false);
            getFormTemplateEditor().setFormId(null);
            showTemplateEdition =false;
        }
    }
}
