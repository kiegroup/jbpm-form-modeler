/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.jbpm.formModeler.core.processing.fieldHandlers.mocks;

import org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils.SubFormHelper;

import javax.enterprise.inject.Specializes;

@Specializes
public class SubFormHelperMock extends SubFormHelper {
    public static final String EDIT_FIELD_SUFFIX = ".edit.";
    public static final String PREVIEW_FIELD_SUFFIX = ".preview.";

    @Override
    public Integer getEditFieldPosition( String inputName ) {
        if ( inputName.indexOf(EDIT_FIELD_SUFFIX) != -1 ) return 0;
        return null;
    }

    @Override
    public Integer getPreviewFieldPosition(String inputName) {
        if ( inputName.indexOf(PREVIEW_FIELD_SUFFIX) != -1 ) return 0;
        return null;
    }
}
