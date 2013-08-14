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

import org.jbpm.formModeler.api.model.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormNamespaceData {
    private static transient Logger log = LoggerFactory.getLogger(FormNamespaceData.class);

    private Form form;
    private String namespace;
    private String fieldNameInParent;

    public FormNamespaceData(Form form, String namespace, String fieldNameInParent) {
        this.fieldNameInParent = fieldNameInParent;
        this.form = form;
        this.namespace = namespace;
    }

    public Form getForm() {
        return form;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFieldNameInParent() {
        return fieldNameInParent;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FormNamespaceData that = (FormNamespaceData) o;

        if (fieldNameInParent != null ? !fieldNameInParent.equals(that.fieldNameInParent) : that.fieldNameInParent != null)
            return false;
        if (!form.equals(that.form)) return false;
        if (!namespace.equals(that.namespace)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = form.hashCode();
        result = 29 * result + namespace.hashCode();
        result = 29 * result + (fieldNameInParent != null ? fieldNameInParent.hashCode() : 0);
        return result;
    }
}
