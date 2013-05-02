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

import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.ApplicationScoped;

/**
 * Replaces the {$this} token on formula.
 */
@ApplicationScoped
public class ThisReplacer implements FormulaReplacer {

    public static final String THIS_TOKEN = "{$this}";

    public String replace(FormulaReplacementContext ctx) {
        if (ctx.isBeforeFieldEvaluation()) {
            if (isFormulaSurroundedByQuotes(ctx.getFormula()))
                return StringUtils.replace(ctx.getFormula(), THIS_TOKEN, ctx.getParamValue());
            return StringUtils.replace(ctx.getFormula(), THIS_TOKEN, "\"" + ctx.getParamValue() + "\"");
        } else {
            return StringUtils.replace(ctx.getFormula(), THIS_TOKEN, "{" + ctx.getField().getFieldName() + "}");
        }
    }

    protected boolean isFormulaSurroundedByQuotes(String formula) {
        if (StringUtils.isEmpty(formula) || !formula.contains(THIS_TOKEN)) return false;
        return hasOpenQuote(formula) && hasCloseQuote(formula);
    }

    protected boolean hasOpenQuote(String formula) {
        int count = countQuotes(formula.substring(0, formula.indexOf(THIS_TOKEN)));
        return count % 2 != 0;
    }

    protected boolean hasCloseQuote(String formula) {
        int count = countQuotes(formula.substring(formula.indexOf(THIS_TOKEN) + 1));
        return count % 2 != 0;
    }

    private int countQuotes(String formula) {
        int count = 0;
        for (int i = 0; i < formula.length(); i++) {
            if (formula.charAt(i) == '\"') count++;
        }
        return count;
    }
}
