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
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.processing.BindingManager;
import org.jbpm.formModeler.api.processing.FieldHandler;
import org.jbpm.formModeler.core.config.FieldTypeManagerImpl;
import org.jbpm.formModeler.core.processing.BindingManagerImpl;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class BindingFormFormatter extends Formatter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(BindingFormFormatter.class.getName());

    private WysiwygFormEditor wysiwygFormEditor;


    public WysiwygFormEditor getWysiwygFormEditor() {
        return wysiwygFormEditor;
    }

    public void setWysiwygFormEditor(WysiwygFormEditor editor) {
        this.wysiwygFormEditor = editor;
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
        if(WysiwygFormEditor.EDITION_OPTION_BINDINGS_FIELDS.equals(wysiwygFormEditor.getCurrentEditionOption())){
            renderPendingFields();
        }else {
            renderBindingSources();
        }
        }catch (Exception e){
            log.error(" BindingFormFormatter rendering error");
        }
    }

    public void renderBindingSources(){

        try {
            renderFragment("outputStart");

            renderFragment("outputNameInput");

            renderFragment("outputStartBindings");

            Form form = wysiwygFormEditor.getCurrentForm();

            Set<BindingSource> bindings =form.getBindingSources();
            for (BindingSource bindingSource : bindings) {
                setAttribute("id",bindingSource.getId() );
                setAttribute("type",bindingSource.getBindingType() );
                setAttribute("value",bindingSource.getBindingStr() );
                renderFragment("outputBindings");
            }
            renderFragment("outputEndBindings");

            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error:", e);
        }

    }

    public void renderPendingFields() throws Exception {
        Form form = wysiwygFormEditor.getCurrentForm();
        Set<BindingSource> bindings =form.getBindingSources();
        BindingManager bindingManager = wysiwygFormEditor.getBindingManager();
        FieldTypeManager fieldTypeManager = wysiwygFormEditor.getFieldTypesManager();

        renderFragment("outputStart");

        renderFragment("outputStartBindings");

        for (BindingSource bindingSource : bindings) {
            setAttribute("id",bindingSource.getId() );
            setAttribute("type",bindingSource.getBindingType() );
            setAttribute("value",bindingSource.getBindingStr() );
            renderFragment("outputBinding");
            Map allBindingSrcPropNames = bindingManager.getBindingFields(bindingSource);

            String fieldName = "";
            int i=0;
            for (Iterator it = allBindingSrcPropNames.keySet().iterator(); it.hasNext(); ) {
                fieldName = (String) it.next();
                if(fieldName!=null && form.getField(bindingSource.getId()+"_"+fieldName)==null){
                    if(i==0){//first field
                        renderFragment("firstField");
                    }
                    renderAddField(bindingSource.getId() + "_" + fieldName, fieldTypeManager.getTypeByClass(((Class) allBindingSrcPropNames.get(fieldName)).getName()), bindingSource.getId());
                }
            }
            if(i!=0){//last field of list
                renderFragment("lastField");
            }

        }
        renderFragment("outputEndBindings");
        renderFragment("outputEnd");
    }
    public void renderAddField(String fieldName, FieldType type,String bindingId){
            setAttribute("typeName", type.getCode());
            setAttribute("bindingId", bindingId);
            setAttribute("iconUri", wysiwygFormEditor.getFieldTypesManager().getIconPathForCode(type.getCode()));
            setAttribute("fieldName", fieldName);
            renderFragment("outputField");
    }




}
