/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.editor.client.handlers;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Package;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@RunWith(GwtMockitoTestRunner.class)
public class NewFormDefinitionHandlerTest {

    NewFormDefinitionHandler testedHandler;

    @Mock
    EventSourceMock<NewResourceSuccessEvent> newResourceSuccessEventMock;

    @Mock
    FormModelerService fmServiceMock;

    @Mock
    PlaceManager pManagerMock;

    @Mock
    EventSourceMock<NotificationEvent> eventMock;

    @Mock
    NewResourcePresenter nrpresenterMock;

    @Mock
    ErrorPopupPresenter errorPopupMock;

    @Mock
    org.guvnor.common.services.project.model.Package packageMock;

    @Before
    public void setupMocks() {
        CallerMock<FormModelerService> fmServiceCaller = new CallerMock<>(fmServiceMock);
        testedHandler = spy(new NewFormDefinitionHandler(fmServiceCaller, pManagerMock, null, eventMock, errorPopupMock) {
            {
                newResourceSuccessEvent = newResourceSuccessEventMock;
            }

            @Override
            protected String buildFileName(String baseFileName, ResourceTypeDefinition resourceType) {
                return "not relevant for this test";
            }

            @Override
            protected void notifySuccess() {
            }
        });
    }

    @Test
    public void errorPopupDisplayedWhenFormAlreadyExists() {
        when(fmServiceMock.createForm(any(Path.class), anyString()))
                .thenThrow(new GenericPortableException("File already exists."));

        testedHandler.create(packageMock, "existingForm", nrpresenterMock);

        verify(errorPopupMock).showMessage(CommonConstants.INSTANCE.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother());
    }

    @Test
    public void createTest() {
        final NewResourcePresenter presenter = mock(NewResourcePresenter.class);
        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);

        doReturn(path).when(fmServiceMock).createForm(any(Path.class), anyString());
        doReturn(path).when(pathPlaceRequest).getPath();
        doReturn(pathPlaceRequest).when(testedHandler).createPathPlaceRequest(path);

        testedHandler.create(mock(Package.class),
                             "",
                             presenter);

        verify(presenter).complete();
        verify(newResourceSuccessEventMock, times(1)).fire(any(NewResourceSuccessEvent.class));
        verify(pManagerMock, times(1)).goTo(pathPlaceRequest);
    }
}
