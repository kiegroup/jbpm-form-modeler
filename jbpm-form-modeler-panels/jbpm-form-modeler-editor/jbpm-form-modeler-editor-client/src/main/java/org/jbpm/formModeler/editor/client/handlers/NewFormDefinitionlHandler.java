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
package org.jbpm.formModeler.editor.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.formModeler.editor.client.resources.i18n.Constants;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class NewFormDefinitionlHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<FormModelerService> modelerService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private FormDefinitionResourceType resourceType;

    @Inject
    private Event<NotificationEvent> notificationEvent;

    @Override
    public String getDescription() {
        return Constants.INSTANCE.form_modeler_form();
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( org.guvnor.common.services.project.model.Package pkg,
                        String baseFileName,
                        final NewResourcePresenter presenter ) {
        BusyPopup.showMessage( "Creating New Form" );

        modelerService.call( new RemoteCallback<Path>() {
                                 @Override
                                 public void callback( final Path path ) {
                                     BusyPopup.close();
                                     presenter.complete();
                                     notifySuccess();
                                     PlaceRequest place = new PathPlaceRequest( path, "FormModelerEditor" );
                                     placeManager.goTo( place );

                                 }
                             }, new ErrorCallback<Message>() {
                                 @Override
                                 public boolean error( Message message,
                                                       Throwable throwable ) {
                                     BusyPopup.close();
                                     ErrorPopup.showMessage( CommonConstants.INSTANCE.SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother() );
                                     return true;
                                 }
                             }
                           ).createForm( pkg.getPackageMainResourcesPath(), buildFileName( baseFileName,
                                                                                           resourceType ) );
    }

}
