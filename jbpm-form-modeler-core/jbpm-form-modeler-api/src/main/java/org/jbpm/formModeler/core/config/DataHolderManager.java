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

package org.jbpm.formModeler.core.config;


import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface DataHolderManager extends Serializable {

    Map<String, String> getHolderColors();

    Set<DataHolderBuilder> getHolderBuilders();

    DataHolderBuilder getBuilderByBuilderType(String builderId);

    DataHolder createDataHolderByType(String type, DataHolderBuildConfig config);

    DataHolderBuilder getBuilderByHolderValueType(String valueType, String context);
}
