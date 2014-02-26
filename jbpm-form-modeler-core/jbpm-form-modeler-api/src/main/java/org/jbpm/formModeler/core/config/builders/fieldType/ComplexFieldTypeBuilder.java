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
package org.jbpm.formModeler.core.config.builders.fieldType;


import org.jbpm.formModeler.api.model.FieldType;

import java.util.ArrayList;
import java.util.List;

public class ComplexFieldTypeBuilder implements FieldTypeBuilder<FieldType> {

    @Override
    public List<FieldType> buildList() {

        List<FieldType> result = new ArrayList<FieldType>();

        FieldType ft = new FieldType();
        ft.setCode("Subform");
        ft.setFieldClass("java.lang.Object");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.SubformFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("MultipleSubform");
        ft.setFieldClass("java.util.List");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CreateDynamicObjectFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        return result;
    }
}
