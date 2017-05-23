/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.formModeler.core.processing.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleDataFormProcessorImplTest extends AbstractFormProcessorImplTest {

    public static final String ORIGINAL_TEXT = "originalText";
    public static final Integer ORIGINAL_INTEGER = 0;
    public static final Date ORIGINAL_DATE = new Date(System.currentTimeMillis() - 150000);

    public static final String EXPECTED_TEXT = "expectedText";
    public static final Integer EXPECTED_INTEGER = 1234567890;
    public static final Date EXPECTED_DATE = new Date();

    @Test
    public void testPersistDataWithoutInputs() throws Exception {

        Map<String, Object> modifiedValues = new HashMap<>();
        modifiedValues.put(TestFormGenerator.TEXT_FIELD,
                           EXPECTED_TEXT);
        modifiedValues.put(TestFormGenerator.INT_FIELD,
                           EXPECTED_INTEGER);
        modifiedValues.put(TestFormGenerator.DATE_FIELD,
                           EXPECTED_DATE);

        doTest(new HashMap<String, Object>(),
               modifiedValues);
    }

    @Test
    public void testPersistDataWithInputs() throws Exception {

        Map<String, Object> originalValues = new HashMap<>();
        originalValues.put(TestFormGenerator.TEXT_FIELD,
                           ORIGINAL_TEXT);
        originalValues.put(TestFormGenerator.INT_FIELD,
                           ORIGINAL_INTEGER);
        originalValues.put(TestFormGenerator.DATE_FIELD,
                           ORIGINAL_DATE);

        Map<String, Object> modifiedValues = new HashMap<>();
        modifiedValues.put(TestFormGenerator.TEXT_FIELD,
                           EXPECTED_TEXT);
        modifiedValues.put(TestFormGenerator.INT_FIELD,
                           EXPECTED_INTEGER);
        modifiedValues.put(TestFormGenerator.DATE_FIELD,
                           EXPECTED_DATE);

        doTest(originalValues,
               modifiedValues);
    }

    protected void doTest(Map<String, Object> inputs,
                          Map<String, Object> modifiedValues) throws Exception {

        this.inputs.putAll(inputs);

        form = formGenerator.generateBasicForm();

        initContext();

        FormStatusData data = processor.read(CTX_UID);

        assertNotNull(data);

        for (Field field : form.getFormFields()) {
            assertEquals(inputs.get(field.getFieldName()),
                         data.getCurrentValue(field.getFieldName()));
        }

        when(processor.getFilteredMapRepresentationToPersist(context.getForm(),
                                                             context.getUID())).thenReturn(modifiedValues);

        processor.persist(CTX_UID);

        for (Field field : form.getFormFields()) {
            Object value = context.getOutputData().get(field.getFieldName());
            assertNotNull(value);
            assertEquals(modifiedValues.get(field.getFieldName()),
                         value);
        }
    }
}
