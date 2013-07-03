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


import org.jbpm.formModeler.core.config.builders.FieldTypeBuilder;
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
        ft.setCode("InputTextFloat");
        ft.setFieldClass("java.lang.Float");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
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
        ft.setCode("InputTextBigDecimal");
        ft.setFieldClass("java.math.BigDecimal");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
        ft.setSize("25");
        ft.setPattern("#.##");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextShort");
        ft.setFieldClass("java.lang.Short");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
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
        ft.setCode("InputTextLong");
        ft.setFieldClass("java.lang.Long");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.NumericFieldHandler");
        ft.setMaxlength(new Long(100));
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

/*        ft = new FieldType();
        ft.setCode("InputTextCP");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setPattern("[0-9]{5}");
        ft.setMaxlength(new Long(5));
        ft.setSize("5");
        result.add(ft);
*/
        ft = new FieldType();
        ft.setCode("CheckBox");
        ft.setFieldClass("java.lang.Boolean");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.CheckBoxFieldHandler");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("HTMLEditor");
        ft.setFieldClass("org.jbpm.formModeler.core.wrappers.HTMLString");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.HTMLTextAreaFieldHandler");
        ft.setHeight("30");
        ft.setSize("50");
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

        ft = new FieldType();
        ft.setCode("Link");
        ft.setFieldClass("org.jbpm.formModeler.core.wrappers.Link");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.LinkFieldHandler");
        ft.setMaxlength(new Long(4000));
        ft.setSize("30");
        result.add(ft);

 /*       ft = new FieldType();
        ft.setCode("InputTextCCC");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setFormula("=Functions.String.upperCase({$this}.trim())");
        ft.setPattern("=Functions.checkCCC({$this})");
        ft.setMaxlength(new Long(20));
        ft.setSize("25");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextIBAN");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setFormula("=Functions.String.upperCase({$this}.trim())");
        ft.setPattern("=Functions.checkIBAN({$this})");
        ft.setMaxlength(new Long(24));
        ft.setSize("24");
        result.add(ft);

        ft = new FieldType();
        ft.setCode("InputTextPhone");
        ft.setFieldClass("java.lang.String");
        ft.setManagerClass("org.jbpm.formModeler.core.processing.fieldHandlers.InputTextFieldHandler");
        ft.setPattern("[0-9]{9}");
        ft.setMaxlength(new Long(9));
        ft.setSize("13");
        result.add(ft);
*/
        return result;
    }
}
