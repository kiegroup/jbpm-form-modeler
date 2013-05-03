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

import org.jbpm.formModeler.service.LocaleManager;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.ApplicationScoped;

/**
 * Replaces the {$lang} token on formula.
 */
@ApplicationScoped
public class LangReplacer implements FormulaReplacer {

    public String replace(FormulaReplacementContext ctx) {
        return StringUtils.replace(ctx.getFormula(), "{$lang}", LocaleManager.currentLang());
    }
}
