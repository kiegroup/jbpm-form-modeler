package org.jbpm.formModeler.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.formModeler.client.i18n.Constants;
import org.kie.workbench.common.screens.projecteditor.client.menu.ProjectMenu;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.IconType;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBarItem;

@ApplicationScoped
@WorkbenchPerspective(identifier = "home", isDefault = true)
public class HomePerspective {

    private Constants constants = GWT.create(Constants.class);

    private static String[] PERMISSIONS_ADMIN = new String[]{ "ADMIN" };

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

    private PerspectiveDefinition perspective;
    private Menus menus;
    private ToolBar toolBar;

    @PostConstruct
    public void init() {
        buildPerspective();
        buildMenuBar();
        buildToolBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    public void buildPerspective( ) {
        perspective = new PerspectiveDefinitionImpl(PanelType.ROOT_LIST);
        perspective.setName("Home");

        final PanelDefinition west = new PanelDefinitionImpl(PanelType.MULTI_LIST);
        west.setWidth(300);
        west.setMinWidth(200);
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("org.kie.guvnor.explorer")));
        perspective.getRoot().insertChild( Position.WEST, west );
        perspective.setTransient(true);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu("Projects")
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "org.kie.guvnor.explorer" );
                    }
                } )
                .endMenu()

                .newTopLevelMenu("New")
                .withItems(newResourcesMenu.getMenuItems())
                .endMenu()

                .newTopLevelMenu("Tools")
                .withItems(projectMenu.getMenuItems())
                .endMenu()

                .build();
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "guvnor.new.item" );
        final String tooltip = constants.newItem();
        final Command command = new Command() {
            @Override
            public void execute() {
                newResourcePresenter.show();
            }
        };
        toolBar.addItem( new DefaultToolBarItem( IconType.FILE, tooltip, command ) );
    }
}
