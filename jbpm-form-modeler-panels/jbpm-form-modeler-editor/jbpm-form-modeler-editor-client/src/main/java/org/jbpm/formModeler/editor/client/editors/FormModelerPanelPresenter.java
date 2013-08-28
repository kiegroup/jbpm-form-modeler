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
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.shared.file.DeleteService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.formModeler.api.client.FormEditorContextTO;
import org.jbpm.formModeler.editor.client.resources.i18n.Constants;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "FormModelerEditor", supportedTypes = { FormDefinitionResourceType.class })
public class FormModelerPanelPresenter {

    public interface FormModelerPanelView
            extends
            UberView<FormModelerPanelPresenter> {

        void hideForm();

        void loadContext( FormEditorContextTO context );
    }

    @Inject
    private SyncBeanManager iocBeanManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    FormModelerPanelView view;

    @Inject
    Caller<FormModelerService> modelerService;

    @Inject
    private Caller<DeleteService> deleteService;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private Event<ResourceDeletedEvent> resourceDeleteEvent;

    @Inject
    private Event<NotificationEvent> notification;

    private FormEditorContextTO context;

    private Menus menus;

    private Path path;

    @OnStartup
    public void onStartup( final Path path,
                           PlaceRequest placeRequest ) {

        this.path = path;

        modelerService.call( new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback( FormEditorContextTO ctx ) {
                if ( ctx == null ) {
                    notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_load_form( path.getFileName() ), NotificationEvent.NotificationType.ERROR ) );
                } else {
                    loadContext( ctx );
                    makeMenuBar();
                }
            }
        } ).loadForm( path );

    }

    @OnSave
    public void onSave() {
        try {
            modelerService.call( new RemoteCallback<Long>() {
                @Override
                public void callback( Long formId ) {
                    notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_successfully_saved( context.getFormName() ), NotificationEvent.NotificationType.SUCCESS ) );
                }
            } ).saveForm( context.getCtxUID() );
        } catch ( Exception e ) {
            notification.fire( new NotificationEvent( Constants.INSTANCE.form_modeler_cannot_save( context.getFormName() ), NotificationEvent.NotificationType.ERROR ) );
        }

    }

    protected void onDelete() {
        if ( path == null || !Window.confirm( Constants.INSTANCE.form_modeler_confirm_delete() ) ) {
            return;
        }
        RemoteCallback deleteCallBack = new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully(), NotificationEvent.NotificationType.SUCCESS ) );
                placeManager.closePlace( new PathPlaceRequest( path ) );
                resourceDeleteEvent.fire( new ResourceDeletedEvent( path ) );
            }
        };

        deleteService.call( deleteCallBack, new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( path, "" );
    }

    @OnOpen
    public void onOpen() {
        makeMenuBar();

        if ( context == null ) {
            return;
        }

        modelerService.call( new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback( FormEditorContextTO context ) {
                loadContext( context );
            }

        } ).setFormFocus( ( context != null ? context.getCtxUID() : null ) );

    }

    @OnClose
    public void onClose() {
        modelerService.call( new RemoteCallback<Long>() {
            @Override
            public void callback( Long formId ) {
            }

        } ).removeEditingForm( context.getCtxUID() );

    }

    public void loadContext( FormEditorContextTO ctx ) {
        this.context = ctx;
        view.loadContext( ctx );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.form_modeler_title( path.getFileName() );
    }

    @WorkbenchPartView
    public UberView<FormModelerPanelPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if ( menus == null ) {
            makeMenuBar();
        }
        return menus;
    }

    private void makeMenuBar() {

        Command saveCommand = new Command() {
            @Override
            public void execute() {
                onSave();
            }
        };

        Command deleteCommand = new Command() {
            @Override
            public void execute() {
                onDelete();
            }
        };

        menus = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.form_modeler_save() )
                .respondsWith( saveCommand )
                .endMenu()
                .newTopLevelMenu( Constants.INSTANCE.form_modeler_delete() )
                .respondsWith( deleteCommand )
                .endMenu()
                .build();
    }

}
