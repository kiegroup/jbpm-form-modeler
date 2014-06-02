/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.jbpm.formModeler.panels.modeler.backend.indexing;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.DataHolderIndexGenerator;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.FieldIndexGenerator;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.FormIndexGenerator;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.valueterms.*;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FormVisitor {
    private final DefaultIndexBuilder builder;
    private final Form form;

    public FormVisitor(DefaultIndexBuilder builder, Form form) {
        this.builder = PortablePreconditions.checkNotNull("builder", builder);
        this.form = PortablePreconditions.checkNotNull("form", form);
    }

    public Set<Pair<String, String>> visit() {
        visit(form);
        return builder.build();
    }

    protected void visit(Form form) {

        builder.addGenerator(new FormIndexGenerator(new ValueFormIndexTerm( form.getName())));

        for (Field field : form.getFormFields()) {
            visit(field);
        }

        for (DataHolder dataHolder : form.getHolders()) {
            visit(dataHolder);
        }
    }

    protected void visit(DataHolder dataHolder) {
        DataHolderIndexGenerator indexGenerator = new DataHolderIndexGenerator(new ValueDataHolderIndexTerm(dataHolder.getUniqeId()), new ValueDataHolderTypeIndexTerm(dataHolder.getInfo()));

        if (dataHolder.canHaveChildren()) {
            List<ValueDataHolderFieldIndexTerm> fieldTerms = new ArrayList<ValueDataHolderFieldIndexTerm>();
            for (DataFieldHolder field : dataHolder.getFieldHolders()) {
                if (form.isFieldBinded(dataHolder, field.getId())) fieldTerms.add(new ValueDataHolderFieldIndexTerm(field.getId()));
            }
            indexGenerator.setDataHolderFieldsIndexTerms(fieldTerms);
        }
        builder.addGenerator(indexGenerator);
    }

    protected void visit(Field field) {
        DataHolder holder = field.getForm().getDataHolderByField(field);

        FieldIndexGenerator indexGenerator;

        if (holder != null) {
            ValueDataHolderIndexTerm holderIndexTerm = new ValueDataHolderIndexTerm(holder.getUniqeId());
            if (holder.canHaveChildren()) {
                String bindingExpression = StringUtils.defaultIfEmpty(field.getInputBinding(), field.getOutputBinding());

                int slash = bindingExpression.indexOf("/");
                String holderFieldId = bindingExpression.substring(slash + 1);
                DataFieldHolder holderField = holder.getDataFieldHolderById(holderFieldId);
                if (holderField != null) {
                    indexGenerator = new FieldIndexGenerator(new ValueFieldIndexTerm(field.getFieldName()), new ValueFieldTypeIndexTerm(holderField.getClassName()), holderIndexTerm, new ValueDataHolderFieldIndexTerm(holderFieldId));
                } else {
                    indexGenerator = new FieldIndexGenerator(new ValueFieldIndexTerm(field.getFieldName()), new ValueFieldTypeIndexTerm(holder.getInfo()), holderIndexTerm);
                }
            } else {
                indexGenerator = new FieldIndexGenerator(new ValueFieldIndexTerm(field.getFieldName()), new ValueFieldTypeIndexTerm(holder.getInfo()), holderIndexTerm);
            }
        } else {
            indexGenerator = new FieldIndexGenerator(new ValueFieldIndexTerm(field.getFieldName()), new ValueFieldTypeIndexTerm(field.getFieldType().getFieldClass()));
        }

        if (!StringUtils.isEmpty(field.getDefaultSubform())) indexGenerator.setDefaultSubform(new ValueSubFormIndexTerm(field.getDefaultSubform()));
        if (!StringUtils.isEmpty(field.getPreviewSubform())) indexGenerator.setPreviewSubform(new ValueSubFormIndexTerm(field.getPreviewSubform()));
        if (!StringUtils.isEmpty(field.getTableSubform())) indexGenerator.setTableSubform(new ValueSubFormIndexTerm(field.getTableSubform()));

        builder.addGenerator(indexGenerator);
    }


}
