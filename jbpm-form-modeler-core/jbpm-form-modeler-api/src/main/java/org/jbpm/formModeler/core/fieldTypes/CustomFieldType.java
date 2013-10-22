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
package org.jbpm.formModeler.core.fieldTypes;

import java.util.Locale;
import java.util.Map;

/**
 * Definition interface for custom fields
 */
public interface CustomFieldType {
    /**
     * This method returns a text definition for the custom type. This text will be shown on the UI to identify the CustomFieldType
     * @param locale                The current user locale
     * @return                      A String that describes the field type on the specified locale.
     */
    public String getDescription(Locale locale);

    /**
     * This method returns a string that contains the HTML code that will be used to show the field value.
     * shown on screen
     * @param value                 The current field value
     * @param fieldName             The field name
     * @param namespace             The unique id for the rendered form, it should be used to generate identifiers inside the html code.
     * @param required              Determines if the field is required or not
     * @param readonly              Determines if the field must be shown on read only mode
     * @param params                A list of configuration params that can be set on the field configuration screen
     * @return                      The HTML that will be used to show the field value
     */
    public String getShowHTML(Object value, String fieldName, String namespace, boolean required, boolean readonly, String... params);

    /**
     * This method returns a String that contains the HTML code that will show the input view of the field. That will be used to set the field value.
     * @param value                 The current field value
     * @param fieldName             The field name
     * @param namespace             The unique id for the rendered form, it should be used to generate identifiers inside the html code.
     * @param required              Determines if the field is required or not
     * @param readonly              Determines if the field must be shown on read only mode
     * @param params                A list of configuration params that can be set on the field configuration screen
     * @return                      The HTML code that will be used to show the input view of the field.
     */
    public String getInputHTML(Object value, String fieldName, String namespace, boolean required, boolean readonly, String... params);

    /**
     * This method is used to obtain the field value from the submitted form values.
     * @param requestParameters     A Map containing the request parameters for the submitted form
     * @param requestFiles          A Map containing the java.io.Files uploaded on the request
     * @param fieldName             The field name
     * @param namespace             The unique id for the rendered form, it should be used to generate identifiers inside the html code.
     * @param previousValue         The previous value of the current field
     * @param required              Determines if the field is required or not
     * @param readonly              Determines if the field must be shown on read only mode
     * @param params                A list of configuration params that can be set on the field configuration screen
     * @return                      The value of the field based on the submitted form values.
     */
    public Object getValue(Map requestParameters, Map requestFiles, String fieldName, String namespace, Object previousValue, boolean required, boolean readonly, String... params);
}
