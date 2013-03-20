package org.jboss.modeler.form.panels.formRenderer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.*;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.modeler.form.client.validation.FormValidationResult;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;


@Dependent
@Templated(value = "FormRendererPanelViewImpl.html")
public class FormRendererPanelViewImpl extends Composite
        implements
        FormRendererPanelPresenter.FormRendererPanelView {

    private FormRendererPanelPresenter presenter;

    private long id;

    @Inject
    private TextBox message;

    @Inject
    private TextBox header;

    @Inject
    @DataField
    private HTML response;

    @Inject
    @DataField
    private Frame frame;

    @Inject
    @DataField
    public Button sendButton;

    @Inject
    @DataField
    public Button clearButton;


    public FormRendererPanelViewImpl() {

    }

    @PostConstruct
    protected void init() {
        id = System.currentTimeMillis();

        frame.setWidth("100%");
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "FormModeler?dispatch=d").asString());
        frame.getElement().setId("frame_" + id);

        HeadElement head = frame.getElement().getOwnerDocument().getElementsByTagName( HeadElement.TAG ).getItem(0).cast();

        StringBuffer js = new StringBuffer();

        js.append("function submitFormRenderer(id) {")
                .append("var frd = document.getElementById('frame_' + ").append(id).append(").contentWindow.document;")
                .append("frd.getElementById('clientId').value = '").append(id).append("';")
                .append("var forms = frd.getElementsByTagName('form');")
                .append("if (forms && forms.length == 1) forms[0].submit();")
                .append("}");

        js.append("function clearFormRenderer(id) {")
                .append("var frd = document.getElementById('frame_' + ").append(id).append(").contentWindow.document;")
                .append("var forms = frd.getElementsByTagName('form');")
                .append("if (forms && forms.length == 1) forms[0].reset();")
                .append("}");

        ScriptElement scriptElement = Document.get().createScriptElement(js.toString());
        scriptElement.setType( "text/javascript" );
        head.appendChild( scriptElement );
    }

    @Override
    public void init(final FormRendererPanelPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("clearButton")
    public void clearForm(ClickEvent e) {
        clearForm(String.valueOf(id));
    }

    @EventHandler("sendButton")
    public void sendMessage(ClickEvent e) {
        presenter.validateForm();
        submitForm(String.valueOf(id));
    }

    private native void clearForm(String id) /*-{
        $wnd.clearFormRenderer(id)
    }-*/;

    private native void submitForm(String id)  /*-{
        $wnd.submitFormRenderer(id)
    }-*/;

    @Override
    public void showMessage(String message) {
        response.getElement().getStyle().setColor("green");
        response.setText(message);
    }

    @Override
    public void showErrors(List<String> errors) {
        response.getElement().getStyle().setColor("red");
        StringBuffer err = new StringBuffer();
        for (String error : errors) {
            err.append(error).append("<br/>");
        }
        response.setHTML(new SafeHtmlBuilder().appendHtmlConstant(err.toString()).toSafeHtml());
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void displayResult(FormValidationResult result) {
        if (result == null) return;
        if (result.isValid()) {
            showMessage("Tot OK, form processat correctament!");
        } else {
            showErrors(result.getErrors());
        }
    }
}


