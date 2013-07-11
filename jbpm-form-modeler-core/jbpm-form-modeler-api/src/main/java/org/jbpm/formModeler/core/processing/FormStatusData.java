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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A FormStatusData is used to check the status of a form and it's fields values, errors...
 */
public interface FormStatusData {

    public static final String EXPANDED_FIELDS = "-jbpm-expandedFields";
    public static final String PREVIEW_FIELD_POSITIONS = "-jbpm-previewFieldPositions";
    public static final String EDIT_FIELD_POSITIONS = "-jbpm-editFieldPositions";
    public static final String EDIT_FIELD_PREVIOUS_VALUES = "-jbpm-editFieldPreviousValue";
    public static final String DO_THE_ITEM_ADD = "-jbpm-doTheItemAdd";
    public static final String CALCULATED_RANGE_FORMULAS="-jbpm-rangeFormulas";
    public static final String CALCULATED_RANGE_FORMULAS_LANG ="-jbpm-rangeFormulasLang";
    public static final String REMOVED_ELEMENTS = "-jbpm-removed-array-elements";

    public List getWrongFields();

    public boolean isValid();

    public boolean isEmpty();

    public Object getCurrentValue(String fieldName);

    public String getCurrentInputValue(String inputName);

    public void clear();

    public boolean isNew();

    public Map getCurrentValues();

    public Map getCurrentInputValues();

    public Map getAttributes();

    public boolean hasErrorMessage(String fieldName);

    public List getErrorMessages(String fieldName);

    public Map getWrongFieldsMessages();

    public Object getLoadedObject(String id);
}
