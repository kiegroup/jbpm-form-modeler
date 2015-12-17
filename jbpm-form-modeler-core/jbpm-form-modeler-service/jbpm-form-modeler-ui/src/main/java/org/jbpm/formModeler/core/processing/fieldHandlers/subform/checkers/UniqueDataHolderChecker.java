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
package org.jbpm.formModeler.core.processing.fieldHandlers.subform.checkers;

import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Form;

import java.util.Set;

public class UniqueDataHolderChecker extends SubformChecker {

    @Override
    public FormCheckResult checkForm(Form form) {
        Set<DataHolder> holders = form.getHolders();
        FormCheckResult result = new FormCheckResult();
        if (holders == null || holders.size() != 1) {
            result.setValid(false);
            result.setMessageKey("invalidDatasources");
        } else result.setValid(true);
        return result;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
