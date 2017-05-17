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

import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.FieldHandlersManager;
import org.jbpm.formModeler.core.FieldHandlersManagerImpl;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.RangeProviderManager;
import org.jbpm.formModeler.core.processing.fieldHandlers.mocks.SubFormHelperMock;
import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;
import org.jbpm.formModeler.core.processing.formProcessing.DefaultFormulaProcessor;
import org.jbpm.formModeler.core.processing.formProcessing.FormulasCalculatorChangeProcessor;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.core.processing.formStatus.FormStatusManager;
import org.jbpm.formModeler.core.processing.impl.util.TestFormGenerator;
import org.junit.Before;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AbstractFormProcessorImplTest {

    public static final String CTX_UID = "ctxUID";

    protected TestFormGenerator formGenerator;
    private FormulasCalculatorChangeProcessor formulasCalculatorChangeProcessor;
    private RangeProviderManager rangeProviderManager;
    private DefaultFormulaProcessor defaultFormulaProcessor;
    private FieldHandlersManager fieldHandlersManager;
    private FormRenderContextManager formRenderContextManager;
    private NamespaceManager namespaceManager;
    private SubFormHelper subFormHelper;
    private FormStatusManager formStatusManager;
    protected FormProcessorImpl processor;

    protected FormRenderContext context;

    protected Form form;

    protected Map<String, Object> inputs = new HashMap<>();

    protected Map<String, Object> outputs = new HashMap<>();

    protected FieldTypeManager fieldTypeManager;

    protected Weld weld;
    protected WeldContainer weldContainer;

    @Before
    public void init() {
        weld = new Weld();
        weldContainer = weld.initialize();

        formulasCalculatorChangeProcessor = weldContainer.instance().select(FormulasCalculatorChangeProcessor.class).get();
        rangeProviderManager = weldContainer.instance().select(RangeProviderManager.class).get();
        defaultFormulaProcessor = weldContainer.instance().select(DefaultFormulaProcessor.class).get();
        fieldHandlersManager = weldContainer.instance().select(FieldHandlersManagerImpl.class).get();
        namespaceManager = weldContainer.instance().select(NamespaceManager.class).get();
        formRenderContextManager = mock(FormRenderContextManager.class);
        subFormHelper = spy(new SubFormHelperMock());
        formStatusManager = weldContainer.instance().select(FormStatusManager.class).get();

        processor = spy(new FormProcessorImpl(formulasCalculatorChangeProcessor,
                                              rangeProviderManager,
                                              defaultFormulaProcessor,
                                              fieldHandlersManager,
                                              formRenderContextManager,
                                              namespaceManager,
                                              subFormHelper,
                                              formStatusManager));

        fieldTypeManager = weldContainer.instance().select(FieldTypeManager.class).get();

        formGenerator = new TestFormGenerator(fieldTypeManager);
    }

    protected void initContext() {
        context = new FormRenderContext(CTX_UID,
                                        form,
                                        inputs,
                                        outputs);

        when(formRenderContextManager.getFormRenderContext(anyString())).thenReturn(context);
        when(formRenderContextManager.getRootContext(anyString())).thenReturn(context);
    }
}
