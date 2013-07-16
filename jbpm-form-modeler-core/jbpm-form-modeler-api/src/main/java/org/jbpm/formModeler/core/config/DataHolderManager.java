package org.jbpm.formModeler.core.config;


import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;

import java.io.Serializable;
import java.util.Map;

public interface DataHolderManager extends Serializable {

    Map<String, String> getHolderColors();

    DataHolderBuilder getBuilderByBuilderType(String builderId);

    DataHolder createDataHolderByType(String type, Map<String, Object> config);

    DataHolderBuilder getBuilderByHolderValueType(String valueType, Object context);
}
