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
package org.jbpm.formModeler.api.util.helpers;

import org.jbpm.formModeler.api.model.Form;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;

public class EditorHelper implements Serializable {

    public static EditorHelper lookup() {
        return (EditorHelper) CDIHelper.getBeanByType(EditorHelper.class);
    }

    private Long originalForm;
    private HashMap loadedForms;

    public Long getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(Long originalForm) {
        this.originalForm = originalForm;
    }

    public void setFormToEdit(String path, Form formToEdit) {
        if (loadedForms==null) loadedForms=new HashMap();
        this.loadedForms.put(path,formToEdit);
    }

    public Form getFormToEdit(String path) {
        if (loadedForms!=null) return (Form)loadedForms.get(path);
        else return null;
    }

    public Form removeEditingForm(String path) {
        if (loadedForms != null) return (Form) loadedForms.remove(path);
        else return null;
    }
}
