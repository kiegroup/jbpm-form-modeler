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

import org.jbpm.formModeler.service.LocaleManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * An error report.
 */
public class ErrorReport {

    /** The error date */
    protected Date date;

    /** The error unique identifier */
    protected String id;

    /** The error itself */
    protected Throwable exception;

    protected static transient DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public ErrorReport() {
        date = new Date();
        id = null;
        exception = null;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String printContext(int indent) {
        StringBuffer buf = new StringBuffer();

        appendIndent(buf, indent);

        appendIndent(buf, indent);
        buf.append("Error id='").append(id).append("'\n");
        appendIndent(buf, indent);
        buf.append("Error date='").append(dateFormat.format(date)).append("'\n");
        appendIndent(buf, indent);
        buf.append("\r\n");
        appendIndent(buf, indent);
        buf.append(printExceptionTrace());

        return buf.toString();
    }

    public String printExceptionTrace() {
        StringWriter sw = new StringWriter();
        ErrorManager.lookup().getRootCause(exception).printStackTrace(new PrintWriter(sw));
        return sw.getBuffer().toString();
    }

    public static void appendIndent(StringBuffer out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.append("\t");
        }
    }

    public boolean isBusinessAppError() {
        ApplicationError appError = ErrorManager.lookup().getApplicationErrorCause(exception);
        return appError != null && appError instanceof BusinessError;
    }

    public boolean isTechnicalAppError() {
        ApplicationError appError = ErrorManager.lookup().getApplicationErrorCause(exception);
        return appError != null && appError instanceof BusinessError;
    }

    protected String getUnexpectedErrorTitle() {
        ResourceBundle i18n = ResourceBundle.getBundle("org.jbpm.formModeler.service.error.messages", LocaleManager.currentLocale());
        return i18n.getString("errorTitle");
    }

    protected String getUnexpectedErrorMessage() {
        ResourceBundle i18n = ResourceBundle.getBundle("org.jbpm.formModeler.service.error.messages", LocaleManager.currentLocale());
        return i18n.getString("errorMessage");
    }

    public String printErrorTitle() {
        ApplicationError appError = ErrorManager.lookup().getApplicationErrorCause(exception);
        if (isBusinessAppError()) return ((BusinessError) appError).getTitle();
        return getUnexpectedErrorTitle();
    }

    public String printErrorMessage() {
        ApplicationError appError = ErrorManager.lookup().getApplicationErrorCause(exception);
        if (appError != null) return appError.getMessage();
        return getUnexpectedErrorMessage();
    }

    public static void main(String[] args) {
        ErrorReport report = new ErrorReport();
        report.setException(new RuntimeException("ERROR"));
        report.setId(String.valueOf(System.currentTimeMillis()));
        System.out.println(report.toString());
    }
}

