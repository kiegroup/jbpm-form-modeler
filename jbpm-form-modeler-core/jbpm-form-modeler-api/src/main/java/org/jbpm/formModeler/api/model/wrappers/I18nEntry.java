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
package org.jbpm.formModeler.api.model.wrappers;

import java.io.Serializable;
import java.util.Map;

/**
 * Definition for I18nEntries used on I18nProperties.
 */
public interface I18nEntry extends Serializable, Map.Entry {
    public abstract String getLang();

    public abstract Object getValue();

    public abstract void setLang(String s);

    public abstract Object setValue(Object s);
}
