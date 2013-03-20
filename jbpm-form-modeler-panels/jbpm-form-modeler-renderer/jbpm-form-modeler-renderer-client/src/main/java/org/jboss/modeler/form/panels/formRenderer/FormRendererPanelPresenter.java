package org.jboss.modeler.form.panels.formRenderer;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.framework.MessageBus;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.jboss.modeler.form.client.validation.FormValidationResult;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;


@Dependent
@WorkbenchScreen(identifier = "FormRendererPanel")
public class FormRendererPanelPresenter {

    @Inject
    MessageBus bus;

    public interface FormRendererPanelView
            extends
            UberView<FormRendererPanelPresenter> {
        void showMessage(String message);
        void showErrors (List<String> errors);
        long getId();

        void displayResult(FormValidationResult result);
    }

    @Inject
    private PlaceManager placeManager;

    @Inject
    FormRendererPanelView view;

    @Inject
    private Event<NotificationEvent> notification;

    @PostConstruct
    public void init() {

    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Renderer Panel";
    }

    @WorkbenchPartView
    public UberView<FormRendererPanelPresenter> getView() {
        return view;
    }

    public void validateForm() {
        notification.fire(new NotificationEvent("Starting form validation to server at: " + new java.util.Date()));

        MessageBuilder.createMessage()
                .toSubject("FormService").with("message", "Hello from servlet at " + new Date())
                .with("id", String.valueOf(view.getId()))
                .done()
                .repliesTo(new MessageCallback() {
                    public void callback(Message message) {
                        String msg = (String) message.getParts().get("message");
                        FormValidationResult result = message.get(FormValidationResult.class, "result");
                        notification.fire(new NotificationEvent(msg + " " + new java.util.Date()));
                        if (result != null) view.displayResult(result);
                    }
                })
                .sendNowWith(bus);
    }

}
