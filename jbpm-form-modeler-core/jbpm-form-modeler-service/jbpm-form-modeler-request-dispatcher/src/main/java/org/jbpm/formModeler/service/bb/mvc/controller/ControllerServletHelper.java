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
package org.jbpm.formModeler.service.bb.mvc.controller;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.FactoryLifecycle;
import org.jbpm.formModeler.service.bb.mvc.Framework;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.controller.impl.CommandRequestImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class ControllerServletHelper extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ControllerServletHelper.class.getName());

    private String controllerStatus;

    public Framework framework;

    public String getControllerStatus() {
        return controllerStatus;
    }

    public void setControllerStatus(String controllerStatus) {
        this.controllerStatus = controllerStatus;
    }

    public ControllerStatus getStatus() {
        return (ControllerStatus) Factory.lookup(controllerStatus);
    }

    public CommandRequest initThreadLocal(HttpServletRequest request, HttpServletResponse response) {
        // Initialize threadLocal with request object
        CommandRequest cmdReq = new CommandRequestImpl(request, response);
        RequestContext.init(cmdReq);
        return cmdReq;
    }

    public void clearThreadLocal(HttpServletRequest request, HttpServletResponse response) {
        Enumeration en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            Object obj = request.getAttribute(name);
            if (obj instanceof FactoryLifecycle) {
                try {
                    ((FactoryLifecycle) obj).shutdown();
                } catch (Exception e) {
                    log.error("Error: ", e);
                }
            }
        }
        RequestContext.destroy();
    }
}
