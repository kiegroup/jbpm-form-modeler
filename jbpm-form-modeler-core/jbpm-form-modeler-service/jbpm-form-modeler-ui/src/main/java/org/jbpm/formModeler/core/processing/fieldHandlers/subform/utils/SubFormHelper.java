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

package org.jbpm.formModeler.core.processing.fieldHandlers.subform.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.formModeler.core.processing.FormNamespaceData;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.processing.FormStatusData;
import org.jbpm.formModeler.core.processing.formProcessing.NamespaceManager;

@Dependent
public class SubFormHelper {
    public static final String FIELD_ROOT_VALUE = "-jbpm-field-root-value";

    @Inject
    protected NamespaceManager namespaceManager;

    @Inject
    protected FormProcessor formProcessor;

    public Object getFieldRootValue( String inputName ) {
        return getValue( inputName, FIELD_ROOT_VALUE );
    }

    public void setFieldRootValue( String inputName, Object rootValue ) {
        setValue( inputName, rootValue, FIELD_ROOT_VALUE );
    }

    public String getExpandedField( String inputName ) {
        return ( String ) getValue( inputName, FormStatusData.EXPANDED_FIELDS );
    }

    public void setExpandedField( String inputName, String expandedField ) {
        setValue( inputName, expandedField, FormStatusData.EXPANDED_FIELDS );
    }

    public void clearExpandedField( String inputName ) {
        clearValues( inputName, FormStatusData.EXPANDED_FIELDS );
    }

    public List<Integer> getRemovedFieldPositions( String inputName ) {
        return ( List<Integer> ) getValue( inputName, FormStatusData.REMOVED_ELEMENTS );
    }

    public void setRemovedFieldPositions( String inputName, List<Integer> positions ) {
        setValue( inputName, positions, FormStatusData.REMOVED_ELEMENTS );
    }

    public void clearRemovedFieldPositions( String inputName ) {
        clearValues( inputName, FormStatusData.REMOVED_ELEMENTS );
    }

    public Integer getPreviewFieldPosition( String inputName ) {
        return ( Integer ) getValue( inputName, FormStatusData.PREVIEW_FIELD_POSITIONS );
    }

    public void setPreviewFieldPosition( String inputName, Integer position ) {
        setValue( inputName, position, FormStatusData.PREVIEW_FIELD_POSITIONS );
    }

    public void clearPreviewFieldPositions( String inputName ) {
        clearValues( inputName, FormStatusData.PREVIEW_FIELD_POSITIONS );
    }

    public Integer getEditFieldPosition( String inputName ) {
        return ( Integer ) getValue( inputName, FormStatusData.EDIT_FIELD_POSITIONS );
    }

    public void setEditFieldPosition( String inputName, Integer position ) {
        setValue( inputName, position, FormStatusData.EDIT_FIELD_POSITIONS );
    }

    public void clearEditFieldPositions( String inputName ) {
        clearValues( inputName, FormStatusData.EDIT_FIELD_POSITIONS );
    }

    public Object getEditFieldPreviousValues( String inputName ) {
        return getValue( inputName, FormStatusData.EDIT_FIELD_PREVIOUS_VALUES );
    }

    public void setEditFieldPreviousValues( String inputName, Object values ) {
        setValue( inputName, values, FormStatusData.EDIT_FIELD_PREVIOUS_VALUES );
    }

    public void clearEditFieldPreviousValues( String inputName ) {
        clearValues( inputName, FormStatusData.EDIT_FIELD_PREVIOUS_VALUES );
    }

    public Object getValue( String inputName, String attribute ) {
        FormNamespaceData rootNamespaceData = getRootNamespaceData( inputName );

        if ( rootNamespaceData != null ) {
            Map values = ( Map ) formProcessor.getAttribute( rootNamespaceData.getForm(), rootNamespaceData.getNamespace(), attribute );
            if ( values != null ) {
                return values.get( inputName );
            }

        }
        return null;
    }

    protected void setValue( String inputName, Object value, String attribute ) {
        FormNamespaceData rootNamespaceData = getRootNamespaceData( inputName );

        if ( rootNamespaceData != null ) {
            Map values = ( Map ) formProcessor.getAttribute( rootNamespaceData.getForm(), rootNamespaceData.getNamespace(), attribute );
            if ( values == null ) {
                formProcessor.setAttribute( rootNamespaceData.getForm(), rootNamespaceData.getNamespace(), attribute, ( values = new HashMap() ) );
            }
            values.put( inputName, value );
        }
    }

    protected void clearValues( String inputName, String attribute ) {
        FormNamespaceData rootNamespaceData = getRootNamespaceData( inputName );

        if ( rootNamespaceData != null ) {
            Map values = ( Map ) formProcessor.getAttribute( rootNamespaceData.getForm(), rootNamespaceData.getNamespace(), attribute );
            if ( values != null ) {
                for ( Iterator it = values.keySet().iterator(); it.hasNext(); ) {
                    String key = ( String ) it.next();
                    if ( key.startsWith( inputName ) ) {
                        it.remove();
                    }
                }
            }
        }
    }

    protected FormNamespaceData getRootNamespaceData( String inputName ) {
        FormNamespaceData rootNamespaceData = namespaceManager.getRootNamespace( inputName );

        if ( rootNamespaceData != null ) {
            return rootNamespaceData;
        }
        return namespaceManager.getNamespace( namespaceManager.getParentNamespace( inputName ) );
    }
}
