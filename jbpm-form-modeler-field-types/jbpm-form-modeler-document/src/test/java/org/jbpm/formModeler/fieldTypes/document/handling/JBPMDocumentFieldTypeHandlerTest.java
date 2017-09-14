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

package org.jbpm.formModeler.fieldTypes.document.handling;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.document.service.impl.util.DocumentDownloadLinkGenerator;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.components.renderer.FormRenderContextManagerImpl;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ControllerStatus.class)
public class JBPMDocumentFieldTypeHandlerTest {

    private static final String NAMESPACE = "namespace";

    private static final String SERVER_TEMPLATE_ID = "serverTemplateId";

    private static final String FIELD_NAME = "myDocument";

    private static final String DOC_ID = "docId";
    private static final String DOC_NAME = "doc.txt";
    private static final long DOC_SIZE = 1234l;
    private static final Date DOC_LAST_MODIFIED = new Date();
    private static final String DOC_LINK = "http://mydocs/doc.txt";

    private static final String FILE_INPUT = "<input type=\"file\" name=\"" + FIELD_NAME + "\" id=\"" + FIELD_NAME + "\"";

    private JBPMDocumentFieldTypeHandler handler;

    @Mock
    private ControllerStatus controllerStatus;

    @Mock
    private CommandRequest commandRequest;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Field field;

    @Mock
    private FormRenderContextManagerImpl contextManager;

    @Mock
    private FormRenderContext context;

    private Document document;

    @Before
    public void init() {
        document = new DocumentImpl(DOC_ID,
                                    DOC_NAME,
                                    DOC_SIZE,
                                    DOC_LAST_MODIFIED,
                                    DOC_LINK);

        when(field.getFieldName()).thenReturn(FIELD_NAME);

        when(request.getContextPath()).thenReturn("");

        when(commandRequest.getRequestObject()).thenReturn(request);

        when(controllerStatus.getRequest()).thenReturn(commandRequest);

        when(context.getServerTemplateId()).thenReturn(SERVER_TEMPLATE_ID);

        when(contextManager.getRootContext(anyString())).thenReturn(context);

        PowerMockito.mockStatic(ControllerStatus.class);

        PowerMockito.when(ControllerStatus.lookup()).thenReturn(controllerStatus);

        handler = spy(new JBPMDocumentFieldTypeHandler(contextManager));

        handler.init();
    }

    @Test
    public void testGetInputHTML() {

        String result = handler.getInputHTML(document,
                                             field,
                                             FIELD_NAME,
                                             NAMESPACE,
                                             false);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .contains(FILE_INPUT)
                .contains(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                             DOC_ID));
    }

    @Test
    public void testGetInputHTMLReadOnly() {

        String result = handler.getInputHTML(document,
                                             field,
                                             FIELD_NAME,
                                             NAMESPACE,
                                             true);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .contains(FILE_INPUT)
                .contains("disabled=\"disabled\"")
                .contains(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                             DOC_ID));
    }

    @Test
    public void testGetInputHTMLNullDocument() {

        String result = handler.getInputHTML(null,
                                             field,
                                             FIELD_NAME,
                                             NAMESPACE,
                                             false);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .contains(FILE_INPUT)
                .doesNotContain(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                                   DOC_ID));
    }

    @Test
    public void testGetInputHTMLEmptyDocumentId() {

        document.setIdentifier(null);

        String result = handler.getInputHTML(document,
                                             field,
                                             FIELD_NAME,
                                             NAMESPACE,
                                             false);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .contains(FILE_INPUT)
                .doesNotContain(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                                   DOC_ID));
    }

    @Test
    public void testGetShowHTML() {

        String result = handler.getShowHTML(document,
                                            field,
                                            FIELD_NAME,
                                            NAMESPACE);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .doesNotContain(FILE_INPUT)
                .contains(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                             DOC_ID));
    }

    @Test
    public void testGetShowHTMLNullDocument() {

        String result = handler.getShowHTML(null,
                                            field,
                                            FIELD_NAME,
                                            NAMESPACE);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .doesNotContain(FILE_INPUT)
                .doesNotContain(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                                   DOC_ID));
    }

    @Test
    public void testGetShowHTMLEmptyDocumentId() {

        document.setIdentifier(null);

        String result = handler.getShowHTML(document,
                                            field,
                                            FIELD_NAME,
                                            NAMESPACE);

        Assertions.assertThat(result).isNotNull()
                .isNotEmpty()
                .doesNotContain(FILE_INPUT)
                .doesNotContain(DocumentDownloadLinkGenerator.generateDownloadLink(SERVER_TEMPLATE_ID,
                                                                                   DOC_ID));
    }
}
