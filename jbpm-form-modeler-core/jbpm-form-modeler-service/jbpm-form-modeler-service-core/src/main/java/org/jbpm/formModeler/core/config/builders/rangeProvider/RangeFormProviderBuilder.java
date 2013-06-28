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
package org.jbpm.formModeler.core.config.builders.rangeProvider;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.RangeProvider;
import org.jbpm.formModeler.core.config.builders.RangeProviderBuilder;
import org.jbpm.formModeler.core.model.RangeProviderForm;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class RangeFormProviderBuilder implements RangeProviderBuilder {
    @Override
    public String getId() {
        return Form.RANGE_PROVIDER_FORM;
    }


    @Override
    public RangeProvider buildRangeProvider(Map<String, Object> config) {
        //return new PojoDataHolder((String)config.get("id"), (String)config.get("value"), (String)config.get("color"));
        return new RangeProviderForm();
    }

}
