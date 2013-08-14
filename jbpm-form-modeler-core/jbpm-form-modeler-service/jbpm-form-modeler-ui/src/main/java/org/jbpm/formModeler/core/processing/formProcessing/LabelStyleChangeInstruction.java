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

import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.apache.commons.lang.StringEscapeUtils;
import org.jbpm.formModeler.core.config.FormManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelStyleChangeInstruction extends FormChangeInstruction {
    private static transient Logger log = LoggerFactory.getLogger(LabelStyleChangeInstruction.class);
    private String XMLrepresentation;

    public LabelStyleChangeInstruction(FormManagerImpl formManagerImpl, Form form, String namespace, Field field, String styleValue) {
        String uid = formManagerImpl.getUniqueIdentifier(form, namespace, field, field.getFieldName()) + "_label";
        StringBuffer sb = new StringBuffer();
        sb.append("<setLabelStyle name=\"");
        sb.append(StringEscapeUtils.escapeXml(uid));
        sb.append("\" value=\"");
        sb.append(StringEscapeUtils.escapeXml(styleValue));
        sb.append("\"/>");
        XMLrepresentation = sb.toString();
    }

    public String getXML() {
        return XMLrepresentation;
    }

    public String toString() {
        return getXML();
    }
}
