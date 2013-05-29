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

import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.formModeler.service.bb.mvc.components.handling.UIBeanHandler;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FactoryUniqueIdEncoder {

    public static FactoryUniqueIdEncoder lookup() {
        return (FactoryUniqueIdEncoder) CDIBeanLocator.getBeanByType(FactoryUniqueIdEncoder.class);
    }

    /**
     * Encode a name for a given panel context, appending it to a String depending on the panel.
     *
     * @param uiBean The UI bean
     * @param name  The symbolic name to encode so that different panels bean instances have different names.
     * @return A encoded name
     */
    public String encode(UIBeanHandler uiBean, String name) {
        StringBuffer sb = new StringBuffer();
        if (uiBean != null) {
            sb.append("uibean").append(Math.abs(uiBean.getBeanName().hashCode())).append("_");
        }
        sb.append(StringEscapeUtils.escapeHtml(name));
        return sb.toString();
    }
}
