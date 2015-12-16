/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.formModeler.core.config;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;
import org.jbpm.formModeler.service.LocaleChangedEvent;
import org.jbpm.formModeler.service.LocaleManager;

@SessionScoped
public class FieldTypeLabelBuilder implements Serializable {

    @Inject
    private LocaleManager localeManager;
    private ResourceBundle bundle;

    public String getFieldTypeLabel(FieldType fieldType) {
        if (fieldType instanceof PlugableFieldType) {
            return ((PlugableFieldType)fieldType).getDescription(localeManager.getCurrentLocale());
        }

        return bundle.getString("fieldType." + fieldType.getCode());
    }

    protected void onChangeLocale(@Observes LocaleChangedEvent localeChangedEvent) {
        bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.config.fieldTypes.messages", localeManager.getCurrentLocale());
    }
}
