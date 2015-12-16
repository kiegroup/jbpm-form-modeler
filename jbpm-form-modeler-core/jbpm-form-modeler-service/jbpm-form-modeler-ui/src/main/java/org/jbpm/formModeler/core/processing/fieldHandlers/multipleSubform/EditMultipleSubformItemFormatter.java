/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;

import javax.inject.Named;

@Named("EditMultipleSubformItemFormatter")
public class EditMultipleSubformItemFormatter extends AbstractMultipleSubformItemFormatter {

    @Override
    protected Integer getItemPosition(String namespace) {
        return helper.getEditFieldPosition( namespace );
    }

    @Override
    protected Form getForm(Field field, String currentNamespace) {
        CreateDynamicObjectFieldHandler fieldHandler = (CreateDynamicObjectFieldHandler) getFieldHandlersManager().getHandler(field.getFieldType());
        return fieldHandler.getEditForm(field, currentNamespace);
    }
}
