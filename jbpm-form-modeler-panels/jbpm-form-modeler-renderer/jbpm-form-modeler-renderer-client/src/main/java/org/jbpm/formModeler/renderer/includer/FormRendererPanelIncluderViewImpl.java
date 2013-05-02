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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.renderer.FormRenderContext;
import org.jbpm.formModeler.renderer.client.FormRenderer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
@Templated(value = "FormRendererPanelIncluderViewImpl.html")
public class FormRendererPanelIncluderViewImpl extends Composite implements FormRendererPanelIncluderPresenter.FormRendererIncluderPanelView {

    @Inject
    @DataField
    public ListBox forms;

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

    @Override
    public void init(FormRendererPanelIncluderPresenter presenter) {
        this.presenter = presenter;
        submitButton.setText("submit");
        startTestButton.setText("start");
    }

    @Override
    public void addForms(List<FormTO> formsToAdd) {
        forms.clear();
        for (FormTO form : formsToAdd) {
            forms.addItem(form.getFormName(), String.valueOf(form.getFormId()));
        }
    }

    @EventHandler("startTestButton")
    public void startTest(ClickEvent event) {
        presenter.startTest();
    }

    @EventHandler("submitButton")
    public void submitForm(ClickEvent event) {
        formRenderer.submitForm();
    }

    @EventHandler("forms")
    public void selectForm(ChangeEvent event) {
        presenter.loadForm(Long.decode(forms.getValue(forms.getSelectedIndex())));
    }

    @Override
    public void loadContext(FormRenderContext ctx) {
        if (ctx != null) formRenderer.loadContext(ctx);
    }
}
