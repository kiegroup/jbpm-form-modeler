package org.jbpm.formModeler.core.config;


import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;

import java.io.Serializable;
import java.util.Map;

public interface DataHolderManager extends Serializable {
    DataHolderBuilder getBuilderByType(String builderId);

    DataHolder createDataHolderByType(String type, Map<String, Object> config);
}
