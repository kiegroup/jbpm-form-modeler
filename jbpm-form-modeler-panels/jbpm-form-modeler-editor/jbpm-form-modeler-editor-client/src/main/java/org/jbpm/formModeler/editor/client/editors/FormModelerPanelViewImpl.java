package org.jbpm.formModeler.editor.client.editors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ListBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated(value = "FormModelerPanelViewImpl.html")
public class FormModelerPanelViewImpl extends Composite
        implements
        FormModelerPanelPresenter.FormModelerPanelView {


    private FormModelerPanelPresenter presenter;

    @Inject
    @DataField
    private Frame frame;

    private long id;
    private long formId = -1;

    public FormModelerPanelViewImpl() {

    }

    @PostConstruct
    protected void init() {
        id = System.currentTimeMillis();
        frame.getElement().setId("frame_" + id);
        visible(false);

    }

    @Override
    public void init(final FormModelerPanelPresenter presenter) {
        this.presenter = presenter;

    }

    @EventHandler("frame")
    public void onLoadFrame(LoadEvent event) {
        if (!"".equals(frame.getUrl())) visible(true);
        else visible(false);
    }


    public void visible(boolean show) {
        frame.setVisible(show);
    }

    @Override
    public void hideForm() {
        frame.setUrl("");
        visible(false);
    }

    @Override
    public void showForm() {
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "Controller?_fb=wysiwygfe&_fp=Start").asString());
    }
}


