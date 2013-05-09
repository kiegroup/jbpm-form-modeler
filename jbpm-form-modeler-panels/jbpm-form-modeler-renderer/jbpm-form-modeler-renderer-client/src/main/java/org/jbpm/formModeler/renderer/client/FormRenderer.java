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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;


public class FormRenderer extends Composite {

    final private HandlerManager handlerManager = new HandlerManager(this);

    private FormRenderContextTO ctx;

    private Frame frame = new Frame();

    public FormRenderer() {
        VerticalPanel widget = new VerticalPanel();
        initWidget(widget);
        widget.add(frame);
        frame.setWidth("100%");
        frame.setHeight("600px");
        frame.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
    }

    public boolean submitForm() {
        submitForm(ctx.getCtxUID());

        final boolean[] submitEnded = new boolean[]{false};
        final int errors[] = new int[] {0};

        return errors[0] > 0;
    }

    private native boolean isSubmitted(String uid) /*-{
        var frame = $doc.getElementById('frame_' + uid)

        if (frame) {
            var frameDoc = frame.contentWindow.document;
            return frameDoc.getElementById('submitted_' + uid).value;
        }

        return false
    }-*/;

    private native int getErrors(String uid) /*-{
        var frame = $doc.getElementById('frame_' + uid)

        if (frame) {
            var frameDoc = frame.contentWindow.document;
            return frameDoc.getElementById('errors_' + uid).value;
        }

        return 0
    }-*/;

    private native void submitForm(String uid) /*-{
        var frame = $doc.getElementById('frame_' + uid)

        if (frame) {
            var frameDoc = frame.contentWindow.document;
            frameDoc.getElementById('formRendering' + uid).submit();
        }
    }-*/;

    public void loadContext(FormRenderContextTO ctx) {

        this.ctx = ctx;

        String ctxUID = ctx.getCtxUID();

        frame.getElement().setId("frame_" + ctxUID);
        frame.setUrl(UriUtils.fromString(GWT.getModuleBaseURL() + "Controller?_fb=frc&_fp=Start&ctxUID=" + ctx.getCtxUID()).asString());
    }
}


