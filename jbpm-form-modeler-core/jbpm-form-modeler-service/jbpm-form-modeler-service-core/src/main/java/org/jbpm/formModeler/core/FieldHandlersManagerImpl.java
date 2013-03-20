package org.jbpm.formModeler.core;

import org.jbpm.formModeler.api.processing.FieldHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class FieldHandlersManagerImpl extends FieldHandlersManager {
    @Override
    public FieldHandler[] getHandlers() {
        ArrayList list = new ArrayList();
        //static handlers
        if (getStaticHandlers() != null)
            list.addAll(Arrays.asList(getStaticHandlers()));

        return (FieldHandler[]) list.toArray(new FieldHandler[list.size()]);
    }
}
