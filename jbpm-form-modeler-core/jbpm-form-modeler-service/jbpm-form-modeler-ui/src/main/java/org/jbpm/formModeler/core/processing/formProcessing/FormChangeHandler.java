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

import org.jbpm.formModeler.api.util.helpers.CDIHelper;
import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.service.bb.mvc.components.handling.HandlerFactoryElement;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.jbpm.formModeler.api.processing.FormProcessor;

public class FormChangeHandler extends HandlerFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FormChangeHandler.class.getName());

    private FormManagerImpl formsManager;
    private FormChangeProcessor changeProcessor;
    private NamespaceManager namespaceManager;
    private FormProcessor defaultFormProcessor = (FormProcessor) CDIHelper.getBeanByType(FormProcessor.class);

    @Override
    public void start() throws Exception {
        super.start();
        formsManager = FormManagerImpl.lookup();
    }

    public FormManagerImpl getFormsManager() {
        return formsManager;
    }

    public void setFormsManager(FormManagerImpl formsManager) {
        this.formsManager = formsManager;
    }

    public FormChangeProcessor getChangeProcessor() {
        return changeProcessor;
    }

    public void setChangeProcessor(FormChangeProcessor changeProcessor) {
        this.changeProcessor = changeProcessor;
    }

    public NamespaceManager getNamespaceManager() {
        return namespaceManager;
    }

    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    public CommandResponse actionProcess(CommandRequest request) throws Exception {
        String modifiedFieldName = request.getParameter("modifiedFieldName");
        FormNamespaceData formNamespaceData = getNamespaceManager().getNamespace(modifiedFieldName);

        FormChangeResponse changeResponse = new FormChangeResponse();

        while (formNamespaceData != null) {
            //if (getChangeProcessor() != null) {
                defaultFormProcessor.setValues(formNamespaceData.getForm(), formNamespaceData.getNamespace(), request.getRequestObject().getParameterMap(), request.getFilesByParamName(), false);
            //    getChangeProcessor().process(formNamespaceData.getForm(), formNamespaceData.getNamespace(), changeResponse);
                // Clear errors that might be stored in formStatuses
                defaultFormProcessor.clearFieldErrors(formNamespaceData.getForm(), formNamespaceData.getNamespace());
            //}
            // Evaluate parent's formulas
            formNamespaceData = getNamespaceManager().getNamespace(formNamespaceData.getNamespace());
        }

        request.getResponseObject().setContentType("text/xml");
        if (log.isDebugEnabled())
            log.debug("Sending form change response " + changeResponse.getXML());
        request.getResponseObject().getWriter().write(changeResponse.getXML());

        return new DoNothingResponse();
    }

    public FormProcessor getDefaultFormProcessor() {
        return defaultFormProcessor;
    }

    public void setDefaultFormProcessor(FormProcessor defaultFormProcessor) {
        this.defaultFormProcessor = defaultFormProcessor;
    }
}
