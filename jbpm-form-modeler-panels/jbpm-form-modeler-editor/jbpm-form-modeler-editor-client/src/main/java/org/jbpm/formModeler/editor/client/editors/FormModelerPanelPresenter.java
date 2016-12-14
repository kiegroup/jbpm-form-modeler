/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.formModeler.editor.client.resources.i18n.Constants;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.model.FormEditorContextTO;
import org.jbpm.formModeler.editor.model.FormModelerContent;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "FormModelerEditor", supportedTypes = { FormDefinitionResourceType.class })
public class FormModelerPanelPresenter extends KieEditor {

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Caller<FormModelerService> modelerService;

    @Inject
    protected BusyIndicatorView busyIndicatorView;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected Caller<MetadataService> metadataService;

    @Inject
    protected FormDefinitionResourceType resourceType;

    @Inject
    protected DefaultFileNameValidator fileNameValidator;

    @Inject
    protected FileMenuBuilder menuBuilder;

    protected FormModelerPanelView view;

    protected FormModelerContent content;

    @Inject
    public FormModelerPanelPresenter( FormModelerPanelView baseView ) {
        super( baseView );
        view = baseView;
    }

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {

        init( path, place, resourceType );
    }

    @Override
    protected void loadContent() {
        if ( versionRecordManager.getCurrentPath() != null ) {
            if (content == null) {
                modelerService.call( new RemoteCallback<FormModelerContent>() {
                    @Override
                    public void callback( FormModelerContent content ) {
                        loadContext( content );
                    }
                }, getNoSuchFileExceptionErrorCallback() ).loadContent( versionRecordManager.getCurrentPath() );
            } else {
                modelerService.call( new RemoteCallback<FormEditorContextTO>() {
                    @Override
                    public void callback( FormEditorContextTO ctx ) {
                        content.setContextTO( ctx );
                        loadContext( content );
                    }
                }, getNoSuchFileExceptionErrorCallback() ).reloadContent( versionRecordManager.getCurrentPath(), content.getContextTO().getCtxUID() );
            }
        }
    }

    public void save() {
        saveOperationService.save( versionRecordManager.getCurrentPath(),
                                         new ParameterizedCommand<String>() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 runSaveCommand( commitMessage );
                                             }
                                         }
                                       );
        concurrentUpdateSessionInfo = null;
    }

    protected void runSaveCommand( final String commitMessage ) {
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        try {
            modelerService.call( new RemoteCallback<Path>() {
                @Override
                public void callback( Path formPath ) {
                    busyIndicatorView.hideBusyIndicator();
                    notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_successfully_saved( versionRecordManager.getCurrentPath().getFileName() ), NotificationEvent.NotificationType.SUCCESS ) );

                    versionRecordManager.reloadVersions( versionRecordManager.getCurrentPath() );

                }
            } ).save( versionRecordManager.getCurrentPath(), content.getContextTO(), metadata, commitMessage );
        } catch ( Exception e ) {
            notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_save( versionRecordManager.getCurrentPath().getFileName() ), NotificationEvent.NotificationType.ERROR ) );
        } finally {
            busyIndicatorView.hideBusyIndicator();
        }
    }
    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return modelerService;
    }

    @Override
    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return modelerService;
    }

    @OnClose
    public void onClose() {
        if ( content != null ) {
            modelerService.call().removeEditingForm( content.getContextTO().getCtxUID() );
        }
    }

    public void loadContext( FormModelerContent content ) {
        busyIndicatorView.hideBusyIndicator();

        this.content = content;
        resetEditorPages( content.getOverview() );
        if ( content.getContextTO().isLoadError() ) {
            notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_load_form( content.getPath().getFileName() ), NotificationEvent.NotificationType.ERROR ) );
        }
        view.loadContext( content.getContextTO().getCtxUID() );
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        String fileName = FileNameUtil.removeExtension( versionRecordManager.getCurrentPath(), resourceType );
        return Constants.INSTANCE.form_modeler_title( fileName );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if ( menus == null ) {
            makeMenuBar();
        }
        return menus;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addSave(versionRecordManager.newSaveMenuItem(new Command() {
                    @Override
                    public void execute() {
                        onSave();
                    }
                }))
                .addCopy(versionRecordManager.getCurrentPath(),
                        fileNameValidator)
                .addRename(versionRecordManager.getPathToLatest(),
                        fileNameValidator)
                .addDelete(versionRecordManager.getPathToLatest())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();
    }
}
