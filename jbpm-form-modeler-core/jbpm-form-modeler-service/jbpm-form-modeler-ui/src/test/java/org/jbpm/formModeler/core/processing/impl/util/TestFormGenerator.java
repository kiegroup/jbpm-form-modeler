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

package org.jbpm.formModeler.core.processing.impl.util;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.model.BasicTypeDataHolder;
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.jbpm.formModeler.core.processing.impl.model.Address;
import org.jbpm.formModeler.core.processing.impl.model.Hobby;
import org.jbpm.formModeler.core.processing.impl.model.Person;

public class TestFormGenerator {

    // Basic Form
    public static final Long BASIC_FORM_ID = 3L;
    public static final String TEXT_FIELD = "text";
    public static final String INT_FIELD = "int";
    public static final String DATE_FIELD = "date";

    // Person Form
    public static final Long PERSON_FORM_ID = 0L;
    public static final String PERSON_DATAHOLDER = "person";
    public static final String NAME_FIELD = "name";
    public static final String SURNAME_FIELD = "surname";
    public static final String BIRTHDAY_FIELD = "birthday";
    public static final String ADDRESS_FIELD = "address";
    public static final String HOBBIES_FIELD = "hobbies";

    // Address Form
    public static final Long ADDRESS_FORM_ID = 1L;
    public static final String ADDRESS_DATAHOLDER = "address";
    public static final String STREET_FIELD = "street";
    public static final String NUM_FIELD = "num";

    // Hobby Form
    public static final String HOBBY_DATAHOLDER = "hobby";
    public static final Long HOBBY_FORM_ID = 2L;
    public static final String LEVEL_FIELD = "level";

    //Multiple SubForm form
    public static final Long MULTIPLE_SUB_FORM_ID = 4L;
    public static final String HOBBIES_DATA_HOLDER = "hobbies";

    private FieldTypeManager fieldTypeManager;

    public TestFormGenerator(FieldTypeManager fieldTypeManager) {
        this.fieldTypeManager = fieldTypeManager;
    }

    public Form generateBasicForm() {
        Form form = new Form();
        form.setName(Person.class.getName());
        form.setId(BASIC_FORM_ID);

        form.getFormFields().add(getSimpleField(TEXT_FIELD,
                                                TEXT_FIELD,
                                                String.class.getName(),
                                                form));
        form.getFormFields().add(getSimpleField(INT_FIELD,
                                                INT_FIELD,
                                                Integer.class.getName(),
                                                form));
        form.getFormFields().add(getSimpleField(DATE_FIELD,
                                                DATE_FIELD,
                                                Date.class.getName(),
                                                form));

        form.setDataHolder(new BasicTypeDataHolder(TEXT_FIELD,
                                                   TEXT_FIELD,
                                                   TEXT_FIELD,
                                                   String.class.getName(),
                                                   ""));
        form.setDataHolder(new BasicTypeDataHolder(INT_FIELD,
                                                   INT_FIELD,
                                                   INT_FIELD,
                                                   Integer.class.getName(),
                                                   ""));
        form.setDataHolder(new BasicTypeDataHolder(DATE_FIELD,
                                                   DATE_FIELD,
                                                   DATE_FIELD,
                                                   Date.class.getName(),
                                                   ""));

        return form;
    }

    public Form generatePersonForm() {
        Form form = new Form();
        form.setName(Person.class.getName());
        form.setId(PERSON_FORM_ID);

        form.getFormFields().add(getSimpleField(NAME_FIELD,
                                                PERSON_DATAHOLDER + "/" + NAME_FIELD,
                                                String.class.getName(),
                                                form));
        form.getFormFields().add(getSimpleField(SURNAME_FIELD,
                                                PERSON_DATAHOLDER + "/" + SURNAME_FIELD,
                                                String.class.getName(),
                                                form));
        form.getFormFields().add(getSimpleField(BIRTHDAY_FIELD,
                                                PERSON_DATAHOLDER + "/" + BIRTHDAY_FIELD,
                                                Date.class.getName(),
                                                form));

        Field address = getComplexField(ADDRESS_FIELD,
                                        PERSON_DATAHOLDER + "/" + ADDRESS_FIELD,
                                        Object.class.getName(),
                                        form);
        address.getFieldType().setFieldClass(Address.class.getName());
        address.setDefaultSubform(ADDRESS_FORM_ID.toString());
        form.getFormFields().add(address);

        Field hobbies = getComplexField(HOBBIES_FIELD,
                                        PERSON_DATAHOLDER + "/" + HOBBIES_FIELD,
                                        List.class.getName(),
                                        form);
        hobbies.setBag(Hobby.class.getName());
        hobbies.setDefaultSubform(HOBBY_FORM_ID.toString());

        form.getFormFields().add(hobbies);
        form.setDataHolder(new PojoDataHolder(PERSON_DATAHOLDER,
                                              PERSON_DATAHOLDER,
                                              PERSON_DATAHOLDER,
                                              Person.class.getName(),
                                              ""));

        return form;
    }

    public Form generateHobbyForm() {
        Form form = new Form();
        form.setName(Hobby.class.getName());
        form.setId(HOBBY_FORM_ID);

        form.getFormFields().add(getSimpleField(NAME_FIELD,
                                                HOBBY_DATAHOLDER + "/" + NAME_FIELD,
                                                String.class.getName(),
                                                form));
        form.getFormFields().add(getSimpleField(LEVEL_FIELD,
                                                HOBBY_DATAHOLDER + "/" + LEVEL_FIELD,
                                                Integer.class.getName(),
                                                form));
        form.setDataHolder(new PojoDataHolder(HOBBY_DATAHOLDER,
                                              HOBBY_DATAHOLDER,
                                              HOBBY_DATAHOLDER,
                                              Hobby.class.getName(),
                                              ""));

        return form;
    }

    public Form generateAddressForm() {
        Form form = new Form();
        form.setName(Address.class.getName());
        form.setId(ADDRESS_FORM_ID);

        form.getFormFields().add(getSimpleField(STREET_FIELD,
                                                ADDRESS_DATAHOLDER + "/" + STREET_FIELD,
                                                String.class.getName(),
                                                form));
        form.getFormFields().add(getSimpleField(NUM_FIELD,
                                                ADDRESS_DATAHOLDER + "/" + NUM_FIELD,
                                                Integer.class.getName(),
                                                form));

        form.setDataHolder(new PojoDataHolder(ADDRESS_DATAHOLDER,
                                              ADDRESS_DATAHOLDER,
                                              ADDRESS_DATAHOLDER,
                                              Address.class.getName(),
                                              ""));

        return form;
    }

    public Form generateMultipleSubformForm() {
        Form form = new Form();
        form.setName(List.class.getName());
        form.setId(MULTIPLE_SUB_FORM_ID);

        Field hobbies = getComplexField(HOBBIES_FIELD,
                                        HOBBIES_FIELD,
                                        List.class.getName(),
                                        form);
        hobbies.setBag(Hobby.class.getName());
        hobbies.setDefaultSubform(HOBBY_FORM_ID.toString());

        form.getFormFields().add(hobbies);

        form.setDataHolder(new BasicTypeDataHolder(HOBBIES_DATA_HOLDER,
                                                   HOBBIES_DATA_HOLDER,
                                                   HOBBIES_DATA_HOLDER,
                                                   List.class.getName(),
                                                   ""));

        return form;
    }

    private Field getSimpleField(String name,
                                 String binding,
                                 String className,
                                 Form form) {
        FieldType type = fieldTypeManager.getSimpleTypeByClass(className);

        return getFieldFor(name,
                           binding,
                           type,
                           form);
    }

    private Field getComplexField(String name,
                                  String binding,
                                  String className,
                                  Form form) {
        FieldType type = fieldTypeManager.getComplexTypeByClass(className);
        return getFieldFor(name,
                           binding,
                           type,
                           form);
    }

    private Field getFieldFor(String name,
                              String binding,
                              FieldType type,
                              Form form) {

        Random randomGenerator = new Random();

        Field field = new Field();

        field.setId(randomGenerator.nextInt(1000) * System.currentTimeMillis());

        field.setFieldName(name);
        field.setInputBinding(binding);
        field.setOutputBinding(binding);
        field.setFieldType(type);
        field.setForm(form);
        field.setPosition(form.getFormFields().size());
        return field;
    }
}
