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

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.jbpm.formModeler.api.client.FormEditorContextTO;
import org.jbpm.formModeler.editor.client.resources.i18n.Constants;
import org.jbpm.formModeler.editor.client.type.FormDefinitionResourceType;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnSave;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.uberfire.workbench.model.menu.MenuFactory.newSimpleItem;


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

    @OnStartup
    public void onStartup(Path path, PlaceRequest placeRequest) {

        this.path = path;

        modelerService.call(new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback(FormEditorContextTO ctx) {
                if (ctx == null) {
                    notification.fire(new NotificationEvent("Cannot load the form from server."));
                } else {
                    loadContext(ctx);
                    makeMenuBar();
                }
            }
        }).loadForm(path);

    }

    @OnSave
    public void onSave() {
        try {
            modelerService.call(new RemoteCallback<Long>() {
                @Override
                public void callback(Long formId) {
                }
            }).saveForm(context.getCtxUID());
        } catch (Exception e) {
            notification.fire(new NotificationEvent("Cannot save form."));
        }

    }

    @OnFocus
    public void onFocus() {
/*        makeMenuBar();

        if(context==null) return;

        modelerService.call(new RemoteCallback<FormEditorContextTO>() {
            @Override
            public void callback(FormEditorContextTO context) {
//                loadContext(context);
            }

        }).setFormFocus((context!=null? context.getCtxUID():null));
*/
    }

    @OnOpen
    public void onOpen() {
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
        return "Form Modeler ["+ path.getFileName() + "]";
    }

    @WorkbenchPartView
    public UberView<FormModelerPanelPresenter> getView() {
        return view;
    }

    private List<MenuItem> getMenuItems() {

        final List<MenuItem> menuItems = new ArrayList<MenuItem>();

        Command saveCommand = new Command() {
            @Override
            public void execute() {
                onSave();
            }
        };

        menuItems.add(newSimpleItem(Constants.INSTANCE.form_modeler_save() + " [" + path.getFileName() + "]")
                .respondsWith(saveCommand)
                .endMenu().build().getItems().get(0));

        return menuItems;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(Constants.INSTANCE.form_modeler_form())
                .withItems( getMenuItems() )
                .endMenu().build();
    }


}
