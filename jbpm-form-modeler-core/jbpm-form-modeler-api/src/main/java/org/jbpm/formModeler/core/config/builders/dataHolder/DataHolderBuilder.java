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
package org.jbpm.formModeler.core.config.builders.dataHolder;

import org.jbpm.formModeler.api.model.DataHolder;

import java.util.Locale;
import java.util.Map;

public interface DataHolderBuilder {
    String getId();
    String getDataHolderName(Locale locale);
    DataHolder buildDataHolder(DataHolderBuildConfig config);
    boolean supportsPropertyType(String type, String path);
    int getPriority();
    String[] getSupportedHolderTypes();
}
