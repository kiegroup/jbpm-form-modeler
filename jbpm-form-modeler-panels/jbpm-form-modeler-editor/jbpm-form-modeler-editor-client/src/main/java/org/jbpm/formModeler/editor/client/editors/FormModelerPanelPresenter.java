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
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
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
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
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
    private SyncBeanManager iocBeanManager;

    @Inject
    private PlaceManager placeManager;

    private FormModelerPanelView view;

    @Inject
    private Caller<FormModelerService> modelerService;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private FormDefinitionResourceType resourceType;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    private FormModelerContent content;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;

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
        new SaveOperationService().save( versionRecordManager.getCurrentPath(),
                                         new ParameterizedCommand<String>() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 try {
                                                     modelerService.call( new RemoteCallback<Path>() {
                                                         @Override
                                                         public void callback( Path formPath ) {
                                                             busyIndicatorView.hideBusyIndicator();
                                                             notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_successfully_saved( versionRecordManager.getCurrentPath().getFileName() ), NotificationEvent.NotificationType.SUCCESS ) );
                                                         }
                                                     } ).save( versionRecordManager.getCurrentPath(), content.getContextTO(), metadata, commitMessage );
                                                 } catch ( Exception e ) {
                                                     notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_save( versionRecordManager.getCurrentPath().getFileName() ), NotificationEvent.NotificationType.ERROR ) );
                                                 } finally {
                                                     busyIndicatorView.hideBusyIndicator();
                                                 }
                                             }
                                         }
                                       );
        concurrentUpdateSessionInfo = null;
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return modelerService;
    }

    @Override
    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return modelerService;
    }

    protected void onDelete() {
        final DeletePopup popup = new DeletePopup( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String comment ) {
                busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                modelerService.call( new RemoteCallback<Void>() {

                    @Override
                    public void callback( final Void response ) {
                        notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully(), NotificationEvent.NotificationType.SUCCESS ) );
                        placeManager.closePlace( place );
                        onClose();
                        busyIndicatorView.hideBusyIndicator();
                    }
                }, new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( versionRecordManager.getCurrentPath(), comment );
            }
        } );

        popup.show();
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

    private void disableMenus() {
        menus.getItemsMap().get( MenuItems.DELETE ).setEnabled( false );
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
