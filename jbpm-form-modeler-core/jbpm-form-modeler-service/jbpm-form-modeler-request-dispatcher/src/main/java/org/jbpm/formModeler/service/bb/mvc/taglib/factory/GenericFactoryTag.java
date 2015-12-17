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
package org.jbpm.formModeler.service.bb.mvc.taglib.factory;

import org.jbpm.formModeler.service.bb.mvc.components.handling.UIBeanHandler;

public class GenericFactoryTag extends javax.servlet.jsp.tagext.BodyTagSupport {

    private String bean;
    private String action;
    private String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getBean() {
        if (bean == null) {
            UIBeanHandler currentComponent = (UIBeanHandler) pageContext.getRequest().getAttribute(UseComponentTag.COMPONENT_ATTR_NAME);
            if (currentComponent != null) {
                return currentComponent.getBeanName();
            }
        }
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
