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
package org.jbpm.formModeler.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class DefaultDataHolder implements DataHolder {
    private String renderColor;

    @Override
    public Map load(Map<String, Object> bindingData) throws Exception {
        Map values = new HashMap();
        Object value = bindingData.get(getId());
        if (value != null) {
            Set<DataFieldHolder> fieldHodlers = getFieldHolders();
            for (DataFieldHolder fieldHolder : fieldHodlers) {
                values.put(fieldHolder.getId(), readValue(value, fieldHolder.getId()));
            }
        } else {
            bindingData.put(getId(), createInstance());
        }
        return values;
    }

    @Override
    public String getRenderColor() {
        return renderColor;
    }

    @Override
    public void setRenderColor(String renderColor) {
        this.renderColor = renderColor;
    }
}
