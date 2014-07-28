package org.jbpm.formModeler.core.config;

import org.jbpm.formModeler.api.model.SelectValuesProvider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SelectValuesProviderManagerImpl implements SelectValuesProviderManager {

    @Inject
    private Instance<SelectValuesProvider> providers;

    private List<SelectValuesProvider> providerList;

    @PostConstruct
    protected void init() {
        providerList = new ArrayList<SelectValuesProvider>();
        for (SelectValuesProvider provider : providers) {
            providerList.add(provider);
        }
    }

    @Override
    public List<SelectValuesProvider> getProvidersList() {
        return providerList;
    }

    @Override
    public SelectValuesProvider getRangeProviderByType(String providerId) {
        for (SelectValuesProvider provider : providerList) {
            if (provider.getIdentifier().equals(providerId)) return provider;
        }
        return null;
    }
}
