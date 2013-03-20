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
package org.jbpm.formModeler.core;

import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.api.processing.FieldHandler;

/**
 *
 */
public abstract class FieldHandlersManager extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FieldHandlersManager.class.getName());

    private FieldHandler[] staticHandlers;
    private FieldHandler[] decorators;
    private LocaleManager localeManager;

    private String[] entitySelectorsFieldHandlers;
    private String[] remoteSelectorsFieldHandlers;

    public abstract FieldHandler[] getHandlers();

    public void start() throws Exception {
        super.start();
    }

    protected String[] getLanguages() {
        return localeManager.getPlatformAvailableLangs();
    }

    public FieldHandler[] getDecorators() {
        return decorators;
    }

    public void setDecorators(FieldHandler[] decorators) {
        this.decorators = decorators;
    }

    public FieldHandler[] getStaticHandlers() {
        return staticHandlers;
    }

    public void setStaticHandlers(FieldHandler[] staticHandlers) {
        this.staticHandlers = staticHandlers;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public String[] getEntitySelectorsFieldHandlers() {
        return entitySelectorsFieldHandlers;
    }

    public void setEntitySelectorsFieldHandlers(String[] entitySelectorsFieldHandlers) {
        this.entitySelectorsFieldHandlers = entitySelectorsFieldHandlers;
    }

    public String[] getRemoteSelectorsFieldHandlers() {
        return remoteSelectorsFieldHandlers;
    }

    public void setRemoteSelectorsFieldHandlers(String[] remoteSelectorsFieldHandlers) {
        this.remoteSelectorsFieldHandlers = remoteSelectorsFieldHandlers;
    }
}
