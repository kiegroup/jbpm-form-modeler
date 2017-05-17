/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.formModeler.core.processing.fieldHandlers.date;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formModeler.core.processing.fieldHandlers.DefaultFieldHandlerFormatterTest;
import org.jbpm.formModeler.service.LocaleManager;

public abstract class AbstractDateFieldHandlerFormatterTest<HANDLER extends DateFieldHandler> extends DefaultFieldHandlerFormatterTest<DateFieldHandlerFormatter> {

    protected FormatterFragmentMatcher tagMatcher;

    protected HANDLER fieldHandler;
    protected LocaleManager localeManager;

    @Override
    public void setup() {
        super.setup();

        List<String> renderingFragments = new ArrayList<String>();
        renderingFragments.add("output");

        tagMatcher = new FormatterFragmentMatcher(renderingFragments);
        tagMatcher.addParam("uid",
                            currentField.getFieldName());
    }

    @Override
    protected void initDependencies() {
        fieldHandler = getHandler();
        localeManager = weld.instance().select(LocaleManager.class).get();
    }

    @Override
    protected DateFieldHandlerFormatter getFormatterInstance() {
        DateFieldHandlerFormatter formatter = new DateFieldHandlerFormatter();
        return formatter;
    }

    protected abstract HANDLER getHandler();
}
