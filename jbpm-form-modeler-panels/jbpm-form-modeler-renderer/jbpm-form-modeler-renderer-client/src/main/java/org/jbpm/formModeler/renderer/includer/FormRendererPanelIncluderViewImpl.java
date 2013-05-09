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
import org.jbpm.formModeler.api.events.*;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;
import org.jbpm.formModeler.renderer.client.FormRenderer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
@Templated(value = "FormRendererPanelIncluderViewImpl.html")
public class FormRendererPanelIncluderViewImpl extends Composite implements FormRendererPanelIncluderPresenter.FormRendererIncluderPanelView {

    @Inject
    @DataField
    public FormRenderer formRenderer;

    @Inject
    @DataField
    public Button submitButton;

    @Inject
    @DataField
    public Button startTestButton;

    private FormRendererPanelIncluderPresenter presenter;

    @Inject
    Event<FormRenderEvent> formRenderEvent;

    @Override
    public void init(FormRendererPanelIncluderPresenter presenter) {
        this.presenter = presenter;
        submitButton.setText("submit");
        startTestButton.setText("start");
        formRenderer.setVisible(false);
        submitButton.setVisible(false);
    }

    @EventHandler("startTestButton")
    public void startTest(ClickEvent event) {
        presenter.startTest();
        formRenderer.setVisible(true);
        submitButton.setVisible(true);
    }

    @EventHandler("submitButton")
    public void submitForm(ClickEvent event) {
        formRenderer.submitForm();
    }

    @Override
    public void loadContext(FormRenderContextTO ctx) {
        if (ctx != null) {
            formRenderer.loadContext(ctx);
        }
    }
}
