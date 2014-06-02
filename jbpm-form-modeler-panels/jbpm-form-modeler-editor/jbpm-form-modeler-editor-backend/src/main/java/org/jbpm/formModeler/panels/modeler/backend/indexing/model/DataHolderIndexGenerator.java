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

import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.valueterms.ValueDataHolderFieldIndexTerm;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.valueterms.ValueDataHolderIndexTerm;
import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.valueterms.ValueDataHolderTypeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.IndexElementsGenerator;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.ArrayList;
import java.util.List;

public class DataHolderIndexGenerator implements IndexElementsGenerator {
    private ValueDataHolderIndexTerm dataHolderIndexTerm;
    private ValueDataHolderTypeIndexTerm dataHolderTypeIndexTerm;
    private List<ValueDataHolderFieldIndexTerm> dataHolderFieldsIndexTerms;

    public DataHolderIndexGenerator(ValueDataHolderIndexTerm dataHolderIndexTerm, ValueDataHolderTypeIndexTerm dataHolderTypeIndexTerm) {
        this.dataHolderIndexTerm = PortablePreconditions.checkNotNull("dataHolderIndexTerm", dataHolderIndexTerm);
        this.dataHolderTypeIndexTerm = PortablePreconditions.checkNotNull("dataHolderTypeIndexTerm", dataHolderTypeIndexTerm);
    }

    public void setDataHolderFieldsIndexTerms(List<ValueDataHolderFieldIndexTerm> dataHolderFieldsIndexTerms) {
        this.dataHolderFieldsIndexTerms = dataHolderFieldsIndexTerms;
    }

    @Override
    public List<Pair<String, String>> toIndexElements() {
        final List<Pair<String, String>> indexElements = new ArrayList<Pair<String, String>>();
        indexElements.add(new Pair<String, String>(dataHolderIndexTerm.getTerm(), dataHolderIndexTerm.getValue()));

        String root = dataHolderIndexTerm.getTerm() + ":" + dataHolderIndexTerm.getValue() + ":";

        indexElements.add(new Pair<String, String>(root + dataHolderTypeIndexTerm.getTerm(), dataHolderTypeIndexTerm.getValue()));

        if (dataHolderFieldsIndexTerms != null) {
            root += dataHolderTypeIndexTerm.getValue() + ":";
            for (ValueDataHolderFieldIndexTerm field : dataHolderFieldsIndexTerms) {
                indexElements.add(new Pair<String, String>(root + field.getTerm(), field.getValue()));
            }
        }

        return indexElements;
    }
}
