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
package org.jbpm.formModeler.core.processing.mocks;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.formProcessing.FormChangeResponse;
import org.jbpm.formModeler.core.processing.formProcessing.FormulasCalculatorChangeProcessor;

import javax.enterprise.inject.Specializes;

@Specializes
public class FormulasCalculatorChangeProcessorMock extends FormulasCalculatorChangeProcessor {
    @Override
    public FormChangeResponse process(Form form, String namespace, FormChangeResponse response) {
        return response;
    }
}
