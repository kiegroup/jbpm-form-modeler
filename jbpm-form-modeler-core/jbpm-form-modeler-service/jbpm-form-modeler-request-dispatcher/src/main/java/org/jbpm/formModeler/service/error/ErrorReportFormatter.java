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

import org.jbpm.formModeler.service.annotation.config.Config;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("ErrorReportFormatter")
public class ErrorReportFormatter extends Formatter {

    @Inject @Config("/formModeler/components/errorReport/images/32x32/info.gif")
    protected String messagesImg;

    @Inject @Config("/formModeler/components/errorReport/images/32x32/warning.gif")
    protected String warningsImg;

    @Inject @Config("/formModeler/components/errorReport/images/32x32/error.gif")
    protected String errorsImg;

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        ErrorReportHandler errorReportHandler = ErrorReportHandler.lookup();
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
