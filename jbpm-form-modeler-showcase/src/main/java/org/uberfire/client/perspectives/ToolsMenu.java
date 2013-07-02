package org.uberfire.client.perspectives;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ToolsMenu {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<ProjectService> projectService;

    private MenuItem projectScreen = MenuFactory.newSimpleItem("Data model").respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo("dataModelerScreen");
                }
            }).endMenu().build().getItems().get(0);

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add(projectScreen);

        return menuItems;
    }

    public void selectedPathChanged(@Observes final PathChangeEvent event) {
        projectService.call(new RemoteCallback<Project>() {
            @Override
            public void callback(Project project) {
                projectScreen.setEnabled(project != null);
            }
        }).resolveProject(event.getPath());
    }
}
