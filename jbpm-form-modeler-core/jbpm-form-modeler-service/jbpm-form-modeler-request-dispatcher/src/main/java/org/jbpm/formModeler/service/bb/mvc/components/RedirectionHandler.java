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
package org.jbpm.formModeler.service.bb.mvc.components;

import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.ShowScreenResponse;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedirectionHandler extends BeanHandler {

    public static final String PARAM_PAGE_TO_REDIRECT = "JB_page_to_redirect";

    public CommandResponse actionRedirectToSection(CommandRequest request) throws Exception {
        String page = request.getRequestObject().getParameter(PARAM_PAGE_TO_REDIRECT);
        if (page != null && !"".equals(page.trim())) {
            return new ShowScreenResponse(page);
        } else return new DoNothingResponse();
    }
}
