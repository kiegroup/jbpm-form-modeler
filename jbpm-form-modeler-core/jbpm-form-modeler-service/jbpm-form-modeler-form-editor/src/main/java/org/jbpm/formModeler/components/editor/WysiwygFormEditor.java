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

import org.jbpm.formModeler.api.config.FieldTypeManager;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.model.BindingSource;
import org.jbpm.formModeler.api.processing.BindingManager;
import org.jbpm.formModeler.core.processing.BindingManagerImpl;
import org.jbpm.formModeler.core.wrappers.HTMLi18n;
import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.config.FieldTypeManagerImpl;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.util.helpers.EditorHelper;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.i18n.I18nSet;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.processing.FormProcessor;
import org.jbpm.formModeler.api.processing.FormStatusData;

import java.util.*;

public class WysiwygFormEditor extends BaseUIComponent {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WysiwygFormEditor.class.getName());

    private String componentIncludeJSP;
    private String baseComponentJSP;

    public static final String TOP_FIELD_MODIFIER = "topModifier";
    public static final String LEFT_FIELD_MODIFIER = "leftModifier";
    public static final String RIGHT_FIELD_MODIFIER = "rightModifier";
    public static final String BOTTOM_FIELD_MODIFIER = "bottomModifier";

    public static final String EDITION_OPTION_FIELDTYPES = "fieldTypes";
    public static final String EDITION_OPTION_FORM_PROPERTIES = "formProperties";
    public static final String EDITION_OPTION_BINDINGS_FIELDS = "fieldBindingsFields";
    public static final String EDITION_OPTION_BINDINGS_SOURCES = "fieldBindingsSources";

    public static final String EDITION_OPTION_IMG_FIELDTYPES = "general/AddFieldsByType.png";
    public static final String EDITION_OPTION_IMG_FORM_PROPERTIES = "general/FormProperties.png";
    public static final String EDITION_OPTION_IMG_BINDINGS_FIELDS = "general/Bindings.png";
    public static final String EDITION_OPTION_IMG_BINDINGS_SOURCES = "general/Bindings.png";

    public static final String EDITION_OPTION_VIS_MODE_FIELDTYPES = "shared";
    public static final String EDITION_OPTION_VIS_MODE_FORM_PROPERTIES = "shared";
    public static final String EDITION_OPTION_VIS_MODE_BINDINGS_FIELDS = "shared";
    public static final String EDITION_OPTION_VIS_MODE_BINDINGS_SOURCE = "full";

    public static final String EDITION_OPTION_IMG_FORM_INSERTMODE = "general/InsertDataMode.png";
    public static final String EDITION_OPTION_IMG_FORM_SHOWTMODE = "general/ShowDataMode.png";

    public static final String ACTION_TO_DO = "actionToDo";
    public static final String ACTION_CHANGE_FIELD_TYPE = "changeFieldType";
    public static final String ACTION_SAVE_FIELD_PROPERTIES = "saveFieldProperties";
    public static final String ACTION_CANCEL_FIELD_EDITION = "cancelFieldEdition";

    public static final String ACTION_REMOVE_BINDING_VAR= "removeBindingVar";
    public static final String ACTION_ADD_BINDING_VAR= "addBindingVar";
    public static final String ACTION_ADD_BINDING_FIELDS= "addBindingFields";

    private Form currentForm;
    private int currentEditFieldPosition = -1;
    private FormProcessor defaultFormProcessor;
    private FormManager formManager;
    private FieldTypeManager fieldTypeManager;
    private BindingManager bindingManager;
    private LocaleManager localeManager;
    private boolean swapFields = true;
    private String fieldTypeToView = null;
    private String currentEditionOption = EDITION_OPTION_FIELDTYPES;
    private FormTemplateEditor formTemplateEditor;
    private int lastMovedFieldPosition = -1;
    private boolean showReturnButton = false;
    private String renderMode = Form.RENDER_MODE_WYSIWYG_FORM;
    private FieldType originalFieldType;

    private String lastBindingUsedId = "";

    @Override
    public void start() throws Exception {
        super.start();
        formManager = FormManagerImpl.lookup();
        fieldTypeManager = FieldTypeManagerImpl.lookup();
        bindingManager = BindingManagerImpl.lookup();
        localeManager = (LocaleManager) Factory.lookup("org.jbpm.formModeler.service.LocaleManager");
    }

    public boolean isShowReturnButton() {
        return showReturnButton;
    }

    public void setShowReturnButton(boolean showReturnButton) {
        this.showReturnButton = showReturnButton;
    }

    public Form getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(Form currentForm) {
        this.currentForm = currentForm;
        lastMovedFieldPosition = -1;
        setCurrentEditFieldPosition(-1);
        setFieldTypeToView(null);
    }

    public FormTemplateEditor getFormTemplateEditor() {
        return formTemplateEditor;
    }

    public void setFormTemplateEditor(FormTemplateEditor formTemplateEditor) {
        this.formTemplateEditor = formTemplateEditor;
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
        return fieldTypeManager;
    }

    public void setFieldTypesManager(FieldTypeManagerImpl FieldTypeManagerImpl) {
        this.fieldTypeManager = FieldTypeManagerImpl;
    }

    public BindingManager getBindingManager() {
        return bindingManager;
    }

    public void setBindingManager(BindingManager bindingManager) {
        this.bindingManager = bindingManager;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
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

    public String getBeanName() {
        return super.getComponentName();
    }

    public Form getCurrentEditForm() {
        return currentForm;
    }

    public Field getCurrentEditField() {
        return getFieldInPosition(getCurrentEditFieldPosition());
    }

    protected Field getFieldInPosition(int position) {
        if (position != -1) {
            Form form = getCurrentEditForm();
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

        EditorHelper helper = (EditorHelper) commandRequest.getSessionObject().getAttribute("EditorHelper");
        String contextURI = (String) commandRequest.getSessionObject().getAttribute("contextURI");

        if (helper != null && contextURI != null) setCurrentForm(helper.getFormToEdit(contextURI));
        else {
            String formId = commandRequest.getRequestObject().getParameter("formId");
            setCurrentForm(formManager.getFormById(Long.decode(formId)));
        }

    }

    public void actionDelete(CommandRequest request) throws Exception {
        Long pos = Long.decode(request.getParameter("position"));
        Form form = getCurrentEditForm();
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
            formManager.deleteField(form, pos.intValue());
            if (currentEditFieldPosition == pos.intValue()) currentEditFieldPosition = -1;
            else if (currentEditFieldPosition > pos.intValue()) currentEditFieldPosition--;
        }
    }

    public void actionStartEdit(CommandRequest request) throws Exception {
        Integer pos = Integer.decode(request.getParameter("position"));
        setCurrentEditFieldPosition(pos.intValue());

        Form formToEdit = getFormularyForFieldEdition(getCurrentEditField());
        if (formToEdit != null) {
            defaultFormProcessor.clear(formToEdit.getId(), "edit_" + getCurrentEditField().getId());
        }
        originalFieldType = getCurrentEditField().getFieldType();
    }

    public void actionSwapUp(CommandRequest request) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentEditForm();
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
                    formManager.moveUp(form, lastMovedFieldPosition);
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
        Form form = getCurrentEditForm();
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
                    formManager.moveDown(form, lastMovedFieldPosition);
                }
                lastMovedFieldPosition++;
            } else {
                log.error("Cannot swap down unexistant field");
            }
        }
    }


    public synchronized void actionMoveUp(CommandRequest request) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentEditForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            Field fieldToMove = getFieldInPosition(lastMovedFieldPosition);
            if (fieldToMove != null) {
                if (Boolean.TRUE.equals(fieldToMove.getGroupWithPrevious())) {
                    Field previousField = getFieldInPosition(lastMovedFieldPosition - 1);
                    fieldToMove.setGroupWithPrevious(previousField.getGroupWithPrevious());
                    previousField.setGroupWithPrevious(Boolean.TRUE);
                    formManager.moveUp(form, lastMovedFieldPosition);
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
        Form form = getCurrentEditForm();
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
                    formManager.moveDown(form, lastMovedFieldPosition);
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
        Form form = getCurrentEditForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            formManager.groupWithPrevious(form, pos.intValue(), false);
        }
    }

    public synchronized void actionPutInPreviousLine(CommandRequest request) throws Exception {
        final Integer pos = Integer.decode(request.getParameter("position"));
        Form form = getCurrentEditForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            formManager.groupWithPrevious(form, pos.intValue(), true);
        }
    }


    public void actionEnd(CommandRequest request) throws Exception {
        setCurrentForm(null);
    }

    public boolean isActive() {
        return currentForm != null;
    }

    protected void addFieldToForm(Form form, String typeId) throws Exception {
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            FieldType fType = fieldTypeManager.getTypeByCode(typeId);
            formManager.addFieldToForm(form, fType);
        }
    }

    public void actionAddFieldToFormulary(CommandRequest request) throws Exception {
        final String fieldType = request.getParameter("fieldType");
        Form form = getCurrentEditForm();
        addFieldToForm(form, fieldType);
    }

    public void actionAddDecoratorToFormulary(CommandRequest request) throws Exception {
        final String fieldType = request.getParameter("fieldType");
        Form form = getCurrentEditForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            final String name = generateDecoratorName(form);
            I18nSet label = new I18nSet();
            String lang = getLocaleManager().getDefaultLang();
            FieldType fType = fieldTypeManager.getTypeByCode(fieldType);
            Field formField = formManager.addFieldToForm(form, name, fType, label);

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

    public Form getFormularyForFieldEdition(Field field) throws Exception {
        if (getFieldTypeToView() != null) {
            return formManager.getFormForFieldEdition(fieldTypeManager.getTypeByCode(getFieldTypeToView()));
        }
        return formManager.getFormForFieldEdition(field.getFieldType());
    }

    public void actionSaveFieldProperties(final CommandRequest request) throws Exception {

        Field pField = getCurrentEditField();
        Map parameterMap = request.getRequestObject().getParameterMap();
        Map filesMap = request.getFilesByParamName();
        String action = request.getRequestObject().getParameter(ACTION_TO_DO);
        if (pField == null) {
            log.error("Cannot update unexistant field.");
        } else {
            fieldTypeToView = ((String[]) parameterMap.get("fieldType"))[0];

            Form editForm = getFormularyForFieldEdition(pField);

            if (ACTION_CHANGE_FIELD_TYPE.equals(action)) {
                pField.setFieldType(fieldTypeManager.getTypeByCode(getFieldTypeToView()));
            } else if (ACTION_CANCEL_FIELD_EDITION.equals(action)) {
                pField.setFieldType(originalFieldType);
                defaultFormProcessor.clear(editForm.getId(), "edit_" + pField.getId());
                originalFieldType = null;
                currentEditFieldPosition = -1;
            } else {
                //Use custom edit form
                defaultFormProcessor.setValues(editForm, "edit_" + pField.getId(), parameterMap, filesMap);
                FormStatusData data = defaultFormProcessor.read(editForm.getId(), "edit_" + pField.getId());
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
                                bindingManager.setPropertyValue(pField, propertyName, value);
                            } catch (Exception e) {
                                log.error("Error setting property '" + propertyName + "' on field " + pField.getFieldName(), e);
                            }

                        }
                    }

                    pField.setFieldType(fieldTypeManager.getTypeByCode(getFieldTypeToView()));
                    currentEditFieldPosition = -1;
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

        Form form = getCurrentEditForm();
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
                formManager.promoteField(form, origPosition, destPosition, groupWithPrevious, nextGrouped);
                if (currentEditFieldPosition < origPosition && destPosition <= currentEditFieldPosition)
                    currentEditFieldPosition++;
            } else {
                formManager.degradeField(form, origPosition, destPosition, groupWithPrevious, nextGrouped);
                if (currentEditFieldPosition > origPosition && destPosition >= currentEditFieldPosition)
                    currentEditFieldPosition--;
            }
        }
    }

    public synchronized void actionMoveFirst(CommandRequest request) throws Exception {
        int fieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentEditForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            formManager.moveTop(form, fieldPosition);
            lastMovedFieldPosition = 0;
            if (currentEditFieldPosition == fieldPosition) currentEditFieldPosition = lastMovedFieldPosition;
            else if (currentEditFieldPosition > -1 && fieldPosition > currentEditFieldPosition) currentEditFieldPosition ++;
        }
    }

    public synchronized void actionMoveLast(CommandRequest request) throws Exception {
        int fieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentEditForm();
        if (form == null) {
            log.error("Cannot modify unexistant form.");
        } else {
            formManager.moveBottom(form, fieldPosition);
            lastMovedFieldPosition = form.getFormFields().size() - 1;
            if (currentEditFieldPosition == fieldPosition) currentEditFieldPosition = lastMovedFieldPosition;
            else if (fieldPosition < currentEditFieldPosition) currentEditFieldPosition--;
        }

    }

    /*
  public void actionAddComplexFieldToFormulary(CommandRequest request) throws Exception {
      String managerClass = request.getParameter("fieldClass");
      String name = request.getParameter("name");
      String label = request.getParameter("label");
      Long typeDbid = null;
      Form editForm = getCurrentEditForm();
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
        if (Form.RENDER_MODE_WYSIWYG_DISPLAY.equals(renderMode) || Form.RENDER_MODE_WYSIWYG_FORM.equals(renderMode))
            setRenderMode(renderMode);
    }

    public void saveCurrentForm(Map parameterMap) throws Exception {
        Map map = fillFormFromForm(parameterMap);
        String name = (String) map.get("name");
        String displayMode = (String) map.get("displayMode");
        String labelMode = (String) map.get("labelMode");
        Long status = (Long) map.get("status");

        Form form = getCurrentEditForm();
        form.setName(name);
        form.setDisplayMode(displayMode);
        form.setLabelMode(labelMode);
        form.setStatus(status);

        String[] editTemplateParams = (String[]) parameterMap.get("editTemplate");
        if (editTemplateParams != null && editTemplateParams.length > 0 && "true".equals(editTemplateParams[0])) {
            getFormTemplateEditor().setFormId(currentForm.getId());
            Long formId = currentForm.getId();
            if (formId != null) getFormTemplateEditor().setFormId(formId);
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

    public void createForm(Map parameterMap) {
        Map map = fillFormFromForm(parameterMap);
        String name = (String) map.get("name");
        String displayMode = (String) map.get("displayMode");
        Long copyingFrom = (Long) map.get("copyingFrom");
        Long status = (Long) map.get("status");
        try {
            Form form = null;
            if (copyingFrom == null) {
                form = formManager.createForm("", name, displayMode, status);
            } else {
                form = formManager.duplicateForm(copyingFrom, name, displayMode, status);
            }
            setCurrentForm(form);
        } catch (Exception e) {
            log.error("Error:", e);
        }
    }
    /*
   public boolean isShowingTemplateEdition() {
       if (getFormTemplateEditor() != null) {
           return getFormTemplateEditor().isOn();
       }
       return false;
   } */

    public boolean isShowingFormsList() {
        return !isActive();
    }

    public void actionUnGroupWithPrevious(CommandRequest request) throws Exception {
        groupField(request, false);
    }

    public void actionGroupWithPrevious(CommandRequest request) throws Exception {
        groupField(request, true);
    }


    protected void groupField(CommandRequest request, final boolean groupIt) throws Exception {
        lastMovedFieldPosition = Integer.decode(request.getParameter("position")).intValue();
        Form form = getCurrentEditForm();
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

    public synchronized void actionAddFieldFromBinding(CommandRequest request) throws Exception {
        addBindingsFieldToForm(request.getRequestObject().getParameterMap());
    }

    public synchronized void actionFormBindings(CommandRequest request) throws Exception {
        String action = request.getRequestObject().getParameter(ACTION_TO_DO);
        if(ACTION_ADD_BINDING_VAR.equals(action)){
            addBindingVar(request.getRequestObject().getParameterMap());
        } else if(ACTION_REMOVE_BINDING_VAR.equals(action)){
            removeBindingVar(request.getRequestObject().getParameterMap());
        } else if(ACTION_ADD_BINDING_FIELDS.equals(action)){
            addBindingsFieldsToForm(request.getRequestObject().getParameterMap());
        }

    }

    public void addBindingVar(Map parameterMap) throws Exception {
        String[] name = (String[]) parameterMap.get("className");
        String[] binding = (String[]) parameterMap.get("bindingId");

        String className = null;
        if (name != null && name.length > 0) className = name[0];

        String bindingId = null;
        if (binding != null && binding.length > 0) bindingId = binding[0];

        if ((className != null) && ( bindingId!= null)){
            Form form = getCurrentEditForm();
            form.setBindingSource(bindingId, BindingSource.BINDING_CODE_TYPE_CLASSNAME, className);
        }
    }


    public void removeBindingVar(Map parameterMap) throws Exception {
        String[] binding = (String[]) parameterMap.get("bindingId");

        String bindingId = null;
        if (binding != null && binding.length > 0) bindingId = binding[0];

        if ( ( bindingId!= null)){
            Form form = getCurrentEditForm();
            form.removeBindingSource(bindingId);
        }
    }

    public void addBindingsFieldsToForm(Map parameterMap) throws Exception {

        String[] binding = (String[]) parameterMap.get("bindingId");

        String bindingId = null;
        if (binding != null && binding.length > 0) bindingId = binding[0];

        if ( bindingId!= null){
            Form form = getCurrentEditForm();
            BindingSource source = form.getBindingSource(bindingId);

            Map propertyNames = bindingManager.getBindingFields(source);

            String fieldName = "";
            for (Iterator it = propertyNames.keySet().iterator(); it.hasNext(); ) {
                fieldName = (String) it.next();
                addBindingField(form,source,fieldName,fieldTypeManager.getTypeByClass(((Class) propertyNames.get(fieldName)).getName()));
            }
        }
    }

    public void addBindingsFieldToForm(Map parameterMap) throws Exception {

        String[] bindings = (String[]) parameterMap.get("bindingId");
        String[] fieldNames = (String[]) parameterMap.get("fieldName");
        String[] fieldTypeCodes = (String[]) parameterMap.get("fieldTypeCode");

        String bindingId = null;
        if (bindings != null && bindings.length > 0) bindingId = bindings[0];

        String fieldName = null;
        if (fieldNames != null && fieldNames.length > 0) fieldName = fieldNames[0];

        String fieldTypeCode = null;
        if (fieldTypeCodes != null && fieldTypeCodes.length > 0) fieldTypeCode = fieldTypeCodes[0];


        if ( bindingId!= null){
            Form form = getCurrentEditForm();
            BindingSource source = form.getBindingSource(bindingId);
            addBindingField(form,source,fieldName,fieldTypeManager.getTypeByCode(fieldTypeCode));
        }
    }

    private void addBindingField(Form form,BindingSource source,String fieldName, FieldType fieldType) throws Exception{
        I18nSet label = new I18nSet();
        String defaultLang = localeManager.getDefaultLang();
        label.setValue(defaultLang, fieldName+" ("+source.getId()+")");
        formManager.addFieldToForm(form, source.getId()+"_"+fieldName, fieldType, label, "{" + source.getId() + "/" + fieldName+"}");
        setLastBindingUsedId(source.getId());
    }

    public String getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode;
    }

    public FormManager getFormManager() {
        return formManager;
    }

    public FormProcessor getDefaultFormProcessor() {
        return defaultFormProcessor;
    }

    public void setDefaultFormProcessor(FormProcessor defaultFormProcessor) {
        this.defaultFormProcessor = defaultFormProcessor;
    }

    public String getBaseComponentJSP() {
        return baseComponentJSP;
    }

    public void setBaseComponentJSP(String baseComponentJSP) {
        this.baseComponentJSP = baseComponentJSP;
    }

    public static Log getLog() {
        return log;
    }

    public static void setLog(Log log) {
        WysiwygFormEditor.log = log;
    }

    public String getLastBindingUsedId() {
        return lastBindingUsedId;
    }

    public void setLastBindingUsedId(String lastBindingUsedId) {
        this.lastBindingUsedId = lastBindingUsedId;
    }
}
