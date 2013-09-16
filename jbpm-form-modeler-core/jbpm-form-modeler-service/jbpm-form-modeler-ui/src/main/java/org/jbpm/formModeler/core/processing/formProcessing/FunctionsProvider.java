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

import bsh.EvalError;
import bsh.Interpreter;
import org.slf4j.Logger;
import org.jbpm.formModeler.service.cdi.CDIBeanLocator;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;

/**
 * Properties class used to provide Java objects as a functions that can be used on field Formulas.
 */
public class FunctionsProvider extends Properties {

    public static FunctionsProvider lookup() {
        return (FunctionsProvider) CDIBeanLocator.getBeanByType(FunctionsProvider.class);
    }

    private Logger log = LoggerFactory.getLogger(FunctionsProvider.class);


    public void populate(Interpreter interpreter) throws EvalError {
        interpreter.set("Functions", new Functions());
    }
}
