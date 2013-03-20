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
package org.jbpm.formModeler.core.valueConverters;

import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.jbpm.formModeler.api.processing.PropertyDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ValueConverterManager extends BasicFactoryElement {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ValueConverterManager.class.getName());

    private ValueConverter[] staticConverters;
    private LocaleManager localeManager;

    public ValueConverter[] getStaticConverters() {
        return staticConverters;
    }

    public void setStaticConverters(ValueConverter[] converters) {
        this.staticConverters = converters;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    protected List orderEntitiesByName(List entities) {
        return new ArrayList();
    }

    public List getConverters() {
        ArrayList list = new ArrayList();
        // static converters
        list.addAll(Arrays.asList(staticConverters));
        return list;
    }

    public ValueConverter getConverter(PropertyDefinition IPropertyDefinition) {
        String typeDescription = IPropertyDefinition.getId();
        return getConverter(typeDescription);
    }

    public ValueConverter getConverter(String className) {

        //Try standard converters
        for (int i = 0; i < staticConverters.length; i++) {
            ValueConverter staticConverter = staticConverters[i];
            if (staticConverter.accepts(className)) {
                return staticConverter;
            }
        }

        log.error("No converter for type " + className);
        return null;
    }
}
