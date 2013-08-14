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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class SetFieldValueInstruction extends FormChangeInstruction {
    private static transient Logger log = LoggerFactory.getLogger(SetFieldValueInstruction.class.getName());
    private String XMLrepresentation;

    public SetFieldValueInstruction(Map paramsMap) {
        StringBuffer sb = new StringBuffer();
        for (Iterator it = paramsMap.keySet().iterator(); it.hasNext();) {
            String paramName = (String) it.next();
            Object paramValue = paramsMap.get(paramName);
            if (paramValue instanceof String[]) {
                sb.append("<setvalue name=\"");
                sb.append(StringEscapeUtils.escapeXml(paramName));
                sb.append("\" value=\"");
                if (((String[]) paramValue).length > 0){
                    String s = StringEscapeUtils.escapeXml(((String[]) paramValue)[0]);
                    sb.append(StringUtils.replace(s, "\n", "&#10;"));
                }
                sb.append("\"/>");
            } else {
                log.error("Unsupported param class " + paramValue.getClass().getName());
            }
        }
        XMLrepresentation = sb.toString();
    }

    public String getXML() {
        return XMLrepresentation;
    }

    public String toString() {
        return getXML();
    }
}
