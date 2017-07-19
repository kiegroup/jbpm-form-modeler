/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.formModeler.editor.client.editors;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.model.FormEditorContextTO;
import org.jbpm.formModeler.editor.model.FormModelerContent;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormModelerPanelPresenterTest {

    @Mock
    protected FormModelerPanelView view;

    @Mock
    protected FormModelerService modelerServiceMock;

    @Mock
    protected MetadataService metadataServiceMock;

    @Mock
    protected EventSourceMock<NotificationEvent> notificationMock;

    @Mock
    protected EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationMock;

    @GwtMock
    protected FormDefinitionResourceType formDefinitionResourceTypeMock;

    @Mock
    protected PlaceRequest placeRequestMock;

    @GwtMock
    protected VersionRecordManager versionRecordManagerMock;

    @Mock
    protected Overview overview;

    @Mock
    protected ObservablePath path;

    @Mock
    protected BasicFileMenuBuilder menuBuilder;

    @Spy
    @InjectMocks
    protected FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    protected ProjectController projectController;

    @Mock
    protected ProjectContext workbenchContext;

    protected FormEditorContextTO editionContextTO = new FormEditorContextTO("contextId");
    ;

    protected FormModelerPanelPresenter presenter;

    @Before
    public void setup() {
        presenter = new FormModelerPanelPresenter(view) {
            {
                this.placeManager = mock(PlaceManager.class);
                this.modelerService = new CallerMock<>(modelerServiceMock);
                this.busyIndicatorView = mock(BusyIndicatorView.class);
                this.notification = notificationMock;
                this.changeTitleNotification = changeTitleNotificationMock;
                this.metadataService = new CallerMock<>(metadataServiceMock);
                this.resourceType = formDefinitionResourceTypeMock;
                this.fileNameValidator = mock(DefaultFileNameValidator.class);
                this.fileMenuBuilder = mock(FileMenuBuilder.class);
                this.versionRecordManager = versionRecordManagerMock;
                this.kieView = mock(KieEditorWrapperView.class);
                this.overviewWidget = mock(OverviewWidgetPresenter.class);
                this.savePopUpPresenter = mock(SavePopUpPresenter.class);
                this.fileMenuBuilder = FormModelerPanelPresenterTest.this.fileMenuBuilder;
                this.projectController = FormModelerPanelPresenterTest.this.projectController;
                this.workbenchContext = FormModelerPanelPresenterTest.this.workbenchContext;
            }
        };

        when(versionRecordManagerMock.getCurrentPath()).thenReturn(path);

        FormModelerContent content = createContent();
        when(modelerServiceMock.loadContent(path)).thenReturn(content);
        when(modelerServiceMock.reloadContent(path,
                                              content.getContextTO().getCtxUID())).thenReturn(editionContextTO);

        presenter.onStartup(path,
                            placeRequestMock);
    }

    @After
    public void finishTest() {
        presenter.onClose();
    }

    @Test
    public void testSaveForm() {
        String commitMessage = "commit message";
        presenter.save();
        presenter.runSaveCommand(commitMessage);
        verify(modelerServiceMock,
               times(1)).save(eq(path),
                              eq(editionContextTO),
                              any(Metadata.class),
                              eq(commitMessage));
        verify(versionRecordManagerMock,
               atLeast(1)).reloadVersions(path);
    }

    @Test
    public void testReloadForm() {
        presenter.reload();
        verify(modelerServiceMock,
               times(1)).reloadContent(path,
                                       editionContextTO.getCtxUID());
    }

    @Test
    public void testMakeMenuBar() {
        reset(fileMenuBuilder);
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        reset(fileMenuBuilder);
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class));
    }

    protected FormModelerContent createContent() {
        FormModelerContent content = new FormModelerContent();

        content.setPath(path);
        content.setOverview(overview);
        content.setContextTO(editionContextTO);

        return content;
    }
}
