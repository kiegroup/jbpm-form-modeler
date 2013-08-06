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
package org.jbpm.formModeler.core.config.builders.dataHolder;

import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.model.PojoDataHolder;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class PojoDataHolderBuilder implements DataHolderBuilder {
    @Override
    public String getId() {
        return Form.HOLDER_TYPE_CODE_POJO_CLASSNAME;
    }

    @Override
    public DataHolder buildDataHolder(Map<String, String> config) {
        return new PojoDataHolder(config.get("id"), config.get("inputId"), config.get("outId"), config.get("value"), config.get("color"));
    }

    @Override
    public Map getOptions(String path) {
        return null;
    }

    @Override
    public boolean supportsPropertyType(String type, String path) {
        try {
            Class clazz = Class.forName(type);
            return clazz != null;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 100000;
    }
}
