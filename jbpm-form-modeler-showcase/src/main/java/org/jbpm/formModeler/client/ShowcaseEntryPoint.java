/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.formModeler.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.bus.client.api.BusErrorCallback;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jbpm.formModeler.client.i18n.Constants;
import org.jbpm.formModeler.client.resources.StandaloneResources;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.common.services.shared.security.KieWorkbenchPolicy;
import org.guvnor.common.services.shared.security.KieWorkbenchSecurityService;
import org.kie.workbench.common.widgets.client.menu.AboutMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.WorkbenchConfigurationMenuBuilder;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

@EntryPoint
public class ShowcaseEntryPoint {
    private Constants constants = Constants.INSTANCE;

    @Inject
    private SyncBeanManager manager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private KieWorkbenchACL kieACL;

    @Inject
    private Caller<KieWorkbenchSecurityService> kieSecurityService;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    public User identity;

    @Inject
    private UtilityMenuBar utilityMenuBar;

    @Inject
    private UserMenu userMenu;

    @AfterInitialization
    public void startApp() {
        kieSecurityService.call( new RemoteCallback<String>() {
            public void callback( final String str ) {
                KieWorkbenchPolicy policy = new KieWorkbenchPolicy( str );
                kieACL.activatePolicy( policy );
                loadStyles();
                setupMenu();
                hideLoadingPopup();
            }
        } ).loadPolicy();
    }

    private void logout() {
        authService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void response ) {
                redirect( GWT.getHostPageBaseURL() + "login.jsp" );
            }
        }, new BusErrorCallback() {
            @Override
            public boolean error( Message message,
                                  Throwable throwable ) {
                Window.alert( "Logout failed: " + throwable );
                return true;
            }
        } ).logout();
    }

    private void loadStyles() {
        StandaloneResources.INSTANCE.CSS().ensureInjected();
    }

    private void setupMenu() {
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();

        final Menus menus =
                MenuFactory.newTopLevelMenu( constants.home() ).respondsWith( new Command() {
                    @Override
                    public void execute() {
                        if ( defaultPerspective != null ) {
                            placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                        } else {
                            Window.alert( "Default perspective not found." );
                        }
                    }
                } )
                        .endMenu()
                        .newTopLevelMenu( constants.authoring() ).withItems( getAuthoringViews() ).endMenu().build();

        menubar.addMenus( menus );

        for( Menus roleMenus : getRoles() ){
            userMenu.addMenus( roleMenus );
        }

        final Menus utilityMenus =
                MenuFactory.newTopLevelCustomMenu( iocManager.lookupBean( WorkbenchConfigurationMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( CustomSplashHelp.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( AboutMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( iocManager.lookupBean( ResetPerspectivesMenuBuilder.class ).getInstance() )
                        .endMenu()
                        .newTopLevelCustomMenu( userMenu )
                        .endMenu()
                        .build();

        utilityMenuBar.addMenus( utilityMenus );

    }

    private List<? extends MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 3 );

        result.add( MenuFactory.newSimpleItem( constants.project_authoring() )
                .withRoles( kieACL.getGrantedRoles( "wb_project_authoring" ) )
                .place( new DefaultPlaceRequest( "AuthoringPerspective" ) )
                .endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<Menus> getRoles() {
        final List<Menus> result = new ArrayList<Menus>( identity.getRoles().size() );
        result.add( MenuFactory.newSimpleItem( constants.logout() ).respondsWith( new Command() {
            @Override
            public void execute() {
                logout();
            }
        } ).endMenu().build() );
        for ( final Role role : identity.getRoles() ) {
            if ( !role.getName().equals( "IS_REMEMBER_ME" ) ) {
                result.add( MenuFactory.newSimpleItem( constants.role() + ": " + role.getName() ).endMenu().build() );
            }
        }
        return result;
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}
