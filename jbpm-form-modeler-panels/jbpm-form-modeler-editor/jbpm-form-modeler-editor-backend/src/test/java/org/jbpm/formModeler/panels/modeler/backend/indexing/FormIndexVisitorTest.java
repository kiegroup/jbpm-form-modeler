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

package org.jbpm.formModeler.panels.modeler.backend.indexing;

import java.io.InputStream;

import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.DataHolderManager;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.config.FormSerializationManagerImpl;
import org.jbpm.formModeler.core.config.builders.dataHolder.BasicTypeHolderBuilder;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.core.config.builders.dataHolder.PojoDataHolderBuilder;
import org.jbpm.formModeler.core.model.BasicTypeDataHolder;
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.jbpm.formModeler.dataModeler.integration.DataModelerService;
import org.jbpm.formModeler.dataModeler.model.DataModelerDataHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormIndexVisitorTest {

    public static final String SIMPLE_DATA_HOLDER_FORM = "ReviewAdministration-taskform.form";

    public static final String COMPLEX_DATA_HOLDER_FORM = "PurchaseHeader.form";

    @Mock
    private DataHolderManager dataHolderManager;

    @Mock
    private FieldTypeManager fieldTypeManager;

    private FormSerializationManager serializationManager;

    @Before
    public void init() {

        when(dataHolderManager.createDataHolderByType(any(), any())).thenAnswer(new Answer<DataHolder>() {
            @Override
            public DataHolder answer(InvocationOnMock invocationOnMock) throws Throwable {

                DataHolder dataHolder = null;

                String holderType = (String) invocationOnMock.getArguments()[0];

                DataHolderBuildConfig config = (DataHolderBuildConfig) invocationOnMock.getArguments()[1];
                if ( holderType.equals(DataModelerService.HOLDER_TYPE_DATA_MODEL)) {
                    dataHolder = mock(DataModelerDataHolder.class);
                } else if (holderType.equals(PojoDataHolderBuilder.HOLDER_TYPE_POJO_CLASSNAME)){
                    dataHolder = mock(PojoDataHolder.class);
                } else if (holderType.equals(BasicTypeHolderBuilder.HOLDER_TYPE_BASIC_TYPE)) {
                    dataHolder = mock(BasicTypeDataHolder.class);
                }
                if ( dataHolder != null) {
                    when(dataHolder.getUniqeId()).thenReturn(config.getHolderId());
                    when(dataHolder.getInputId()).thenReturn(config.getInputId());
                    when(dataHolder.getOuputId()).thenReturn(config.getOutputId());
                    when(dataHolder.getRenderColor()).thenReturn(config.getRenderColor());
                    when(dataHolder.getClassName()).thenReturn(config.getValue());
                }
                return dataHolder;
            }
        });

        serializationManager = new FormSerializationManagerImpl() {
            {
                formManager = new FormManagerImpl();
                dataHolderManager = FormIndexVisitorTest.this.dataHolderManager;
                fieldTypeManager = FormIndexVisitorTest.this.fieldTypeManager;
            }

        };
    }

    @Test
    public void testFormWithSimpleDataHolder() throws Exception {
        runTest(SIMPLE_DATA_HOLDER_FORM);
    }

    @Test
    public void testFormWithComplexDataHolder() throws Exception {
        runTest(COMPLEX_DATA_HOLDER_FORM);
    }

    protected void runTest(final String formName) throws Exception {
        InputStream formInputStream = this.getClass().getResourceAsStream(formName);
        Form form = serializationManager.loadFormFromXML(formInputStream);

        FormIndexVisitor visitor = spy(new FormIndexVisitor(form));

        visitor.visit();

        verify(visitor, times(form.getFormFields().size())).visit(any(Field.class), any());

        verify(visitor, times(form.getHolders().size())).visit(any(DataHolder.class));
    }



}
