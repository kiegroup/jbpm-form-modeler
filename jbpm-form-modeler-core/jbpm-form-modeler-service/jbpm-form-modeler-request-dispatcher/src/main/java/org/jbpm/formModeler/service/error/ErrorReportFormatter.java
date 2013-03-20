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

import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorReportFormatter extends Formatter {

    private static transient Log log = LogFactory.getLog(ErrorReportFormatter.class.getName());

    protected LocaleManager localeManager;

    protected String messagesImg;
    protected String warningsImg;
    protected String errorsImg;

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public String getMessagesImg() {
        return messagesImg;
    }

    public void setMessagesImg(String messagesImg) {
        this.messagesImg = messagesImg;
    }

    public String getWarningsImg() {
        return warningsImg;
    }

    public void setWarningsImg(String warningsImg) {
        this.warningsImg = warningsImg;
    }

    public String getErrorsImg() {
        return errorsImg;
    }

    public void setErrorsImg(String errorsImg) {
        this.errorsImg = errorsImg;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        ErrorReportHandler errorReportHandler = (ErrorReportHandler) getParameter("errorHandler");
        ErrorReport errorReport = errorReportHandler.getErrorReport();
        if (errorReport != null) {
            setAttribute("errorIcon", getErrorIcon(errorReport));
            setAttribute("errorMessage", errorReport.printErrorMessage());
            setAttribute("closeEnabled", errorReportHandler.isCloseEnabled());
            if (!errorReport.isBusinessAppError()) setAttribute("technicalDetails", errorReport.printContext(0));
            renderFragment("errorMessage");
        }
    }

    protected String getErrorIcon(ErrorReport errorReport) {
        if (errorReport.isBusinessAppError()) {
            BusinessError appError = (BusinessError) ErrorManager.lookup().getApplicationErrorCause(errorReport.getException());
            switch (appError.getLevel()) {
                case BusinessError.ERROR: return errorsImg;
                case BusinessError.WARN: return warningsImg;
                case BusinessError.INFO: return messagesImg;
            }
        }
        return errorsImg;
    }
}
