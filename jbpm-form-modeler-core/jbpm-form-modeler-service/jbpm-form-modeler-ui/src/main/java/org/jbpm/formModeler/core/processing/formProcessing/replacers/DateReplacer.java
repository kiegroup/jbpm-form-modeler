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

import javax.enterprise.context.ApplicationScoped;
import java.text.SimpleDateFormat;

/**
 * Replaces the {$date(format)} token on formula.
 */
@ApplicationScoped
public class DateReplacer implements FormulaReplacer {

    public String replace(FormulaReplacementContext ctx) {
        String formula = ctx.getFormula();
        while (formula.indexOf("{$date(") != -1) {
            int index0 = formula.indexOf("{$date(");
            String pattern = "";
            int index1;
            for (index1 = index0 + 7; index1 < formula.length(); index1++) {
                if (formula.charAt(index1) == ')') break;
                else
                    pattern += formula.charAt(index1);
            }
            index1 = index1 + 2;

            String result = "{$WRONG_PATTERN}";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                result = sdf.format(ctx.getDate());
            } catch (Exception e) {
            }
            formula = formula.substring(0, index0) + result + formula.substring(index1);
        }

        return formula;
    }

}
