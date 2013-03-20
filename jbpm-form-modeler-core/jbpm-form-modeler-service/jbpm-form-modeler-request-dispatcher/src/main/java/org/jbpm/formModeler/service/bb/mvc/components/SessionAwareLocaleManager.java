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

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.Factory;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestContext;

import java.util.Locale;

public class SessionAwareLocaleManager extends org.jbpm.formModeler.service.bb.commons.config.LocaleManager {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SessionAwareLocaleManager.class.getName());

    private String sessionHelper;

    public String getSessionHelper() {
        return sessionHelper;
    }

    public void setSessionHelper(String sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    protected SessionHelper getSessionHelperObject() {
        return (RequestContext.getCurrentContext() != null) ? (SessionHelper) Factory.lookup(sessionHelper) : null;
    }


    /**
     * Current locale for viewing contents
     *
     * @return Current locale for viewing contents
     */
    public Locale getCurrentLocale() {
        SessionHelper helper = getSessionHelperObject();
        Locale locale = helper != null ? helper.getCurrentLocale() : null;
        return (locale != null) ? locale : getDefaultLocale();
    }

    public void setCurrentLocale(Locale currentLocale) {
        getSessionHelperObject().setCurrentLocale(currentLocale);
    }


    /**
     * Current locale for editing contents
     *
     * @return Current locale for editing contents
     */
    public Locale getCurrentEditLocale() {
        Locale locale = getSessionHelperObject().getCurrentEditLocale();
        return (locale != null) ? locale : getDefaultLocale();
    }

    public void setCurrentEditLocale(Locale currentEditLocale) {
        getSessionHelperObject().setCurrentEditLocale(currentEditLocale);
    }
}
