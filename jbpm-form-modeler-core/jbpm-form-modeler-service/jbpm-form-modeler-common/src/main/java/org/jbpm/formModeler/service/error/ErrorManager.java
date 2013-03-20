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

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;

/**
 * Manages the error handling in the platform.
 */
public class ErrorManager extends BasicFactoryElement {

    /** Logger */
    private static transient Log log = LogFactory.getLog(ErrorManager.class.getName());

    private ErrorReport errorReport;

    /**
     * Get an ExceptionManager instance.
     */
    public static ErrorManager lookup() {
        return (ErrorManager) Factory.lookup("org.jbpmErrorManager");
    }

    protected boolean logErrorReportEnabled = true;

    public boolean isLogErrorReportEnabled() {
        return logErrorReportEnabled;
    }

    public void setLogErrorReportEnabled(boolean logErrorReportEnabled) {
        this.logErrorReportEnabled = logErrorReportEnabled;
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the give message string is displayed
     * in a error report.<br>
     * <br>
     * <p>NOTE: This functionality is not implemented yet.
     * Nevertheless, the future error handling subsystem will notify such error to the error handling component.
     * Actually, as a temporal solution, the error manager is overridden by the bpe module in order to notify to the
     * StepExecutionErrorHandler component, so the message specified is displayed as part of the error in the BPM task list .
     *
     * @param message The custom message to display in the error window.
     * @param cause The cause of the error, also displayed in the error window.
     */
    public void throwTechnicalError(String message, Throwable cause) {
        throw new TechnicalError(message, cause);
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the given message string is displayed
     * as an error.<br>
     *
     * @param title The error window title.
     * @param message The custom message to display in the error window.
     */
    public void throwBusinessError(String title, String message) {
        throw new BusinessError(BusinessError.ERROR, title, message);
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the given message string is displayed
     * as a warning.<br>
     *
     * @param title The error window title.
     * @param message The custom message to display in the error window.
     */
    public void throwBusinessWarning(String title, String message) {
        throw new BusinessError(BusinessError.WARN, title, message);
    }

    /**
     * <p>Force the given error to be thrown. the current transaction is aborted and the given message string is displayed
     * as extra information.<br>
     *
     * @param title The error window title.
     * @param message The custom message to display in the error window.
     */
    public void throwBusinessInfo(String title, String message) {
        throw new BusinessError(BusinessError.INFO, title, message);
    }

    /**
     * Get the error cause (if any) thrown by the application logic.
     */
    public ApplicationError getApplicationErrorCause(Throwable e) {
        LinkedList<ApplicationError> appErrors = new LinkedList<ApplicationError>();
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof ApplicationError) appErrors.add((ApplicationError)cause);
            cause = cause.getCause();
        }
        if (appErrors.isEmpty()) return null;
        return appErrors.getLast();
    }


    /**
     * Generate an error report and log the error if requested.
     */
    public ErrorReport notifyError(Throwable t, boolean doLog) {
        // Build the report.
        ErrorReport report = (ErrorReport) Factory.lookup("org.jbpm.formModeler.service.error.ErrorReport");
        report.setId(String.valueOf(System.currentTimeMillis()));
        report.setException(t);
        errorReport = report;
        return report;
    }

    /**
     * Log the specified error report.
     */
    public void logError(ErrorReport report) {
        // Log only the non-application errors.
        ApplicationError appError = getApplicationErrorCause(report.getException());
        if (appError == null) {
            // Print the report in the log.
            if (logErrorReportEnabled) {
                log.error("UNEXPECTED ERROR.\n" + report.printContext(0));
            }
        }
    }

    public ErrorReport getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(ErrorReport errorReport) {
        this.errorReport = errorReport;
    }
}
