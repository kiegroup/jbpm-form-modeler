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
package org.jbpm.formModeler.core.processing.formProcessing.replacers;

import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.apache.commons.lang.StringUtils;

public class LangReplacer implements FormulaReplacer {

    private LocaleManager localeManager;

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public String replace(FormulaReplacementContext ctx) {
        return StringUtils.replace(ctx.getFormula(), "{$lang}", localeManager.getCurrentLang());
    }
}
