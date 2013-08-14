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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FormChangeResponse {
    private static transient Logger log = LoggerFactory.getLogger(FormChangeResponse.class);

    private List instructions = new ArrayList(); 

    public List getInstructions() {
        return instructions;
    }

    public void addInstruction(FormChangeInstruction instruction) {
        if (log.isDebugEnabled())
            log.debug("Adding instruction " + instruction);
        instructions.add(instruction);
    }

    public String getXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" \n" +
                "  standalone=\"yes\"?>\n");
        sb.append("<response>\n");
        for (int i = 0; i < instructions.size(); i++) {
            FormChangeInstruction instruction = (FormChangeInstruction) instructions.get(i);
            sb.append(instruction.getXML());
            sb.append("\n");
        }
        sb.append("</response>\n");
        return sb.toString();
    }
}
