package org.jbpm.formModeler.core.config;


import org.jbpm.formModeler.api.model.DataHolder;

import java.util.Set;

public interface DataHolderProvider {
    Set<DataHolder> getDataHolders();
}
