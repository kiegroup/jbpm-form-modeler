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
package org.jbpm.formModeler.core.processing.fieldHandlers;

import junit.framework.TestCase;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterTag;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.ProcessingInstruction;
import org.junit.Before;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class DefaultFieldHandlerFormatterTest<T extends DefaultFieldHandlerFormatter> extends TestCase {
    protected FieldTypeManager fieldTypeManager;
    protected NamespaceManager namespaceManager;

    protected HttpServletRequest httpServletRequest;
    protected HttpServletResponse httpServletResponse;

    protected T formatter;
    protected FormatterTag tag;
    protected String currentNamespace;
    protected Form form;
    protected Field currentField;

    protected WeldContainer weld;

    @Before
    public void setup() {
        weld = new Weld().initialize();

        fieldTypeManager = weld.instance().select( FieldTypeManager.class ).get();
        namespaceManager = weld.instance().select( NamespaceManager.class ).get();

        initDependencies();

        initFormatter();

        httpServletRequest = mock(HttpServletRequest.class);

        httpServletResponse = mock( HttpServletResponse.class );

        form = new Form();
        form.setName( "test" );
        form.setId(System.currentTimeMillis());

        initNamespace();

        initCurrentField();

        when(httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_FIELD) ).thenAnswer(new Answer<Field>() {
            @Override
            public Field answer(InvocationOnMock invocation) throws Throwable {
                return currentField;
            }
        });

        when( httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_NAMESPACE) ).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return currentNamespace;
            }
        });

        when( httpServletRequest.getAttribute(FormRenderingFormatter.ATTR_NAME) ).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return currentNamespace + FormProcessor.NAMESPACE_SEPARATOR + currentField.getFieldName();
            }
        });

        mockRequestAttributes();
    }

    protected void initNamespace() {
        currentNamespace = FormRenderContextManager.CTX_PREFFIX + form.getId()
                + "_" + System.currentTimeMillis()
                + FormProcessor.NAMESPACE_SEPARATOR + form.getId();
    }

    protected void initFormatter() {
        formatter = getFormatterInstance();
        formatter.namespaceManager = namespaceManager;
        tag = mock( FormatterTag.class );
        formatter.setTag( tag );
    }

    protected void initCurrentField() {
        currentField = getMockedField();
        currentField.setId( System.currentTimeMillis() );
        currentField.setForm( form );
    }

    protected abstract void initDependencies();

    protected abstract T getFormatterInstance();

    protected abstract void mockRequestAttributes();

    protected abstract Field getMockedField();

    public class FormatterFragmentMatcher extends ArgumentMatcher<ProcessingInstruction> {
        protected List<String> fragments;

        public FormatterFragmentMatcher(List<String> fragments) {
            super();
            this.fragments = fragments;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction) argument;
                if ( pi.getType() != ProcessingInstruction.RENDER_FRAGMENT ) return true;
                return fragments.contains( pi.getName() );
            }
            return true;
        }
    }
}
