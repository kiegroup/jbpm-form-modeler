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
import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PlugableFieldTypeBuilder extends SimpleFieldTypeBuilder {
    @Inject
    protected Instance<PlugableFieldType> plugableFieldTypes;

    public List<FieldType> fieldTypes;

    @PostConstruct
    public void initList() {
        fieldTypes = new ArrayList<FieldType>();

        for (PlugableFieldType fieldType : plugableFieldTypes) {
            fieldTypes.add(fieldType);
        }
    }

    @Override
    public List<FieldType> buildList() {
        return fieldTypes;
    }
}
