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
package org.jbpm.formModeler.core.processing.formDecorators;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.AbstractFieldHandler;

import java.util.Map;

public abstract class FormDecorator extends AbstractFieldHandler {

    public String getName() {
        return this.getClass().getName();
    }

    public String[] getCompatibleClassNames() {
        return new String[0];
    }

    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        return null;
    }

    public Map getParamValue(String inputName, Object objectValue, String pattern) {
        return null;
    }

    public boolean isEmpty(Object value) {
        return false;
    }
}
