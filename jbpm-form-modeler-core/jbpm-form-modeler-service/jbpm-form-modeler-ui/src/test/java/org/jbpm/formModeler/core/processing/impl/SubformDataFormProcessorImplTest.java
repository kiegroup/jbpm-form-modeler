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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.fieldHandlers.mocks.SubformFinderServiceMock;
import org.jbpm.formModeler.core.processing.impl.model.Address;
import org.jbpm.formModeler.core.processing.impl.model.Hobby;
import org.jbpm.formModeler.core.processing.impl.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.ADDRESS_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.BIRTHDAY_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.HOBBIES_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.LEVEL_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.NAME_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.NUM_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.PERSON_DATAHOLDER;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.STREET_FIELD;
import static org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator.SURNAME_FIELD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubformDataFormProcessorImplTest extends AbstractFormProcessorImplTest {

    public static final String ORIGINAL_NAME = "Aria";
    public static final String ORIGINAL_SURNAME = "Stark";
    public static final Date ORIGINAL_DATE = new Date(System.currentTimeMillis() - 1500);

    public static final String ORIGINAL_STREET = "Winterfell";
    public static final Integer ORIGINAL_NUMBER = 2;

    public static final String ORIGINAL_HOBBY_NAME = "Kill kill kill!";
    public static final Integer ORIGINAL_HOBBY_LEVEL = 10;

    public static final String EXPECTED_NAME = "John";
    public static final String EXPECTED_SURNAME = "Snow";
    public static final Date EXPECTED_DATE = new Date();

    public static final String EXPECTED_STREET = "Black Castle";
    public static final Integer EXPECTED_NUMBER = 1;

    public static final String EXPECTED_HOBBY1_NAME = "Fighting";
    public static final Integer EXPECTED_HOBBY1_LEVEL = 10;

    public static final String EXPECTED_HOBBY2_NAME = "Return from dead";
    public static final Integer EXPECTED_HOBBY2_LEVEL = 7;

    protected SubformFinderServiceMock subformFinderService;

    private Person expectedPerson;

    private Form addressForm;

    private Form hobbyForm;

    public void init() {
        super.init();

        List<Hobby> hobbyList = new ArrayList<>();

        hobbyList.add(new Hobby(EXPECTED_HOBBY1_NAME,
                                EXPECTED_HOBBY1_LEVEL));
        hobbyList.add(new Hobby(EXPECTED_HOBBY2_NAME,
                                EXPECTED_HOBBY2_LEVEL));

        expectedPerson = new Person(EXPECTED_NAME,
                                    EXPECTED_SURNAME,
                                    EXPECTED_DATE,
                                    new Address(EXPECTED_STREET,
                                                EXPECTED_NUMBER),
                                    hobbyList);

        form = formGenerator.generatePersonForm();

        addressForm = formGenerator.generateAddressForm();

        hobbyForm = formGenerator.generateHobbyForm();

        subformFinderService = weldContainer.instance().select(SubformFinderServiceMock.class).get();
        subformFinderService.addFormContext(form);
        subformFinderService.addForm(addressForm);
        subformFinderService.addForm(hobbyForm);
    }

    @Test
    public void testPersistComplexDataWithoutInputs() throws Exception {
        initContext();

        FormStatusData data = processor.read(CTX_UID);

        assertNotNull(data);

        Object name = data.getCurrentValue(NAME_FIELD);
        assertNull(name);
        Object surname = data.getCurrentValue(SURNAME_FIELD);
        assertNull(surname);
        Object birthday = data.getCurrentValue(BIRTHDAY_FIELD);
        assertNull(birthday);
        Object address = data.getCurrentValue(ADDRESS_FIELD);
        assertNotNull(address);
        assertTrue(address instanceof Map);
        assertTrue(((Map) address).isEmpty());
        Object hobbies = data.getCurrentValue(HOBBIES_FIELD);
        assertNotNull(hobbies);
        assertTrue(hobbies instanceof Map[]);
        assertEquals(0,
                     ((Map[]) hobbies).length);

        when(processor.getFilteredMapRepresentationToPersist(context.getForm(),
                                                             context.getUID())).thenReturn(generateExpectedValuesMap());

        processor.persist(CTX_UID);

        assertNotNull(context.getOutputData().get(PERSON_DATAHOLDER));

        assertTrue(context.getOutputData().get(PERSON_DATAHOLDER) instanceof Person);

        Person person = (Person) context.getOutputData().get(PERSON_DATAHOLDER);

        assertEquals(expectedPerson,
                     person);
    }

    @Test
    public void testPersistComplexDataWithInputs() throws Exception {

        List<Hobby> hobbyList = new ArrayList<>();

        hobbyList.add(new Hobby(ORIGINAL_HOBBY_NAME,
                                ORIGINAL_HOBBY_LEVEL));

        Person originalPerson = new Person(ORIGINAL_NAME,
                                           ORIGINAL_SURNAME,
                                           ORIGINAL_DATE,
                                           new Address(ORIGINAL_STREET,
                                                       ORIGINAL_NUMBER),
                                           hobbyList);

        inputs.put(PERSON_DATAHOLDER,
                   originalPerson);

        initContext();

        FormStatusData data = processor.read(CTX_UID);

        assertNotNull(data);

        Object name = data.getCurrentValue(NAME_FIELD);
        assertNotNull(name);
        assertEquals(ORIGINAL_NAME,
                     name);
        Object surname = data.getCurrentValue(SURNAME_FIELD);
        assertNotNull(surname);
        assertEquals(ORIGINAL_SURNAME,
                     surname);
        Object birthday = data.getCurrentValue(BIRTHDAY_FIELD);
        assertNotNull(birthday);
        assertEquals(ORIGINAL_DATE,
                     birthday);

        // Checking address
        Object address = data.getCurrentValue(ADDRESS_FIELD);
        assertNotNull(address);
        assertTrue(address instanceof Map);
        assertFalse(((Map) address).isEmpty());
        Map addressMap = (Map) address;
        Object street = addressMap.get(STREET_FIELD);
        assertNotNull(street);
        assertEquals(ORIGINAL_STREET,
                     street);
        Object num = addressMap.get(NUM_FIELD);
        assertNotNull(num);
        assertEquals(ORIGINAL_NUMBER,
                     num);

        // Checking hobbies
        Object hobbies = data.getCurrentValue(HOBBIES_FIELD);
        assertNotNull(hobbies);
        assertTrue(hobbies instanceof Map[]);
        assertEquals(1,
                     ((Map[]) hobbies).length);
        Map hobbyMap = ((Map[]) hobbies)[0];
        Object hobbyName = hobbyMap.get(NAME_FIELD);
        assertNotNull(hobbyName);
        assertEquals(ORIGINAL_HOBBY_NAME,
                     hobbyName);
        Object hobbyLevel = hobbyMap.get(LEVEL_FIELD);
        assertNotNull(hobbyLevel);
        assertEquals(ORIGINAL_HOBBY_LEVEL,
                     hobbyLevel);

        when(processor.getFilteredMapRepresentationToPersist(context.getForm(),
                                                             context.getUID())).thenReturn(generateExpectedValuesMap());

        processor.persist(CTX_UID);

        assertNotNull(context.getOutputData().get(PERSON_DATAHOLDER));

        assertTrue(context.getOutputData().get(PERSON_DATAHOLDER) instanceof Person);

        Person person = (Person) context.getOutputData().get(PERSON_DATAHOLDER);

        // originalPerson & person should be the same Object but the values should be equal to the expectedPerson
        assertEquals(originalPerson,
                     person);
        assertEquals(expectedPerson,
                     person);
    }

    protected Map generateExpectedValuesMap() {
        Map<String, Object> expectedPersonMap = new HashMap<>();
        expectedPersonMap.put(NAME_FIELD,
                              EXPECTED_NAME);
        expectedPersonMap.put(SURNAME_FIELD,
                              EXPECTED_SURNAME);
        expectedPersonMap.put(BIRTHDAY_FIELD,
                              EXPECTED_DATE);

        Map<String, Object> expectedAddress = new HashMap<>();
        expectedAddress.put(STREET_FIELD,
                            EXPECTED_STREET);
        expectedAddress.put(NUM_FIELD,
                            EXPECTED_NUMBER);

        expectedPersonMap.put(ADDRESS_FIELD,
                              expectedAddress);

        Map hobby1 = new HashMap<>();
        hobby1.put(NAME_FIELD,
                   EXPECTED_HOBBY1_NAME);
        hobby1.put(LEVEL_FIELD,
                   EXPECTED_HOBBY1_LEVEL);

        Map hobby2 = new HashMap<>();
        hobby2.put(NAME_FIELD,
                   EXPECTED_HOBBY2_NAME);
        hobby2.put(LEVEL_FIELD,
                   EXPECTED_HOBBY2_LEVEL);

        Map[] expectedHobbies = new Map[]{hobby1, hobby2};

        expectedPersonMap.put(HOBBIES_FIELD,
                              expectedHobbies);

        return expectedPersonMap;
    }

}
