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
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jbpm.formModeler.client.i18n.Constants;
import org.jbpm.formModeler.client.resources.StandaloneResources;
import org.kie.workbench.common.services.shared.security.KieWorkbenchSecurityService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

@EntryPoint
public class ShowcaseEntryPoint extends DefaultWorkbenchEntryPoint {

    protected Constants constants = Constants.INSTANCE;

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected WorkbenchMenuBarPresenter menuBar;

    protected PlaceManager placeManager;

    @Inject
    public ShowcaseEntryPoint( final Caller<AppConfigService> appConfigService,
                               final Caller<KieWorkbenchSecurityService> kieSecurityService,
                               final Caller<PlaceManagerActivityService> pmas,
                               final KieWorkbenchACL kieACL,
                               final ActivityBeansCache activityBeansCache,
                               final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                               final WorkbenchMenuBarPresenter menuBar,
                               final PlaceManager placeManager ) {
        super( appConfigService, kieSecurityService, pmas, kieACL, activityBeansCache );
        this.menusHelper = menusHelper;
        this.menuBar = menuBar;
        this.placeManager = placeManager;

        addCustomSecurityLoadedCallback( policy -> StandaloneResources.INSTANCE.CSS().ensureInjected() );
    }

    @Override
    protected void setupMenu() {
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = menusHelper.getDefaultPerspectiveActivity();

        final Menus menus =
                MenuFactory.newTopLevelMenu( constants.home() ).respondsWith( () -> {
                    if ( defaultPerspective != null ) {
                        placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                    } else {
                        Window.alert( "Default perspective not found." );
                    }
                } )
                .endMenu()
                .newTopLevelMenu( constants.authoring() ).withItems( getAuthoringViews() ).endMenu().build();

        menuBar.addMenus( menus );

        menusHelper.addRolesMenuItems();
        menusHelper.addWorkbenchConfigurationMenuItem();
        menusHelper.addUtilitiesMenuItems();

    }

    protected List<? extends MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.project_authoring() ).withRoles( kieACL.getGrantedRoles( "wb_project_authoring" ) ).place( new DefaultPlaceRequest( "AuthoringPerspective" ) ).endMenu().build().getItems().get( 0 ) );

        return result;
    }
}
