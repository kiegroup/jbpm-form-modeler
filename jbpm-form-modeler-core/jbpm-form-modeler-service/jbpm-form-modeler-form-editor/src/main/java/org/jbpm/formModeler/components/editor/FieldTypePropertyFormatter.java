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
package org.jbpm.formModeler.components.editor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.jbpm.formModeler.core.processing.FieldHandler;
import org.jbpm.formModeler.core.processing.FormProcessingServices;
import org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Field;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Named("FieldTypePropertyFormatter")
public class FieldTypePropertyFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(FieldTypePropertyFormatter.class);

    public void service(HttpServletRequest request, HttpServletResponse response) throws FormatterException {
        Field field = (Field) getParameter("field");
        FieldType type = (FieldType) getParameter("type");
        try {
            String propertyName = field.getFieldName();
            // TODO: fix that!
            if (type.getPropertyNames().contains(propertyName)) {

                Method getter = type.getClass().getMethod("get" + StringUtils.capitalize(propertyName));
                Object typeValue = getter.invoke(type);

                FieldHandler handler = FormProcessingServices.lookup().getFieldHandlersManager().getHandler(field.getFieldType());
                String displayPage = handler.getPageToIncludeForDisplaying();
                setAttribute(FormRenderingFormatter.ATTR_VALUE, typeValue);
                setAttribute(FormRenderingFormatter.ATTR_NAME, "_$default_" + propertyName);
                includePage(displayPage);


            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }
}
