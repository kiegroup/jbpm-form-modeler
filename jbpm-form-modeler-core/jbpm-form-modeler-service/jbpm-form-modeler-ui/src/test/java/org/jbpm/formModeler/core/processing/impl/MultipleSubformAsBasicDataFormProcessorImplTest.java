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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.fieldHandlers.mocks.SubformFinderServiceMock;
import org.jbpm.formModeler.core.processing.impl.model.Hobby;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.HOBBIES_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.LEVEL_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.NAME_FIELD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultipleSubformAsBasicDataFormProcessorImplTest extends AbstractFormProcessorImplTest {

    public static final String ORIGINAL_HOBBY1_NAME = "Astronomy";
    public static final Integer ORIGINAL_HOBBY1_LEVEL = 3;

    public static final String ORIGINAL_HOBBY2_NAME = "Running";
    public static final Integer ORIGINAL_HOBBY2_LEVEL = 6;

    public static final String EXPECTED_HOBBY1_NAME = "Playing Guitar";
    public static final Integer EXPECTED_HOBBY1_LEVEL = 10;

    public static final String EXPECTED_HOBBY2_NAME = "Biking";
    public static final Integer EXPECTED_HOBBY2_LEVEL = 7;

    public static final String EXPECTED_HOBBY3_NAME = "Coding Java";
    public static final Integer EXPECTED_HOBBY3_LEVEL = 3;

    private List<Hobby> expectedHobbies = new ArrayList<>();

    protected SubformFinderServiceMock subformFinderService;

    protected Form hobbyForm;

    @Override
    public void init() {
        super.init();

        expectedHobbies.add(new Hobby(EXPECTED_HOBBY1_NAME,
                                      EXPECTED_HOBBY1_LEVEL));
        expectedHobbies.add(new Hobby(EXPECTED_HOBBY2_NAME,
                                      EXPECTED_HOBBY2_LEVEL));
        expectedHobbies.add(new Hobby(EXPECTED_HOBBY3_NAME,
                                      EXPECTED_HOBBY3_LEVEL));

        form = formGenerator.generateMultipleSubformForm();
        hobbyForm = formGenerator.generateHobbyForm();

        subformFinderService = weldContainer.instance().select(SubformFinderServiceMock.class).get();
        subformFinderService.addFormContext(form);
        subformFinderService.addForm(hobbyForm);
    }

    @Test
    public void testPersistDataWithoutInputs() throws Exception {
        initContext();

        FormStatusData data = processor.read(CTX_UID);

        assertNotNull(data);

        assertEquals(1,
                     data.getCurrentValues().size());

        Object actualHobbies = data.getCurrentValue(HOBBIES_FIELD);
        assertNotNull(actualHobbies);
        assertTrue(actualHobbies instanceof Map[]);
        assertEquals(0,
                     ((Map[]) actualHobbies).length);

        when(processor.getFilteredMapRepresentationToPersist(context.getForm(),
                                                             context.getUID())).thenReturn(generateExpectedValuesMap());

        processor.persist(CTX_UID);

        assertNotNull(context.getOutputData().get(HOBBIES_FIELD));
        assertTrue(context.getOutputData().get(HOBBIES_FIELD) instanceof List);

        List<Hobby> resultHobbies = (List<Hobby>) context.getOutputData().get(HOBBIES_FIELD);
        assertFalse(resultHobbies.isEmpty());
        assertEquals(expectedHobbies.size(),
                     resultHobbies.size());
        assertEquals(expectedHobbies,
                     resultHobbies);
    }

    @Test
    public void testPersistDataWithInputs() throws Exception {

        List<Hobby> originalHobbies = new ArrayList<>();
        originalHobbies.add(new Hobby(ORIGINAL_HOBBY1_NAME,
                                      ORIGINAL_HOBBY1_LEVEL));
        originalHobbies.add(new Hobby(ORIGINAL_HOBBY2_NAME,
                                      ORIGINAL_HOBBY2_LEVEL));

        inputs.put(HOBBIES_FIELD,
                   originalHobbies);

        initContext();

        FormStatusData data = processor.read(CTX_UID);

        assertNotNull(data);

        assertEquals(1,
                     data.getCurrentValues().size());

        Object actualHobbies = data.getCurrentValue(HOBBIES_FIELD);
        assertNotNull(actualHobbies);
        assertTrue(actualHobbies instanceof Map[]);
        assertEquals(2,
                     ((Map[]) actualHobbies).length);

        Map[] actualHobbiesMap = (Map[]) actualHobbies;
        Map hobby1Map = actualHobbiesMap[0];
        assertEquals(ORIGINAL_HOBBY1_NAME,
                     hobby1Map.get(NAME_FIELD));
        assertEquals(ORIGINAL_HOBBY1_LEVEL,
                     hobby1Map.get(LEVEL_FIELD));
        Map hobby2Map = actualHobbiesMap[1];
        assertEquals(ORIGINAL_HOBBY2_NAME,
                     hobby2Map.get(NAME_FIELD));
        assertEquals(ORIGINAL_HOBBY2_LEVEL,
                     hobby2Map.get(LEVEL_FIELD));

        when(processor.getFilteredMapRepresentationToPersist(context.getForm(),
                                                             context.getUID())).thenReturn(generateExpectedValuesMap());

        processor.persist(CTX_UID);

        assertNotNull(context.getOutputData().get(HOBBIES_FIELD));
        assertTrue(context.getOutputData().get(HOBBIES_FIELD) instanceof List);

        List<Hobby> resultHobbies = (List<Hobby>) context.getOutputData().get(HOBBIES_FIELD);
        assertFalse(resultHobbies.isEmpty());
        assertEquals(expectedHobbies.size(),
                     resultHobbies.size());

        assertEquals(expectedHobbies,
                     resultHobbies);
    }

    private Map generateExpectedValuesMap() {
        Map<String, Object> modifiedValues = new HashMap<>();

        Map<String, Object> hobby1 = new HashMap<>();
        hobby1.put(NAME_FIELD,
                   EXPECTED_HOBBY1_NAME);
        hobby1.put(LEVEL_FIELD,
                   EXPECTED_HOBBY1_LEVEL);

        Map<String, Object> hobby2 = new HashMap<>();
        hobby2.put(NAME_FIELD,
                   EXPECTED_HOBBY2_NAME);
        hobby2.put(LEVEL_FIELD,
                   EXPECTED_HOBBY2_LEVEL);

        Map<String, Object> hobby3 = new HashMap<>();
        hobby3.put(NAME_FIELD,
                   EXPECTED_HOBBY3_NAME);
        hobby3.put(LEVEL_FIELD,
                   EXPECTED_HOBBY3_LEVEL);

        modifiedValues.put(HOBBIES_FIELD,
                           new Map[]{hobby1, hobby2, hobby3});

        return modifiedValues;
    }
}
