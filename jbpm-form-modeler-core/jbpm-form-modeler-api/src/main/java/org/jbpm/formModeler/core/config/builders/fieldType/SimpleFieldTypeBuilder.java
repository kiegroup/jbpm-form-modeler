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

import java.util.ArrayList;
import java.util.List;

public class SimpleFieldTypeBuilder implements FieldTypeBuilder<FieldType> {

    @Override
    public List<FieldType> buildList() {

        List<FieldType> result = new ArrayList<FieldType>();

        FieldType ft = new FieldType();
        ft.setCode("InputText");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextArea");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.TextAreaFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextCharacter");
        ft.setFieldClass("java.lang.Character");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CharacterFieldHandler");
        ft.setMaxlength(new Long(1));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveCharacter");
        ft.setFieldClass("char");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CharacterFieldHandler");
        ft.setMaxlength(new Long(1));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextFloat");
        ft.setFieldClass("java.lang.Float");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveFloat");
        ft.setFieldClass("float");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(50));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextDouble");
        ft.setFieldClass("java.lang.Double");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveDouble");
        ft.setFieldClass("double");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextBigDecimal");
        ft.setFieldClass("java.math.BigDecimal");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextBigInteger");
        ft.setFieldClass("java.math.BigInteger");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextByte");
        ft.setFieldClass("java.lang.Byte");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(4));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveByte");
        ft.setFieldClass("byte");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(4));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextShort");
        ft.setFieldClass("java.lang.Short");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveShort");
        ft.setFieldClass("short");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(6));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextInteger");
        ft.setFieldClass("java.lang.Integer");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveInteger");
        ft.setFieldClass("int");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(11));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextLong");
        ft.setFieldClass("java.lang.Long");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPrimitiveLong");
        ft.setFieldClass("long");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(20));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextEmail");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setPattern("[a-zA-Z0-9.!#$%&'*+-/=?\\^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("CheckBox");
        ft.setFieldClass("java.lang.Boolean");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CheckBoxFieldHandler");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("CheckBoxPrimitiveBoolean");
        ft.setFieldClass("boolean");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CheckBoxFieldHandler");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("HTMLEditor");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.HTMLTextAreaFieldHandler");
        ft.setHeight("200");
        ft.setSize("350");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("I18nHTMLText");
        ft.setFieldClass("org.jbpm.formModeler.core.wrappers.HTMLi18n");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.HTMLi18nFieldHandler");
        ft.setHeight("30");
        ft.setSize("50");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("I18nText");
        ft.setFieldClass("org.jbpm.formModeler.api.model.wrappers.I18nSet");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.I18nSetFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("16");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("I18nTextArea");
        ft.setFieldClass("org.jbpm.formModeler.api.model.wrappers.I18nSet");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.I18nTextAreaFieldHandler");
        ft.setHeight("5");
        ft.setMaxlength(new Long(4000));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputDate");
        ft.setFieldClass("java.util.Date");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.DateFieldHandler");
        ft.setMaxlength(new Long(25));
        ft.setSize("25");
        ft.setPattern("MM-dd-yyyy HH:mm:ss");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputShortDate");
        ft.setFieldClass("java.util.Date");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.ShortDateFieldHandler");
        ft.setMaxlength(new Long(25));
        ft.setSize("25");
        ft.setPattern("MM-dd-yyyy");
        result.add(ft);

        return result;
    }
}
