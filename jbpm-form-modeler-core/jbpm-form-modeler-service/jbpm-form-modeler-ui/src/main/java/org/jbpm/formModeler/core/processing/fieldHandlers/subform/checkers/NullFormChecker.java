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
package org.jbpm.formModeler.core.processing.fieldHandlers.subform.checkers;

import org.jbpm.formModeler.api.model.Form;

public class NullFormChecker extends  SubformChecker {

    @Override
    public FormCheckResult checkForm(Form form) {
        FormCheckResult result = new FormCheckResult();
        if (form == null) {
            result.setValid(false);
            result.setMessageKey("noDefinedForm");
        } else {
            result.setValid(true);
        }
        return result;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
