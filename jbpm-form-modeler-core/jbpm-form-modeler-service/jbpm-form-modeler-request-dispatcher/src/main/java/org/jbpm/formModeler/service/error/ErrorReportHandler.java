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
package org.jbpm.formModeler.service.error;

import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;

public class ErrorReportHandler extends BaseUIComponent {

    protected ErrorReport errorReport;
    protected String componentIncludeJSP;
    protected int width;
    protected int height;
    protected boolean closeEnabled;
    protected Runnable closeListener;
    private String baseComponentJSP;

    public ErrorReportHandler() {
        componentIncludeJSP = "/formModeler/components/errorReport/show.jsp";
        baseComponentJSP = "/formModeler/components/errorReport/show.jsp";
        closeEnabled = true;
        width = 1000;
        height = 400;
        errorReport = null;
        closeListener = null;
    }

    public boolean isCloseEnabled() {
        return closeEnabled;
    }

    public void setCloseEnabled(boolean closeEnabled) {
        this.closeEnabled = closeEnabled;
    }

    public Runnable getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(Runnable closeListener) {
        this.closeListener = closeListener;
    }

    public String getComponentIncludeJSP() {
        return componentIncludeJSP;
    }

    public void setComponentIncludeJSP(String componentIncludeJSP) {
        this.componentIncludeJSP = componentIncludeJSP;
    }

    public ErrorReport getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void actionContinue(CommandRequest request) throws Exception {
        errorReport = null;
        if (closeListener != null) {
            closeListener.run();
        }
    }

    public void setBaseComponentJSP(String baseComponentJSP) {
        this.baseComponentJSP = baseComponentJSP;
    }

    @Override
    public String getBaseComponentJSP() {
        return baseComponentJSP;
    }

    @Override
    public void doStart(CommandRequest commandRequest) {
    }
}
