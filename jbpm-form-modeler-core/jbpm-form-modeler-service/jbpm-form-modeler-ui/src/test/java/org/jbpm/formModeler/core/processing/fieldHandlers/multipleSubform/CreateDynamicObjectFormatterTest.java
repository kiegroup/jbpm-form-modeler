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
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatterTest;
import org.jbpm.formModeler.core.processing.fieldHandlers.mocks.SubFormHelperMock;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.core.processing.formRendering.FieldI18nResourceObtainer;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateDynamicObjectFormatterTest extends DefaultFieldHandlerFormatterTest<CreateDynamicObjectFieldFormatter> {

    protected SubFormHelper helper;
    protected FieldI18nResourceObtainer fieldI18nResourceObtainer;
    protected List<String> fullRenderingFragments = new ArrayList<String>();
    protected List<String> previewRenderingFragments = new ArrayList<String>();
    protected List<String> editRenderingFragments = new ArrayList<String>();

    {
        fullRenderingFragments.add("outputStart");
        fullRenderingFragments.add("beforeItemsTable");
        fullRenderingFragments.add("afterItemsTable");
        fullRenderingFragments.add("outputEnterDataForm");
        fullRenderingFragments.add("outputEnd");
        fullRenderingFragments.add("tableStart");
        fullRenderingFragments.add("headerStart");
        fullRenderingFragments.add("outputColumnName");
        fullRenderingFragments.add("headerEnd");
        fullRenderingFragments.add("outputSubformActions");
        fullRenderingFragments.add("tableRow");
        fullRenderingFragments.add("tableEnd");

        previewRenderingFragments.add("outputStart");
        previewRenderingFragments.add("outputEnd");
        previewRenderingFragments.add("previewItem");

        editRenderingFragments.add("outputStart");
        editRenderingFragments.add("outputEnd");
        editRenderingFragments.add("editItem");
    }

    @Override
    protected void initDependencies() {
        helper = weld.instance().select( SubFormHelper.class ).get();
        fieldI18nResourceObtainer = weld.instance().select( FieldI18nResourceObtainer.class ).get();
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

        formatter.service( httpServletRequest, httpServletResponse );

        verify( tag, atLeastOnce() ).addProcessingInstruction(argThat(new FormatterFragmentMatcher(fullRenderingFragments)));
    }

    @Test
    public void testPreviewRendering() throws Exception {
        testPreviewRendering(false);
    }

    @Test
    public void testPreviewReadonlyRendering() throws Exception {
        testPreviewRendering( true );
    }

    protected void testPreviewRendering(final boolean readonly) throws Exception {
        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY)).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return readonly;
            }
        });

        currentNamespace += SubFormHelperMock.PREVIEW_FIELD_SUFFIX;

        formatter.service( httpServletRequest, httpServletResponse );

        verify( tag, atLeastOnce() ).addProcessingInstruction(argThat(new FormatterFragmentMatcher(previewRenderingFragments)));
    }

    @Test
    public void testEditRendering() throws Exception {
        testEditRendering(false);
    }

    @Test
    public void testEditReadonlyRendering() throws Exception {
        testEditRendering( true );
    }

    public void testEditRendering( final boolean readonly ) throws Exception {
        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD_IS_READONLY)).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return readonly;
            }
        });

        currentNamespace += SubFormHelperMock.EDIT_FIELD_SUFFIX;

        formatter.service( httpServletRequest, httpServletResponse );

        verify( tag, atLeastOnce() ).addProcessingInstruction( argThat( new FormatterFragmentMatcher(editRenderingFragments) ));
    }

    @Override
    protected void mockRequestAttributes() {
        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FORM_RENDER_MODE)).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return Form.RENDER_MODE_FORM;
            }
        });

        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_VALUE)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new Object[0];
            }
        });
    }

    @Override
    protected CreateDynamicObjectFieldFormatter getFormatterInstance() {
        CreateDynamicObjectFieldFormatter formatter = new CreateDynamicObjectFieldFormatter();
        formatter.helper = helper;
        formatter.fieldI18nResourceObtainer = fieldI18nResourceObtainer;
        return formatter;
    }

    @Override
    protected Field getMockedField() {
        Field currentField = new Field();
        currentField.setFieldType(fieldTypeManager.getTypeByCode("MultipleSubform"));

        currentField.setFieldName("multipleSubform");

        return currentField;
    }


}
