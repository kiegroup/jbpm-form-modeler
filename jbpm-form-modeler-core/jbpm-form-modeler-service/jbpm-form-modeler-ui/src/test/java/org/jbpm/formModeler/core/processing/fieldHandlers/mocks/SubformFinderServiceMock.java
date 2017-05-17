/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.jbpm.formModeler.core.processing.fieldHandlers.mocks;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.rendering.SubformFinderService;

@ApplicationScoped
public class SubformFinderServiceMock implements SubformFinderService {

    private Long contextFormId = null;

    private Map<Long, Form> testForms = new HashMap<>();

    public void addFormContext(Form form) {
        contextFormId = form.getId();
        addForm(form);
    }

    public void addForm(Form form) {
        testForms.put(form.getId(),
                      form);
    }

    @Override
    public Form getForm(String ctxUID) {
        if (contextFormId != null){
            return testForms.get(contextFormId);
        }
        return new Form();
    }

    @Override
    public Form getFormByPath(String formPath,
                              String ctxUID) {
        if (!StringUtils.isEmpty(formPath) && StringUtils.isNumeric(formPath)) {
            Form result = testForms.get(Long.valueOf(formPath));
            if (result != null) {
                return result;
            }
        }
        return new Form();
    }

    @Override
    public Form getFormById(long idForm,
                            String ctxUID) {
        Form result = testForms.get(idForm);
        if (result != null) {
            return result;
        }
        return new Form();
    }
}
