/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

public class DecoratorFieldTypeBuilder implements FieldTypeBuilder<FieldType> {
    @Override
    public List<FieldType> buildList() {
        List<FieldType> result = new ArrayList<FieldType>();

        FieldType ft = new FieldType();
        ft.setCode("HTMLLabel");
        ft.setFieldClass("HTMLlabel");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.formDecorators.HTMLlabel");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("Separator");
        ft.setFieldClass("Separator");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.formDecorators.Separator");
        result.add(ft);

        return result;
    }
}
