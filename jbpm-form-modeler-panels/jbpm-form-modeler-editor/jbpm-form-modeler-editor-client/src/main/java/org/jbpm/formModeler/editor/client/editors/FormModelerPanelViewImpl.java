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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.formModeler.api.client.FormEditorContextTO;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.BusyIndicatorView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated(value = "FormModelerPanelViewImpl.html")
public class FormModelerPanelViewImpl extends Composite
        implements
        FormModelerPanelPresenter.FormModelerPanelView {

    final static private String GWT_DEFAULT_LOCALE  = "default";
    final static private String FORM_MODELER_DEFAULT_LOCALE  = "en";

    @Inject
    private BusyIndicatorView busyIndicatorView;

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

    public void visible(boolean show) {
        frame.setVisible(show);
    }

    @Override
    public void hideForm() {
        frame.setUrl("");
        visible(false);
    }

    @Override
    public void loadContext(FormEditorContextTO context) {
        String contexUID = "";
        if(context != null){
            contexUID = context.getCtxUID();
        }
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        if (GWT_DEFAULT_LOCALE.equals(localeName)) localeName = FORM_MODELER_DEFAULT_LOCALE;
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "Controller?_fb=wysiwygfe&_fp=Start&ctxUID=" + contexUID+"&locale=" + localeName).asString());
        visible(true);
    }

    @Override
    public void showCanNotSaveReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        busyIndicatorView.showBusyIndicator( message );
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }
}


