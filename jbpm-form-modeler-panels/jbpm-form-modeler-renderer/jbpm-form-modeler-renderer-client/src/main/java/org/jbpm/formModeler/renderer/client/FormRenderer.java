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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;


public class FormRenderer extends Composite {

    private FormRenderContextTO ctx;

    private Frame frame = new Frame();

    public FormRenderer() {
        VerticalPanel widget = new VerticalPanel();
        initWidget(widget);
        widget.add(frame);
        frame.setWidth("100%");
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
    }

    public void submitForm() {
        submitForm(ctx.getCtxUID());
    }

    private native void submitForm(String uid)  /*-{
        $wnd.submitFormRenderer(uid)
    }-*/;

    public void loadContext(FormRenderContextTO ctx) {
        this.ctx = ctx;

        String ctxUID = ctx.getCtxUID();

        frame.getElement().setId("frame_" + ctxUID);
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "Controller?_fb=frc&_fp=Start&ctxUID=" + ctxUID).asString());

        HeadElement head = frame.getElement().getOwnerDocument().getElementsByTagName( HeadElement.TAG ).getItem(0).cast();

        StringBuffer js = new StringBuffer();

        js.append("function submitFormRenderer(uid) {")
                .append("var frd = document.getElementById('frame_' + ").append(ctxUID).append(").contentWindow.document;")
                .append("var forms = frd.getElementById('formRendering").append(ctxUID).append("');")
                .append("if (forms && forms.length == 1) forms[0].submit();")
                .append("}");

        ScriptElement scriptElement = Document.get().createScriptElement(js.toString());
        scriptElement.setType( "text/javascript" );
        head.appendChild( scriptElement );
    }
}


