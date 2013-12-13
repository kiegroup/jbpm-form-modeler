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
package org.jbpm.formModeler.api.client;

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;

import java.io.Serializable;


public class FormEditorContext implements Serializable {
    private FormRenderContext renderContext;
    private String path;

    private int currentEditFieldPosition = -1;
    private boolean swapFields = true;
    private String fieldTypeToView = null;
    private String currentEditionOption;
    private int lastMovedFieldPosition = -1;
    private boolean showReturnButton = false;
    private String renderMode = Form.RENDER_MODE_WYSIWYG_FORM;
    private Boolean displayBindings = Boolean.TRUE;
    private Boolean displayGrid = Boolean.TRUE;
    private Boolean showTemplateEdition = Boolean.FALSE;
    private FieldType originalFieldType;
    private String lastDataHolderUsedId = "";


    public FormEditorContext(FormRenderContext ctx, String path) {
        this.renderContext = ctx;
        this.path = path;
    }

    public String getUID() {
        return renderContext.getUID();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FormRenderContext getRenderContext() {
        return renderContext;
    }

    public Form getForm() {
        return renderContext.getForm();
    }

    public void setForm(Form form) {
        renderContext.setForm(form);
    }

    /*--Form edition status ----------------------------------*/

    public int getCurrentEditFieldPosition() {
        return currentEditFieldPosition;
    }

    public void setCurrentEditFieldPosition(int currentEditFieldPosition) {
        this.currentEditFieldPosition = currentEditFieldPosition;
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

    public String getCurrentEditionOption() {
        return currentEditionOption;
    }

    public void setCurrentEditionOption(String currentEditionOption) {
        this.currentEditionOption = currentEditionOption;
    }

    public int getLastMovedFieldPosition() {
        return lastMovedFieldPosition;
    }

    public void setLastMovedFieldPosition(int lastMovedFieldPosition) {
        this.lastMovedFieldPosition = lastMovedFieldPosition;
    }

    public boolean isShowReturnButton() {
        return showReturnButton;
    }

    public void setShowReturnButton(boolean showReturnButton) {
        this.showReturnButton = showReturnButton;
    }

    public String getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode;
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

    public Boolean getShowTemplateEdition() {
        return showTemplateEdition;
    }

    public void setShowTemplateEdition(Boolean showTemplateEdition) {
        this.showTemplateEdition = showTemplateEdition;
    }

    public FieldType getOriginalFieldType() {
        return originalFieldType;
    }

    public void setOriginalFieldType(FieldType originalFieldType) {
        this.originalFieldType = originalFieldType;
    }

    public String getLastDataHolderUsedId() {
        return lastDataHolderUsedId;
    }

    public void setLastDataHolderUsedId(String lastDataHolderUsedId) {
        this.lastDataHolderUsedId = lastDataHolderUsedId;
    }
}