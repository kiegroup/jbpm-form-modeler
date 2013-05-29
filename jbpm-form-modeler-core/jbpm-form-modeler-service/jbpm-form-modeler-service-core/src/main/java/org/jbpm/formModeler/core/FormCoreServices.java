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

import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.processing.BindingManager;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FormCoreServices {

    public static FormCoreServices lookup() {
        return (FormCoreServices) CDIBeanLocator.getBeanByType(FormCoreServices.class);
    }

    @Inject
    protected FormManager formManager;

    @Inject
    protected FieldTypeManager fieldTypeManager;

    @Inject
    protected BindingManager bindingManager;

    @Inject
    protected FormSerializationManager formSerializationManager;

    public FormManager getFormManager() {
        return formManager;
    }

    public FieldTypeManager getFieldTypeManager() {
        return fieldTypeManager;
    }

    public BindingManager getBindingManager() {
        return bindingManager;
    }

    public FormSerializationManager getFormSerializationManager() {
        return formSerializationManager;
    }
}
