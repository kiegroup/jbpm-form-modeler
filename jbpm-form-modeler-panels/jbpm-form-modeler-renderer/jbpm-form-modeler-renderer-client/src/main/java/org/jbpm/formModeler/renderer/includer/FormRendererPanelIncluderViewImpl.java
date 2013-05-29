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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.formModeler.api.client.FormRenderContextTO;
import org.jbpm.formModeler.api.events.FormRenderEvent;
import org.jbpm.formModeler.renderer.client.FormRendererWidget;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@Templated(value = "FormRendererPanelIncluderViewImpl.html")
public class FormRendererPanelIncluderViewImpl extends Composite implements FormRendererPanelIncluderPresenter.FormRendererIncluderPanelView {

    @Inject
    @DataField
    public FormRendererWidget formRenderer;

    @Inject
    @DataField
    public Button submitButton;

    @Inject
    @DataField
    public Button submitAndPersistButton;

    @Inject
    @DataField
    public Button persistButton;

    @Inject
    @DataField
    public Button clearButton;

    @Inject
    @DataField
    public Button startTestButton;

    private FormRendererPanelIncluderPresenter presenter;

    @Inject
    Event<FormRenderEvent> formRenderEvent;

    public FormRendererPanelIncluderViewImpl() {
        super();
    }

    @Override
    public void init(FormRendererPanelIncluderPresenter presenter) {
        this.presenter = presenter;
        submitButton.setText("submit");
        submitAndPersistButton.setText("submit form and persist");
        persistButton.setText("persist form status data without submit");
        clearButton.setText("clear form status data without submit");
        startTestButton.setText("start");
        setVisisbleButtons(false);
    }

    @EventHandler("startTestButton")
    public void startTest(ClickEvent event) {
        presenter.startTest();
        setVisisbleButtons(true);
    }

    @Override
    public void hide() {
        setVisisbleButtons(false);
        formRenderer.endContext();
    }

    protected void setVisisbleButtons(boolean visible) {
        formRenderer.setVisible(visible);
        submitButton.setVisible(visible);
        submitAndPersistButton.setVisible(visible);
        persistButton.setVisible(visible);
        clearButton.setVisible(visible);
    }

    @EventHandler("submitButton")
    public void submitForm(ClickEvent event) {
        formRenderer.submitForm();
    }

    @EventHandler("submitAndPersistButton")
    public void submitAndPersistForm(ClickEvent event) {
        formRenderer.submitFormAndPersist();
    }

    @EventHandler("persistButton")
    public void persistForm(ClickEvent event) {
        presenter.persistForm();
    }

    @EventHandler("clearButton")
    public void clearForm(ClickEvent event) {
        presenter.clearFormStatus();
    }

    @Override
    public void loadContext(FormRenderContextTO ctx) {
        if (ctx != null) {
            formRenderer.loadContext(ctx);
        }
    }
}
