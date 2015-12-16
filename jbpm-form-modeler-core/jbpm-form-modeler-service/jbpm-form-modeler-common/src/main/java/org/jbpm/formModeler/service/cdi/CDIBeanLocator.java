/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.formModeler.service.cdi;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CDIBeanLocator {

    public static Object getBeanByName(String name) {
        return BeanProvider.getContextualReference(name, false);
    }

    public static Object getBeanByType(Class type) {
        return BeanProvider.getContextualReference(type, true);
    }

    public static Object getBeanByType(String type) {
        try {
            Class beanClass = Class.forName(type);
            return getBeanByType(beanClass);
        } catch (Throwable e) {
            // Just ignore
            return null;
        }
    }

    public static Object getBeanByNameOrType(String beanName) {
        try {
            Object beanObject = getBeanByName(beanName);
            if (beanObject != null) return beanObject;
            return null;
        } catch (Exception e) {
            return getBeanByType(beanName);
        }
    }
}
