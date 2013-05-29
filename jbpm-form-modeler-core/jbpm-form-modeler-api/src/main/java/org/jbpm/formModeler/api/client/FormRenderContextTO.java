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
package org.jbpm.formModeler.api.client;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.io.Serializable;

@Portable
public class FormRenderContextTO implements Serializable {
    private String ctxUID;
    private Long formId;
    private boolean submit;
    private int errors;

    public FormRenderContextTO() {
    }

    public FormRenderContextTO(String ctxUID, Long formId) {
        this.ctxUID = ctxUID;
        this.formId = formId;
    }

    public FormRenderContextTO(String ctxUID, Long formId, boolean submit, int errors) {
        this.ctxUID = ctxUID;
        this.formId = formId;
        this.submit = submit;
        this.errors = errors;
    }

    public String getCtxUID() {
        return ctxUID;
    }

    public void setCtxUID(String ctxUID) {
        this.ctxUID = ctxUID;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public boolean isSubmit() {
        return submit;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }
}
