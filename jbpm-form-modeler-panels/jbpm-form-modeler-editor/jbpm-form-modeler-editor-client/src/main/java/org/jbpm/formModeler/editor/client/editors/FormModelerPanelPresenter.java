package org.jbpm.formModeler.editor.client.editors;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.model.FormTO;
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
import org.uberfire.shared.mvp.impl.PathPlaceRequest;

import javax.annotation.PostConstruct;
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

        void showForm();
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

    private Path path;

    private Menus menus;

    @OnStart
    public void onStart(Path path, PlaceRequest placeRequest) {

        makeMenuBar();

        this.path = path;

        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formModel) {
                setFormId(formModel);
                notification.fire(new NotificationEvent("Model was loaded from server: " + formModel + " at time: " + new java.util.Date()));
            }
        }).loadForm(path);

    }

    @OnSave
    public void onSave() {
        //makeMenuBar();
        Window.alert("onSave "+ path.getFileName());
        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formId) {
            }
        }).saveForm(path);

    }

    @OnFocus
    public void onFocus() {
        //makeMenuBar();
//        Window.alert("onFocus "+ path.toURI());

        makeMenuBar();

        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formModel) {
                setFormId(formModel);
        //        notification.fire(new NotificationEvent("Model was loaded from server: " + formModel + " at time: " + new java.util.Date()));
            }

        }).setFormFocus(path);

    }

    @OnClose
    public void onClose() {
        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formId) {
            }

        }).removeEditingForm(path);

    }
    public void setFormId(Long formId) {
        modelerService.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long formId) {
                view.showForm();
    /*            if (formId != null) {
                    view.showForm();
                } else {
                    view.hideForm();
                }*/
            }
        }).setFormId(formId, path.toURI());
    }

    public void getFormId() {
        modelerService.call(new RemoteCallback<FormTO>() {
            @Override
            public void callback(FormTO currentForm) {
                if (currentForm == null) notification.fire(new NotificationEvent("Null form received"));
                else notification.fire(new NotificationEvent("Received form: " + currentForm.getFormName()));
            }
        }).getCurrentForm(path.toURI());
        view.hideForm();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Modeler Panel ["+path.getFileName()+"]";
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
