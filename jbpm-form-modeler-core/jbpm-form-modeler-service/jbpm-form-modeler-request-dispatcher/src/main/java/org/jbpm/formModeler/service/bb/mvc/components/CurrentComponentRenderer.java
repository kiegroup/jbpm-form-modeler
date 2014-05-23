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

import org.jbpm.formModeler.service.bb.mvc.components.handling.UIBeanHandler;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.lang.String;

@SessionScoped
@Named("ccrenderer")
public class CurrentComponentRenderer extends UIBeanHandler implements Serializable {

    public static CurrentComponentRenderer lookup() {
        return (CurrentComponentRenderer) CDIBeanLocator.getBeanByName("ccrenderer");
    }

    private BaseUIComponent currentComponent;

    public String getBeanJSP() {
        if (currentComponent != null) return currentComponent.getBaseComponentJSP();
        return "";
    }

    public BaseUIComponent getCurrentComponent() {
        return currentComponent;
    }

    public void setCurrentComponent(BaseUIComponent currentComponent) {
        this.currentComponent = currentComponent;
    }
}
