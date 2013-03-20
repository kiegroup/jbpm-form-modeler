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
package org.jbpm.formModeler.core.processing;

import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.processing.FieldHandler;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractFieldHandler extends BasicFactoryElement implements FieldHandler {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(AbstractFieldHandler.class.getName());

    public String getHumanName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.fieldHandlers", locale);
        try {
            return bundle.getString(getName());
        }
        catch (Exception e) {
            return getName();
        }
    }

    public void initialize(Field field, String namespace) {
        
    }

    @Override
    public boolean isEvaluable(String inputName, Map parametersMap, Map filesMap) {
        return true;
    }

    protected boolean checkBooleanParameter(String[] param) {
        return param != null && param.length > 0 && Boolean.valueOf(StringUtils.defaultString(param[0])).booleanValue();
    }
}
