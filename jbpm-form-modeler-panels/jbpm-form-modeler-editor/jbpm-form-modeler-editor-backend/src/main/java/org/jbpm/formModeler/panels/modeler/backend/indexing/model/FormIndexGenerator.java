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

import org.jbpm.formModeler.panels.modeler.backend.indexing.model.terms.valueterms.ValueFormIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.IndexElementsGenerator;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.ArrayList;
import java.util.List;

public class FormIndexGenerator implements IndexElementsGenerator {

    private ValueFormIndexTerm formTerm;

    public FormIndexGenerator(ValueFormIndexTerm formTerm) {
        this.formTerm = PortablePreconditions.checkNotNull("formTerm", formTerm);
    }

    @Override
    public List<Pair<String, String>> toIndexElements() {
        final List<Pair<String, String>> indexElements = new ArrayList<Pair<String, String>>();
        indexElements.add(new Pair<String, String>(formTerm.getTerm(), formTerm.getValue()));
        return indexElements;
    }
}
