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

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.PropertyDefinition;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Named("WysiwygFieldsFormatter")
public class WysiwygFieldsFormatter extends Formatter {

    @Inject
    private Log log;


    public FieldHandlersManager getFieldHandlersManager() {
        return FormProcessingServices.lookup().getFieldHandlersManager();
    }

    public FieldTypeManager getFieldTypesManager() {
        return FormCoreServices.lookup().getFieldTypeManager();
    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            renderAvailableFields();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    protected void renderAvailableFields() throws Exception {

        renderDecorators();
        renderComplexTypes();
        renderSeparator();
        renderPrimitiveTypes();
        renderSeparator();
    }


    protected void renderSeparator() {
        renderFragment("separator");
    }

    protected void renderComplexTypes() {
        List<FieldType> complexTypes = getFieldTypesManager().getFormComplexTypes();
        if (complexTypes.size() > 0) {
            renderFragment("complexStart");
            for (int i = 0; i < complexTypes.size(); i++) {
                FieldType type = (FieldType) complexTypes.get(i);
                setAttribute("complex", type);
                setAttribute("complexId", type.getCode());
                setAttribute("iconUri", getFieldTypesManager().getIconPathForCode(type.getCode()));
                setAttribute("position", i);
                renderFragment("outputComplex");
            }
            renderFragment("complexEnd");
        }
    }

    protected void renderDecorators() throws Exception {
        List decorators = getFieldTypesManager().getFormDecoratorTypes();
        if (decorators.size() > 0) {
            renderFragment("decoratorsStart");
            for (int i = 0; i < decorators.size(); i++) {
                FieldType type = (FieldType) decorators.get(i);
                setAttribute("decorator", type);
                setAttribute("decoratorId", type.getCode());
                setAttribute("iconUri", getFieldTypesManager().getIconPathForCode(type.getCode()));
                setAttribute("position", i);
                renderFragment("outputDecorator");
            }
            renderFragment("decoratorsEnd");
        }
    }

    protected void renderPrimitiveTypes() throws Exception {

        List fieldTypes = getFieldTypesManager().getFieldTypes();

        for (int j = 0; j < fieldTypes.size(); j++) {
            FieldType type = (FieldType) fieldTypes.get(j);
            if(getFieldTypesManager().isDisplayableType(type.getCode())){
                setAttribute("typeName", type.getCode());
                setAttribute("iconUri", getFieldTypesManager().getIconPathForCode(type.getCode()));
                setAttribute("uid", "primitive_" + j);
                setAttribute("typeId", type.getCode());
                renderFragment("outputType");
            }
        }
    }

    protected List<FieldType> getTypesForManager(String managerClass) throws Exception {
        return getFieldTypesManager().getSuitableFieldTypes(managerClass);
    }
}
