/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated(value = "FormModelerPanelViewImpl.html")
public class FormModelerPanelViewImpl
        extends KieEditorViewImpl
        implements FormModelerPanelView, RequiresResize {

    final static private String GWT_DEFAULT_LOCALE  = "default";
    final static private String FORM_MODELER_DEFAULT_LOCALE  = "en";

    @Inject
    @DataField
    private Frame frame;

    private long id;

    public FormModelerPanelViewImpl() {

    }

    @PostConstruct
    protected void init() {
        id = System.currentTimeMillis();
        frame.getElement().setId("frame_" + id);
    }

    @Override
    public void loadContext(String ctxUID) {
        doOnResize();
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        if (GWT_DEFAULT_LOCALE.equals(localeName)) localeName = FORM_MODELER_DEFAULT_LOCALE;
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "Controller?_fb=wysiwygfe&_fp=Start&ctxUID=" + ctxUID + "&locale=" + localeName).asString());
    }

    @Override
    public void showCanNotSaveReadOnly() {
        Window.alert(CommonConstants.INSTANCE.CantSaveReadOnly());
    }

    @Override
    public void onResize() {
        doOnResize();
    }

    protected void  doOnResize() {
        Widget w = getParent();
        if (w != null) {
            int width = w.getOffsetWidth();
            int height = w.getOffsetHeight() - 30;
            if (width > 0) frame.setWidth( width + "px" );
            if (height > 0) frame.setHeight(height + "px");
        }
    }
}


