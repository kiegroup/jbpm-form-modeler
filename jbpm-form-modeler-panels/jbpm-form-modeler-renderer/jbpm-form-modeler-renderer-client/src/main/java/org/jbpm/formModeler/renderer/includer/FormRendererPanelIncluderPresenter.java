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
package org.jbpm.formModeler.renderer.includer;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.api.events.FormSubmitFailEvent;
import org.jbpm.formModeler.api.events.FormSubmittedEvent;
import org.jbpm.formModeler.renderer.service.FormRendererIncluderService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;


@Dependent
@WorkbenchScreen(identifier = "FormRendererIncluderPanel")
public class FormRendererPanelIncluderPresenter {

    public interface FormRendererIncluderPanelView
            extends
            UberView<FormRendererPanelIncluderPresenter> {

        void loadContext(FormRenderContextTO ctx);

        void hide();
    }

    private FormRenderContextTO context;

    @Inject
    private FormRendererIncluderPanelView view;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    Caller<FormRendererIncluderService> includerService;


    @PostConstruct
    public void init() {

    }

    public void startTest() {
        includerService.call(new RemoteCallback<FormRenderContextTO>() {
            @Override
            public void callback(FormRenderContextTO ctx) {
                context = ctx;
                view.loadContext(ctx);
            }
        }).launchTest();
    }

    public void persistForm() {
        includerService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) notification.fire(new NotificationEvent("Form persisted OK!", NotificationEvent.NotificationType.SUCCESS));
                else notification.fire(new NotificationEvent("Something wrong happened persisting form", NotificationEvent.NotificationType.ERROR));
            }
        }).persistContext(context.getCtxUID());
    }


    public void clearFormStatus() {
        includerService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    notification.fire(new NotificationEvent("Form cleared!", NotificationEvent.NotificationType.SUCCESS));
                    view.hide();
                } else notification.fire(new NotificationEvent("Something wrong happened clearing form", NotificationEvent.NotificationType.ERROR));
            }
        }).clearContext(context.getCtxUID());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Renderer Panel Includer";
    }

    @WorkbenchPartView
    public UberView<FormRendererPanelIncluderPresenter> getView() {
        return view;
    }

    public void notifyErrors(int errorNumber) {
        notification.fire(new NotificationEvent("Unable to process form, it has " + errorNumber + " errors!", NotificationEvent.NotificationType.WARNING));
    }

    public void notifyFormSubmit() {
        notification.fire(new NotificationEvent("Form submitted OK!", NotificationEvent.NotificationType.SUCCESS));
    }

    public void notifyFormProcessingError(String cause) {
        notification.fire(new NotificationEvent("Something wrong happened processing form, cause: '" + cause + "'", NotificationEvent.NotificationType.ERROR));
    }

    //Event Observers
    public void onFormSubmitted(@Observes FormSubmittedEvent event) {
        if (event.isMine(context)) {
            int errors = event.getContext().getErrors();
            if (errors == 0) {
                notifyFormSubmit();
            } else {
                notifyErrors(errors);
            }
        }
    }

    public  void onFormSubmitFail(@Observes FormSubmitFailEvent event) {
        if (event.isMine(context)) {
            notifyFormProcessingError(event.getCause());
        }
    }
}
