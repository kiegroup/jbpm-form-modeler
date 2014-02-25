/**
 * Copyright (C) 2012 JBoss Inc
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
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.jbpm.formModeler.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithFileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.DeletePopup;
import org.kie.workbench.common.widgets.client.popups.file.FileNameAndCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.RenamePopup;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

import static org.uberfire.client.common.ConcurrentChangePopup.*;

@Dependent
@WorkbenchEditor(identifier = "FormModelerEditor", supportedTypes = { FormDefinitionResourceType.class })
public class FormModelerPanelPresenter {

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private SyncBeanManager iocBeanManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    FormModelerPanelView view;

    @Inject
    Caller<FormModelerService> modelerService;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private FormResourceTypeDefinition resourceType;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    private FormEditorContextTO context;

    private Menus menus;

    protected boolean isReadOnly;
    private String version;

    @Inject
    @New
    private FileMenuBuilder menuBuilder;

    @Inject
    private MetadataWidget metadataWidget;

    private ObservablePath path;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private PlaceRequest place;

    @OnStartup
    public void onStartup( final ObservablePath path,
                           PlaceRequest placeRequest ) {

        this.place = placeRequest;
        this.path = path;

        this.isReadOnly = place.getParameter( "readOnly", null ) != null;
        this.version = place.getParameter( "version", null );

        multiPage.addPage( new Page( view,
                                     CommonConstants.INSTANCE.SourceTabTitle() ) {
            @Override
            public void onFocus() {
            }

            @Override
            public void onLostFocus() {
            }
        } );

        multiPage.addPage( new Page( metadataWidget,
                                     CommonConstants.INSTANCE.MetadataTabTitle() ) {
            @Override
            public void onFocus() {
                if ( !metadataWidget.isAlreadyLoaded() ) {
                    metadataWidget.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
                    metadataService.call( new MetadataSuccessCallback( metadataWidget,
                                                                       isReadOnly ),
                                          new HasBusyIndicatorDefaultErrorCallback( metadataWidget )
                                        ).getMetadata( path );
                }
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        } );

        this.path.onConcurrentUpdate( new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentUpdateEvent eventInfo ) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        } );

        this.path.onConcurrentDelete( new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentDelete info ) {
                newConcurrentDelete( info.getPath(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             placeManager.closePlace( place );
                                         }
                                     }
                                   ).show();
            }
        } );

        this.path.onConcurrentRename( new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute( final ObservablePath.OnConcurrentRenameEvent info ) {
                newConcurrentRename( info.getSource(),
                                     info.getTarget(),
                                     info.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             disableMenus();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             reload();
                                         }
                                     }
                                   ).show();
            }
        } );

        this.path.onRename( new Command() {
            @Override
            public void execute() {
                changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitle(), null ) );
            }
        } );

        modelerService.call( new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback( FormEditorContextTO ctx ) {
                loadContext( ctx );
            }
        } ).loadForm( path );
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        changeTitleNotification.fire( new ChangeTitleWidgetEvent( place, getTitle(), null ) );
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        modelerService.call( new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback( FormEditorContextTO ctx ) {
                loadContext( ctx );
            }
        } ).reloadForm( path, context.getCtxUID() );
    }

    private void onRename() {
        final RemoteCallback<Path> renameCallback = new RemoteCallback<Path>() {
            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
                modelerService.call().changeContextPath( context.getCtxUID(), path );
            }
        };
        RenamePopup popup = new RenamePopup( path,
                                             fileNameValidator,
                                             new CommandWithFileNameAndCommitMessage() {
                                                 @Override
                                                 public void execute( final FileNameAndCommitMessage details ) {
                                                     busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                     modelerService.call( renameCallback,
                                                                          new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                  details.getNewFileName(),
                                                                                                                                                  details.getCommitMessage() );
                                                 }
                                             } );

        popup.show();
    }

    @OnSave
    private void onSave() {
        if ( isReadOnly ) {
            view.showCanNotSaveReadOnly();
        } else {
            if ( concurrentUpdateSessionInfo != null ) {
                newConcurrentUpdate( concurrentUpdateSessionInfo.getPath(),
                                     concurrentUpdateSessionInfo.getIdentity(),
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             save();
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             //cancel?
                                         }
                                     },
                                     new Command() {
                                         @Override
                                         public void execute() {
                                             reload();
                                         }
                                     }
                                   ).show();
            } else {
                save();
            }
        }
    }

    public void save() {
        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 try {
                                                     modelerService.call( new RemoteCallback<Path>() {
                                                         @Override
                                                         public void callback( Path formPath ) {
                                                             busyIndicatorView.hideBusyIndicator();
                                                             notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_successfully_saved( path.getFileName() ), NotificationEvent.NotificationType.SUCCESS ) );
                                                         }
                                                     } ).save( path, context, metadataWidget.getContent(), commitMessage );
                                                 } catch ( Exception e ) {
                                                     notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_save( path.getFileName() ), NotificationEvent.NotificationType.ERROR ) );
                                                 } finally {
                                                     busyIndicatorView.hideBusyIndicator();
                                                 }
                                             }
                                         }
                                       );
        concurrentUpdateSessionInfo = null;
    }

    protected void onDelete() {
        final DeletePopup popup = new DeletePopup( new CommandWithCommitMessage() {
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
                }, new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( path, comment );
            }
        } );

        popup.show();
    }

    @OnOpen
    public void onOpen() {
        makeMenuBar();

        if ( context == null ) {
            return;
        }
    }

    @OnClose
    public void onClose() {
        if ( context != null ) {
            modelerService.call().removeEditingForm( context.getCtxUID() );
        }
    }

    public void loadContext( FormEditorContextTO ctx ) {
        busyIndicatorView.hideBusyIndicator();
        if ( ctx == null || ctx.isLoadError() ) {
            notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_load_form( path.getFileName() ), NotificationEvent.NotificationType.ERROR ) );
        }
        if ( ctx != null ) {
            this.context = ctx;
            view.loadContext( ctx.getCtxUID() );
            makeMenuBar();
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = FileNameUtil.removeExtension( path, resourceType );
        if ( version != null ) {
            fileName = fileName + " v" + version;
        }
        return Constants.INSTANCE.form_modeler_title( fileName );
    }

    private void disableMenus() {
        menus.getItemsMap().get( FileMenuBuilder.MenuItems.DELETE ).setEnabled( false );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if ( menus == null ) {
            makeMenuBar();
        }
        return menus;
    }

    private void makeMenuBar() {

        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave( new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    } )
                    .addRename( new Command() {
                        @Override
                        public void execute() {
                            onRename();
                        }
                    } )
                    .addDelete( new Command() {
                        @Override
                        public void execute() {
                            onDelete();
                        }
                    } )
                    .build();
        }
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }
}
