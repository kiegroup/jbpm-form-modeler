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
package org.jbpm.formModeler.core.processing.formRendering.impl;


import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.wrappers.I18nSet;
import org.jbpm.formModeler.core.processing.formRendering.FieldI18nResourceObtainer;
import org.jbpm.formModeler.service.LocaleManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class FieldI18nResourceObtainerImpl implements FieldI18nResourceObtainer {
    @Inject
    private LocaleManager localeManager;

    @Override
    public String getFieldLabel(Field field) {
        return getI18nSetValue(field.getLabel(), "");
    }

    @Override
    public String getFieldTitle(Field field) {
        return getI18nSetValue(field.getTitle(), "");
    }

    @Override
    public String getFieldErrorMessage(Field field) {
        return getI18nSetValue(field.getErrorMessage(), "");
    }

    protected String getI18nSetValue(I18nSet values, String defaultValue) {
        String value = (String) localeManager.localize(values);

        if (StringUtils.isEmpty(value)) value = defaultValue;

        return StringUtils.defaultString(value);
    }
}
