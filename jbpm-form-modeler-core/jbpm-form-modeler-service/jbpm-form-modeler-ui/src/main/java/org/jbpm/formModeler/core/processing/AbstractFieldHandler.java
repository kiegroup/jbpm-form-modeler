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
package org.jbpm.formModeler.core.processing;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AbstractFieldHandler implements FieldHandler {

    protected String getFieldName() {
        String name = getClass().getSimpleName();
        return name.substring(0, name.indexOf("FieldHandler"));
    }

    public String getPageToIncludeForRendering() {
        return "/formModeler/fieldHandlers/" + getFieldName() + "/input.jsp";
    }

    public String getPageToIncludeForDisplaying() {
        return "/formModeler/fieldHandlers/" + getFieldName() + "/show.jsp";
    }
}
