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

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SetListValuesInstruction extends FormChangeInstruction {
    private static transient Logger log = LoggerFactory.getLogger(SetListValuesInstruction.class.getName());

    private String XMLrepresentation;
    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public SetListValuesInstruction(String fieldName, List newValues) {
        this.fieldName = fieldName;
        updateListValues(newValues);
    }

    public void updateListValues(List newValues) {
        StringBuffer sb = new StringBuffer();
        sb.append("<setListValues name=\"").append(StringEscapeUtils.escapeXml(fieldName)).append("\">");
        for (int i = 0; i < newValues.size(); i++) {
            Object[] objects = (Object[]) newValues.get(i);
            String id = String.valueOf(objects[0]);
            String value = String.valueOf(objects[1]);
            String selected = String.valueOf(objects[2]);
            sb.append("<option value=\"").append(StringEscapeUtils.escapeXml(id)).append("\" text=\"").append(StringEscapeUtils.escapeXml(value)).append("\"");
            if (objects[2] != null) {
                sb.append(" selected=\"").append(selected).append("\"");
            }
            sb.append("></option>");
        }
        sb.append("</setListValues>");
        XMLrepresentation = sb.toString();
    }


    public String getXML() {
        return XMLrepresentation;
    }
}
