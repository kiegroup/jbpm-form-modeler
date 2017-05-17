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

package org.jbpm.formModeler.core.processing.fieldHandlers.date;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DateFieldHandlerFormatterWithDefaultValueTest extends AbstractDateFieldHandlerFormatterTest<DateFieldHandler> {

    public static final Date DEFAULT_DATE = new Date();

    @Override
    public void setup() {
        super.setup();

        SimpleDateFormat sdf = new SimpleDateFormat(fieldHandler.getDefaultPattern(),
                                                    localeManager.getCurrentLocale());
        tagMatcher.addParam("value",
                            sdf.format(DEFAULT_DATE));
    }

    @Override
    protected DateFieldHandler getHandler() {
        return new DateFieldHandler();
    }

    @Test
    public void testRendering() throws Exception {
        formatter.service(httpServletRequest,
                          httpServletResponse);

        verify(tag,
               atLeastOnce()).addProcessingInstruction(argThat(tagMatcher));
    }

    @Override
    protected void mockRequestAttributes() {
        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_VALUE)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return DEFAULT_DATE;
            }
        });
    }

    @Override
    protected Field getMockedField() {
        Field currentField = new Field();
        currentField.setFieldType(fieldTypeManager.getTypeByCode("InputDate"));

        currentField.setFieldName("date");
        currentField.setDefaultValueFormula("= new java.util.Date()");

        return currentField;
    }
}
