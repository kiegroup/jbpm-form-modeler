/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.formModeler.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.projecteditor.client.menu.ProjectMenu;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchPerspective(identifier = "home", isDefault = true)
public class HomePerspective {

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private ProjectMenu projectMenu;

    @Perspective
    public PerspectiveDefinition buildPerspective() {

        final PerspectiveDefinitionImpl perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( "Author" );

        final PanelDefinition west = new PanelDefinitionImpl( SimpleWorkbenchPanelPresenter.class.getName() );
        west.setWidth( 400 );
        west.setMinWidth( 350 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );

        perspective.getRoot().insertChild( CompassPosition.WEST,
                                           west );

        return perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu( "Projects" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "org.kie.guvnor.explorer" );
                    }
                } )
                .endMenu()

                .newTopLevelMenu( "New" )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()

                .newTopLevelMenu( "Tools" )
                .withItems( projectMenu.getMenuItems() )
                .endMenu()

                .build();
    }
}
