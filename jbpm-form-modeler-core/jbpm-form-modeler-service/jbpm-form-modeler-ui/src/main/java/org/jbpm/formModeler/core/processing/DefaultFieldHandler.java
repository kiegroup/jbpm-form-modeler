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

import org.jbpm.formModeler.core.processing.formProcessing.FormulaReplacementManager;
import org.jbpm.formModeler.core.processing.formProcessing.FunctionsProvider;
import org.jbpm.formModeler.core.processing.formProcessing.FunctionsProvider;

public abstract class DefaultFieldHandler extends AbstractFieldHandler {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DefaultFieldHandler.class.getName());

    private FunctionsProvider functionsProvider;
    private FormulaReplacementManager replacementManager;

    public FormulaReplacementManager getReplacementManager() {
        return replacementManager;
    }

    public void setReplacementManager(FormulaReplacementManager replacementManager) {
        this.replacementManager = replacementManager;
    }

    public FunctionsProvider getFunctionsProvider() {
        return functionsProvider;
    }

    public void setFunctionsProvider(FunctionsProvider functionsProvider) {
        this.functionsProvider = functionsProvider;
    }

    public String getName() {
        return getComponentName();
    }

}
