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

package org.jbpm.formModeler.fieldTypes.document;

import org.jbpm.document.Document;
import org.jbpm.formModeler.core.fieldTypes.PlugableFieldType;
import org.jbpm.formModeler.fieldTypes.document.handling.JBPMDocumentFieldTypeHandler;

import java.util.Locale;
import java.util.ResourceBundle;

public class JBPMDocumentFieldType extends PlugableFieldType {
    public static final String CODE = "Document";

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public String getManagerClass() {
        return JBPMDocumentFieldTypeHandler.class.getName();
    }

    @Override
    public String getFieldClass() {
        return Document.class.getName();
    }

    @Override
    public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.fieldTypes.document.messages", locale);
        return bundle.getString("description");
    }
}
