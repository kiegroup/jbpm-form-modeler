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

import org.jbpm.formModeler.service.LocaleChangedEvent;
import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestContext;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import java.util.Locale;

@Specializes
public class SessionAwareLocaleManager extends LocaleManager {

    @Inject
    protected Event<LocaleChangedEvent> localeChangedEvent;

    protected SessionContext getSessionContext() {
        return (RequestContext.getCurrentContext() != null ? SessionContext.lookup() : null);
    }

    public Locale getCurrentLocale() {
        SessionContext ctx = getSessionContext();
        Locale locale = ctx != null ? ctx.getCurrentLocale() : null;
        return (locale != null) ? locale : getDefaultLocale();
    }

    public void setCurrentLocale(Locale currentLocale) {
        getSessionContext().setCurrentLocale(currentLocale);
        localeChangedEvent.fire(new LocaleChangedEvent());
    }
}
