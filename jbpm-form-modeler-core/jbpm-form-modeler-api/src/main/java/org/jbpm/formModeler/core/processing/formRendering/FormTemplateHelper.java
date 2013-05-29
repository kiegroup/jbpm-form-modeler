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

import org.jbpm.formModeler.api.model.Form;

import java.util.List;

/**
 * Helps in rendering template based forms
 */
public interface FormTemplateHelper {
    String FIELD_FORMAT = "{0}" + Form.TEMPLATE_FIELD + "'{'{1}'}'{2}";
    String LABEL_FORMAT = "{0}" + Form.TEMPLATE_LABEL + "'{'{1}'}'{2}";

    List getRenderingInstructions(String template);
}
