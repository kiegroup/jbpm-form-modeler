/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

import org.jbpm.formModeler.api.model.Field;

/**
 * Created with IntelliJ IDEA.
 * User: pefernan
 * Date: 10/1/13
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FieldI18nResourceObtainer {
    String getFieldLabel(Field field);

    String getFieldTitle(Field field);

    String getFieldErrorMessage(Field field);
}
