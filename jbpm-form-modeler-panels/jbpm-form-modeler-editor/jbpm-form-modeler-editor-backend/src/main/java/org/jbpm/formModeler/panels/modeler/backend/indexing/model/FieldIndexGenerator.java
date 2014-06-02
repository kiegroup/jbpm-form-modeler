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
package org.jbpm.formModeler.panels.modeler.backend.indexing.model;

import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.valueterms.*;
import org.kie.workbench.common.services.refactoring.model.index.IndexElementsGenerator;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.ArrayList;
import java.util.List;

public class FieldIndexGenerator implements IndexElementsGenerator {
    private ValueFieldIndexTerm fieldTerm;
    private ValueFieldTypeIndexTerm fieldTypeTerm;
    private ValueDataHolderIndexTerm dataHolderIndexTerm;
    private ValueDataHolderFieldIndexTerm dataHolderFieldIndexTerm;
    private ValueSubFormIndexTerm defaultSubform;
    private ValueSubFormIndexTerm previewSubform;
    private ValueSubFormIndexTerm tableSubform;

    public FieldIndexGenerator(ValueFieldIndexTerm fieldTerm, ValueFieldTypeIndexTerm fieldTypeTerm) {
        this.fieldTerm = PortablePreconditions.checkNotNull("fieldTerm", fieldTerm);
        this.fieldTypeTerm = PortablePreconditions.checkNotNull("fieldTypeTerm", fieldTypeTerm);
    }

    public FieldIndexGenerator(ValueFieldIndexTerm fieldTerm, ValueFieldTypeIndexTerm fieldTypeTerm, ValueDataHolderIndexTerm dataHolderIndexTerm) {
        this.fieldTerm = PortablePreconditions.checkNotNull("fieldTerm", fieldTerm);
        this.fieldTypeTerm = PortablePreconditions.checkNotNull("fieldTypeTerm", fieldTypeTerm);
        this.dataHolderIndexTerm = PortablePreconditions.checkNotNull("dataHolderIndexTerm", dataHolderIndexTerm);
    }

    public FieldIndexGenerator(ValueFieldIndexTerm fieldTerm, ValueFieldTypeIndexTerm fieldTypeTerm, ValueDataHolderIndexTerm dataHolderIndexTerm, ValueDataHolderFieldIndexTerm dataHolderFieldIndexTerm) {
        this.fieldTerm = PortablePreconditions.checkNotNull("fieldTerm", fieldTerm);
        this.fieldTypeTerm = PortablePreconditions.checkNotNull("fieldTypeTerm", fieldTypeTerm);
        this.dataHolderIndexTerm = PortablePreconditions.checkNotNull("dataHolderIndexTerm", dataHolderIndexTerm);
        this.dataHolderFieldIndexTerm = PortablePreconditions.checkNotNull("dataHolderFieldIndexTerm", dataHolderFieldIndexTerm);
    }

    public void setDataHolderIndexTerm(ValueDataHolderIndexTerm dataHolderIndexTerm) {
        this.dataHolderIndexTerm = dataHolderIndexTerm;
    }

    public void setDataHolderFieldIndexTerm(ValueDataHolderFieldIndexTerm dataHolderFieldIndexTerm) {
        this.dataHolderFieldIndexTerm = dataHolderFieldIndexTerm;
    }

    public void setDefaultSubform(ValueSubFormIndexTerm defaultSubform) {
        this.defaultSubform = defaultSubform;
    }

    public void setPreviewSubform(ValueSubFormIndexTerm previewSubform) {
        this.previewSubform = previewSubform;
    }

    public void setTableSubform(ValueSubFormIndexTerm tableSubform) {
        this.tableSubform = tableSubform;
    }

    @Override
    public List<Pair<String, String>> toIndexElements() {
        final List<Pair<String, String>> indexElements = new ArrayList<Pair<String, String>>();
        indexElements.add(new Pair<String, String>(fieldTerm.getTerm(), fieldTerm.getValue()));

        String root = fieldTerm.getTerm() + ":" + fieldTerm.getValue() + ":";

        if (fieldTypeTerm != null) indexElements.add(new Pair<String, String>(root + fieldTypeTerm.getTerm(), fieldTypeTerm.getValue()));
        if (dataHolderIndexTerm != null) {
            indexElements.add(new Pair<String, String>(root + dataHolderIndexTerm.getTerm(), dataHolderIndexTerm.getValue()));
            if (dataHolderFieldIndexTerm != null) {
                indexElements.add(new Pair<String, String>(root + dataHolderFieldIndexTerm.getTerm(), dataHolderFieldIndexTerm.getValue()));
            }
        }
        if (defaultSubform != null) indexElements.add(new Pair<String, String>(root + defaultSubform.getTerm(), defaultSubform.getValue()));
        if (previewSubform != null) indexElements.add(new Pair<String, String>(root + previewSubform.getTerm(), previewSubform.getValue()));
        if (tableSubform != null) indexElements.add(new Pair<String, String>(root + tableSubform.getTerm(), tableSubform.getValue()));
        return indexElements;
    }
}
