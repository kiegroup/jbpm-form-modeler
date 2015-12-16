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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

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
