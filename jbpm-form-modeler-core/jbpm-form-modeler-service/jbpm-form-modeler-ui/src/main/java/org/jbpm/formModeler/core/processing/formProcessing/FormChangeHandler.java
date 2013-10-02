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
package org.jbpm.formModeler.core.processing.formProcessing;

import org.slf4j.Logger;
import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

//@SessionScoped
@ApplicationScoped
public class FormChangeHandler extends BeanHandler {

    private Logger log = LoggerFactory.getLogger(FormChangeHandler.class);

    @Inject
    private FormProcessor formProcessor;

    @Inject
    private FormulasCalculatorChangeProcessor changeProcessor;

    @Override
    public boolean isEnabledForActionHandling() {
        //WM
        return true;
    }

    public FormChangeProcessor getChangeProcessor() {
        return changeProcessor;
    }

    public CommandResponse actionProcess(CommandRequest request) throws Exception {
        String modifiedFieldName = request.getParameter("modifiedFieldName");
        FormNamespaceData formNamespaceData = NamespaceManager.lookup().getNamespace(modifiedFieldName);
        FormChangeResponse changeResponse = new FormChangeResponse();

        //the formNamespaceData.getForm() != null control was added in order to prevent cases where the
        //forms has already been closed. TODO check this extra control with sr_pere
        while (formNamespaceData != null && formNamespaceData.getForm() != null) {
            if (getChangeProcessor() != null) {
                formProcessor.setValues(formNamespaceData.getForm(), formNamespaceData.getNamespace(), request.getRequestObject().getParameterMap(), request.getFilesByParamName(), false);
                getChangeProcessor().process(formNamespaceData.getForm(), formNamespaceData.getNamespace(), changeResponse);
                // Clear errors that might be stored in formStatuses
                formProcessor.clearFieldErrors(formNamespaceData.getForm(), formNamespaceData.getNamespace());
            }
            // Evaluate parent's formulas
            formNamespaceData = NamespaceManager.lookup().getNamespace(formNamespaceData.getNamespace());
        }

        request.getResponseObject().setContentType("text/xml");
        if (log.isDebugEnabled()) log.debug("Sending form change response " + changeResponse.getXML());
        request.getResponseObject().getWriter().write(changeResponse.getXML());

        return new DoNothingResponse();
    }
}
