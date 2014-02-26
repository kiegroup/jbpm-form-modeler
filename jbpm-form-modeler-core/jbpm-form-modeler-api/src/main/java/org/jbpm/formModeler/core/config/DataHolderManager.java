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
