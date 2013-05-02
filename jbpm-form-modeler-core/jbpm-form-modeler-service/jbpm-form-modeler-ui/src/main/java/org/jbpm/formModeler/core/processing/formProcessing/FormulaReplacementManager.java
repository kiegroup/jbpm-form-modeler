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
package org.jbpm.formModeler.core.processing.formProcessing;

import org.jbpm.formModeler.core.processing.formProcessing.replacers.FormulaReplacer;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.jbpm.formModeler.core.processing.formProcessing.replacers.FormulaReplacementContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Iterator;

@ApplicationScoped
public class FormulaReplacementManager {

    public static FormulaReplacementManager lookup() {
        return (FormulaReplacementManager) CDIBeanLocator.getBeanByType(FormulaReplacementManager.class);
    }

    @Inject
    private Instance<FormulaReplacer> formulaReplacements;

    public String replace(FormulaReplacementContext ctx) {
        Iterator<FormulaReplacer> it = formulaReplacements.iterator();
        while (it.hasNext()) {
            FormulaReplacer fr = it.next();
            String formula = fr.replace(ctx);
            ctx.setFormula(formula);
        }
        return ctx.getFormula();
    }
}
