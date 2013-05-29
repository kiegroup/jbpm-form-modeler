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
package org.jbpm.formModeler.core.processing.formRendering;

import javax.enterprise.context.ApplicationScoped;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

@ApplicationScoped
public class FormTemplateHelperImpl implements FormTemplateHelper {

    public static final MessageFormat fieldMsgFormat = new MessageFormat(FIELD_FORMAT);
    public static final MessageFormat labelMsgFormat = new MessageFormat(LABEL_FORMAT);

    private Hashtable templatesCache = new Hashtable();

    @Override
    public synchronized List getRenderingInstructions(String template) {
        if (template == null) {
            return Collections.EMPTY_LIST;
        }
        List l = (List) templatesCache.get(template);
        if (l == null) {
            l = calculateInstructions(template);
            if (templatesCache.size() > 50)
                templatesCache.remove(templatesCache.keySet().iterator().next());
            templatesCache.put(template, l);
        }
        return l;
    }

    protected synchronized List calculateInstructions(String template) {
        if (template == null || "".equals(template.trim()))
            return Collections.EMPTY_LIST;
        List result = new ArrayList();

        Object[] parsedResult = splitByElement(template, fieldMsgFormat);
        if (parsedResult == null) {
            Object[] parsedResult2 = splitByElement(template, labelMsgFormat);
            if (parsedResult2 == null) {
                result.add(new HTMLPieceRenderingInstruction(template));
            } else {
                result.add(new HTMLPieceRenderingInstruction((String) parsedResult2[0]));
                result.add(new RenderLabelInstruction((String) parsedResult2[1]));
                result.addAll(calculateInstructions(((String) parsedResult2[2])));
            }
        } else {
            result.addAll((calculateInstructions((String) parsedResult[0])));
            result.add(new RenderFieldInstruction((String) parsedResult[1]));
            result.addAll((calculateInstructions((String) parsedResult[2])));
        }
        return result;
    }


    protected synchronized Object[] splitByElement(String template, MessageFormat s) {
        try {
            return s.parse(template);
        } catch (ParseException e) {
        }
        return null;
    }

}
