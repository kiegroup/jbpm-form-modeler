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

import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
@Named("controllerServletHelper")
public class ControllerServletHelper {

    public static ControllerServletHelper lookup() {
        return (ControllerServletHelper) CDIBeanLocator.getBeanByName("controllerServletHelper");
    }

    public CommandRequest initThreadLocal(HttpServletRequest request, HttpServletResponse response) {
        CommandRequest cmdReq = new CommandRequestImpl(request, response);
        RequestContext.init(cmdReq);
        return cmdReq;
    }

    public void clearThreadLocal(HttpServletRequest request, HttpServletResponse response) {
        RequestContext.destroy();
    }
}
