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
package org.jbpm.formModeler.core.model;

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.RangeProvider;
import org.jbpm.formModeler.core.FormCoreServices;
import org.jbpm.formModeler.core.config.FormManager;

import java.util.Map;
import java.util.TreeMap;

public class RangeProviderForm implements RangeProvider {

    private FormManager getFormManager(){
        return FormCoreServices.lookup().getFormManager();
    }

    @Override
    public String getId() {
        return Form.RANGE_PROVIDER_FORM;
    }

    @Override
    public Map getValuesMap(String code) {

        Form[] allForms = getFormManager().getAllForms();
        TreeMap treeMap = new TreeMap<String,String> ();

        for(Form form: allForms){
            treeMap.put(form.getName(),form.getName());
        }

        return treeMap;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
