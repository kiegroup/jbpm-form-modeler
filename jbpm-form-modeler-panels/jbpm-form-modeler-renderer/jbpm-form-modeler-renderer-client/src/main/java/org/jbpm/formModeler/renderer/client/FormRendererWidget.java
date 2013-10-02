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
package org.jbpm.formModeler.renderer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.client.FormRenderContextTO;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class FormRendererWidget extends Composite {

    final static private String GWT_DEFAULT_LOCALE  = "default";
    final static private String FORM_MODELER_DEFAULT_LOCALE  = "en";

    boolean canSubmit = false;
    private String ctxUID;

    @Inject
    private Frame frame;

    @PostConstruct
    public void initRenderer() {
        VerticalPanel widget = new VerticalPanel();
        initWidget(widget);
        widget.add(frame);
        frame.setWidth("100%");
        frame.setHeight("600px");
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        publish(this);
    }

    // Set up the JS-callable signature as a global JS function.
    private native void publish(FormRendererWidget widget) /*-{
        $wnd.resizeRendererWidget = function (width, height) {
            widget.@org.jbpm.formModeler.renderer.client.FormRendererWidget::resizeWidget(Ljava/lang/String;Ljava/lang/String;)(width,height)
        }
    }-*/;


    protected void resizeWidget(String width, String height) {
        frame.setWidth(width);
        frame.setHeight(height);
    }

    public void submitForm() {
        if (canSubmit) submitForm(ctxUID, false);
    }

    public void submitFormAndPersist() {
        if (canSubmit) submitForm(ctxUID, true);
    }

    private native void submitForm(String uid, boolean persist) /*-{
        var frame = $doc.getElementById('frame_' + uid)

        if (frame) {
            var frameDoc = frame.contentWindow.document;
            frameDoc.getElementById('persist_' + uid).value = persist;
            frameDoc.getElementById('formRendering' + uid).submit();
        }
    }-*/;

    public void loadContext(FormRenderContextTO ctx) {
       loadContext(ctx.getCtxUID());
    }

    public void loadContext(String ctxUID) {
        this.ctxUID = ctxUID;

        frame.getElement().setId("frame_" + ctxUID);
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        if (GWT_DEFAULT_LOCALE.equals(localeName)) localeName = FORM_MODELER_DEFAULT_LOCALE;
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "Controller?_fb=frc&_fp=Start&ctxUID=" + ctxUID + "&locale=" + localeName).asString());
        canSubmit = true;
    }

    public boolean isValidContextUID(String ctxUID) {
        if (ctxUID != null && ctxUID.startsWith(FormRenderContextManager.CTX_PREFFIX)) return true;
        return false;
    }

    public void endContext() {
        canSubmit = false;
        frame.setUrl("");
        ctxUID = null;
    }
}


