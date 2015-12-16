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

package org.jbpm.formModeler.core.processing.fieldHandlers.plugable;

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.FieldHandler;

public abstract class PlugableFieldHandler implements FieldHandler {

    public abstract String getInputHTML(Object value, Field field, String inputName, String namespace, Boolean readonly);
    public abstract String getShowHTML(Object value, Field field, String inputName, String namespace);

    @Override
    public String getPageToIncludeForRendering() {
        return "/formModeler/fieldHandlers/Plugable/input.jsp";
    }

    @Override
    public String getPageToIncludeForDisplaying() {
        return "/formModeler/fieldHandlers/Plugable/show.jsp";
    }
}
