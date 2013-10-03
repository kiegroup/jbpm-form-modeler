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

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.fieldHandlers.*;
import org.jbpm.formModeler.core.processing.formDecorators.*;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FieldHandlersManagerImpl implements FieldHandlersManager {

    private List<FieldHandler> handlers;
    private List<FieldHandler> decorators;

    @PostConstruct
    protected void init() {
        handlers = new ArrayList<FieldHandler>();
        handlers.add(new InputTextFieldHandler());
        handlers.add(new NumericFieldHandler());
        handlers.add(new TextAreaFieldHandler());
        handlers.add(new DateFieldHandler());
        handlers.add(new ShortDateFieldHandler());
        handlers.add(new CheckBoxFieldHandler());
        handlers.add(new I18nSetFieldHandler());
        handlers.add(new I18nTextAreaFieldHandler());
        handlers.add(new HTMLTextAreaFieldHandler());
        handlers.add(new HTMLi18nFieldHandler());

        decorators = new ArrayList<FieldHandler>();
        decorators.add(new HTMLlabel());
        decorators.add(new Separator());

        decorators.add(new SubformFieldHandler());
        decorators.add(new CreateDynamicObjectFieldHandler());
    }

    public List<FieldHandler> getDecorators() {
        return decorators;
    }

    public List<FieldHandler> getHandlers() {
        return handlers;
    }

    public FieldHandler getHandler(FieldType fieldType) {
        if (fieldType == null) return null;

        String handlerClass = fieldType.getManagerClass();
        if (StringUtils.isBlank(handlerClass)) return null;

        return (FieldHandler) CDIBeanLocator.getBeanByNameOrType(handlerClass);
    }
}
