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
package org.jbpm.formModeler.core.processing.fieldHandlers.multipleSubform;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatterTest;
import org.jbpm.formModeler.core.processing.fieldHandlers.mocks.SubFormHelperMock;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PreviewMultipleSubformItemFormatterTest extends DefaultFieldHandlerFormatterTest<PreviewMultipleSubformItemFormatter> {

    protected SubFormHelper helper;

    protected List<String> renderingFragments = new ArrayList<String>();
    {
        renderingFragments.add( "output" );
    }

    @Test
    public void testDefaultRendering() throws Exception {
        testDefaultRendering( false );
    }

    @Test
    public void testDefaultReadonlyRendering() throws Exception {
        testDefaultRendering( true );
    }

    protected void testDefaultRendering( final boolean readonly ) throws Exception {
        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY)).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return readonly;
            }
        });

        currentNamespace += SubFormHelperMock.PREVIEW_FIELD_SUFFIX;

        formatter.service( httpServletRequest, httpServletResponse );

        verify( tag, atLeastOnce() ).addProcessingInstruction(argThat(new FormatterFragmentMatcher(renderingFragments)));
    }

    @Override
    protected void initDependencies() {
        helper = weld.instance().select( SubFormHelper.class ).get();
    }

    @Override
    protected PreviewMultipleSubformItemFormatter getFormatterInstance() {
        PreviewMultipleSubformItemFormatter formatter = new PreviewMultipleSubformItemFormatter();
        formatter.helper = helper;
        return formatter;
    }

    @Override
    protected void mockRequestAttributes() {
        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_VALUE)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new HashMap[]{new HashMap()};
            }
        });
    }

    @Override
    protected Field getMockedField() {
        Field currentField = new Field();
        currentField.setFieldType(fieldTypeManager.getTypeByCode("MultipleSubform"));

        currentField.setFieldName("multipleSubform");

        return currentField;
    }
}
