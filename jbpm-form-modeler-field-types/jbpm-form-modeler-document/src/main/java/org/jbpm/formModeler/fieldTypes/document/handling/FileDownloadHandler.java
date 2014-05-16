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
package org.jbpm.formModeler.fieldTypes.document.handling;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.service.DocumentStorageService;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.HTTPSettings;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.SendStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

@Named("fdch")
@ApplicationScoped
public class FileDownloadHandler extends BeanHandler {
    private Logger log = LoggerFactory.getLogger(FileDownloadHandler.class);

    @Inject
    private HTTPSettings httpSettings;

    @Inject
    private DocumentStorageService fileStorageService;

    /**
     * Action to download the specified file
     * @param request A CommandRequest object that contains the the HttpServletRequest, HttpServletResponse, HttpSession and the information about the files uploaded on the current submit
     * @return A CommandResponse to download the document content if the file exists or an empty response if there is any error.
     */
    public CommandResponse actionDownload(CommandRequest request) {
        try {
            // try to get the content parameter that identifies the file to download and try to find the File.
            String content = request.getRequestObject().getParameter("content");
            if (!StringUtils.isEmpty(content)) {
                Document doc = fileStorageService.getDocument(content);

                if (doc != null) {
                    byte[] docContent = doc.getContent();
                    if (docContent != null) return new SendStreamResponse(new ByteArrayInputStream(docContent), "inline;filename=" + URLEncoder.encode(doc.getName(), httpSettings.getEncoding()));
                }
            }
        } catch (Exception e) {
            log.warn("Error trying to get file content: ", e);
        }

        // if the file doesn't exist or something wrong happens return the DonNothingResponse
        return new DoNothingResponse();
    }

    @Override
    public boolean isEnabledForActionHandling() {
        return true;
    }
}
