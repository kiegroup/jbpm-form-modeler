package org.jbpm.formModeler.editor.client.editors;

import com.google.gwt.user.client.Window;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.api.processing.FormEditorContext;
import org.jbpm.formModeler.api.processing.FormEditorContextTO;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.uberfire.client.workbench.widgets.menu.MenuFactory.newSimpleItem;


@Dependent
@WorkbenchEditor(identifier = "FormModelerEditor", supportedTypes = {FormDefinitionResourceType.class })
public class FormModelerPanelPresenter {

    public interface FormModelerPanelView
            extends
            UberView<FormModelerPanelPresenter> {

        void hideForm();

        void loadContext(FormEditorContextTO context);
    }

    @Inject
    private IOCBeanManager iocBeanManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    FormModelerPanelView view;

    @Inject
    Caller<FormModelerService> modelerService;

    @Inject
    private Event<NotificationEvent> notification;

    private FormEditorContextTO context;

    private Menus menus;

    private Path path;

    @OnStart
    public void onStart(Path path, PlaceRequest placeRequest) {

        makeMenuBar();

        this.path = path;

        modelerService.call(new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback(FormEditorContextTO ctx) {
                if (ctx == null) {
                    notification.fire(new NotificationEvent("Cannot load the form from server."));
                } else {
                    loadContext(ctx);
                    notification.fire(new NotificationEvent("Model was loaded from server: " + ctx + " at time: " + new java.util.Date()));
                }
            }
        }).loadForm(path);

    }

    @OnSave
    public void onSave() {
        //makeMenuBar();

        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formId) {
            }
        }).saveForm(context.getCtxUID());

    }

    @OnFocus
    public void onFocus() {
        //makeMenuBar();
//        Window.alert("onFocus "+ path.toURI());

        makeMenuBar();

        if(context==null) return;

        modelerService.call(new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback(FormEditorContextTO context) {
                loadContext(context);
            }

        }).setFormFocus((context!=null? context.getCtxUID():null));

    }

    @OnClose
    public void onClose() {
        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formId) {
            }

        }).removeEditingForm(context.getCtxUID());

    }
    public void loadContext(FormEditorContextTO ctx) {
        this.context = ctx;
        view.loadContext(ctx);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Modeler Panel ["+ path.getFileName() + "]";
    }

    @WorkbenchPartView
    public UberView<FormModelerPanelPresenter> getView() {
        return view;
    }

    private List<MenuItem> getMenuItems() {

        final List<MenuItem> menuItems = new ArrayList<MenuItem>();

        //TODO take a look at guvnor editors to see if class org.kie.guvnor.commons.ui.client.menu.FileMenuBuilder
        //can be used

        org.uberfire.client.mvp.Command saveCommand = new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {
                onSave();
            }
        };

//        if ( saveCommand != null ) {
            menuItems.add(newSimpleItem("Save")
                    .respondsWith(saveCommand)
                    .endMenu().build().getItems().get(0));
//        }

        return menuItems;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newContributedMenu("Form")
                .withItems( getMenuItems() )
                .endMenu().build();
    }


}
