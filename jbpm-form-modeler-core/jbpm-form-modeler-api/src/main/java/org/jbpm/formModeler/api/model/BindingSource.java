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
package org.jbpm.formModeler.api.model;

public class BindingSource implements  Comparable{
    public static final String BINDING_CODE_TYPE_CLASSNAME = "classNameType";
    public static final String BINDING_CODE_TYPE_DATA_MODEL = "dataModelerEntry";
    public static final String BINDING_CODE_TYPE_BPM_PROCESS = "bpm_process";

    private String id = "";

    private String bindingType = "";

    private String bindingStr = "";

    public BindingSource(String bindingStr, String id, String bindingType) {
        this.bindingStr = bindingStr;
        this.id = id;
        this.bindingType = bindingType;
    }

    public BindingSource() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBindingType() {
        return this.bindingType;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public String getBindingStr() {
        return bindingStr;
    }

    public void setBindingStr(String bindingStr) {
        this.bindingStr = bindingStr;
    }

    public String toString() {
        return bindingStr;
    }

    public boolean equals(Object other) {
        if (!(other instanceof BindingSource)) return false;
        BindingSource castOther = (BindingSource) other;
        return id.equals(castOther.getId());
    }

    public int hashCode() {
        return bindingStr.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((BindingSource)o).getId());
    }
}
