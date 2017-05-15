/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.jbpm.formModeler.panels.modeler.backend.indexing;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.commons.validation.PortablePreconditions;

public class FormIndexVisitor extends ResourceReferenceCollector {

    private final Form form;

    public FormIndexVisitor(Form form) {
        this.form = PortablePreconditions.checkNotNull("form",
                                                       form);
    }

    public void visit() {
        visit(form);
    }

    protected void visit(Form form) {
        Resource res = addResource(form.getName(),
                                   ResourceType.FORM);

        for (Field field : form.getFormFields()) {
            visit(field,
                  res);
        }

        for (DataHolder dataHolder : form.getHolders()) {
            visit(dataHolder);
        }
    }

    protected void visit(DataHolder dataHolder) {
        String className = dataHolder.getClassName();
        ResourceReference resRef = null;
        // it's apparently possible for the class name to be null!
        // See org.jbpm.formModeler.core.model.BasicTypeDataHolder.getClassName()
        if (className != null) {
            resRef = addResourceReference(dataHolder.getClassName(),
                                          ResourceType.JAVA);

            if (dataHolder.canHaveChildren()) {
                for (DataFieldHolder field : dataHolder.getFieldHolders()) {
                    if (form.isFieldBinded(dataHolder,
                                           field.getId())) {
                        resRef.addPartReference(field.getId(),
                                                PartType.FIELD);
                        addResourceReference(field.getClassName(),
                                             ResourceType.JAVA);
                    }
                }
            }
        }
    }

    protected void visit(Field field,
                         Resource res) {
        DataHolder holder = field.getForm().getDataHolderByField(field);

        res.addPart(field.getFieldName(),
                    PartType.FORM_FIELD);
        if (holder != null) {
            if (holder.canHaveChildren()) {
                String bindingExpression = StringUtils.defaultIfEmpty(field.getInputBinding(),
                                                                      field.getOutputBinding());

                int slash = bindingExpression.indexOf("/");
                String holderFieldId = bindingExpression.substring(slash + 1);
                DataFieldHolder holderField = holder.getDataFieldHolderById(holderFieldId);
                if (holderField != null) {
                    addResourceReference(holderField.getClassName(),
                                         ResourceType.JAVA);
                } else {
                    // any references to parts or other resources here?
                }
            }
        } else {
            FieldType type = field.getFieldType();
            if (type != null) {
                String fieldClass = type.getFieldClass();
                if (fieldClass != null && !fieldClass.isEmpty()) {
                    addResourceReference(fieldClass,
                                         ResourceType.JAVA);
                }
            } else {
                // empty type.. do we do anything here?
            }
        }

        if (!StringUtils.isEmpty(field.getDefaultSubform())) {
            addResourceReference(field.getDefaultSubform(),
                                 ResourceType.FORM);
        }
        if (!StringUtils.isEmpty(field.getPreviewSubform())) {
            addResourceReference(field.getDefaultSubform(),
                                 ResourceType.FORM);
        }
        if (!StringUtils.isEmpty(field.getTableSubform())) {
            addResourceReference(field.getDefaultSubform(),
                                 ResourceType.FORM);
        }
    }
}
