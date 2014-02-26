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
package org.jbpm.formModeler.core.config;


import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class DataHolderManagerImpl implements DataHolderManager {

    @Inject
    private Instance<DataHolderBuilder> holderBuilders;
    private Set<DataHolderBuilder> builders;
    private Map<String, String> colors;

    @PostConstruct
    protected void initializeHolders() {
        colors = new HashMap<String, String>();

        colors.put("#FF8881", "holder_color_red");
        colors.put("#FF54A7", "holder_color_pink");
        colors.put("#FBB767", "holder_color_orange");
        colors.put("#E9E371", "holder_color_yellow");
        colors.put("#A7E690", "holder_color_green");
        colors.put("#9BCAFA", "holder_color_blue");
        colors.put("#0000A0", "holder_color_dark_blue");
        colors.put("#B29FE4", "holder_color_violet");
        colors.put("#BBBBBB", "holder_color_grey");
        colors.put("#000000", "holder_color_black");

        builders = new TreeSet<DataHolderBuilder>(new Comparator<DataHolderBuilder>() {
            @Override
            public int compare(DataHolderBuilder o1, DataHolderBuilder o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });

        for (DataHolderBuilder holderBuilder : holderBuilders) {
            builders.add(holderBuilder);
        }
    }

    @Override
    public Map<String, String> getHolderColors() {
        return colors;
    }

    @Override
    public DataHolderBuilder getBuilderByHolderValueType(String valueType, String context) {
        for(DataHolderBuilder builder : builders) {
            if(builder.supportsPropertyType(valueType, context)) return builder;
        }
        return null;
    }

    @Override
    public DataHolderBuilder getBuilderByBuilderType(String builderId) {
        for(DataHolderBuilder builder : builders) {
            if (builder.getId().equals(builderId)) return builder;
        }
        return null;
    }

    protected DataHolderBuilder getBuilderByCompatibleType(String builderId) {
        for(DataHolderBuilder builder : builders) {
            if (Arrays.asList(builder.getSupportedHolderTypes()).contains(builderId)) return builder;
        }
        return null;
    }

    @Override
    public DataHolder createDataHolderByType(String type, DataHolderBuildConfig config) {
        DataHolderBuilder builder = getBuilderByBuilderType(type);
        if (builder == null) return null;

        DataHolder holder = builder.buildDataHolder(config);

        if (holder == null) {
            builder = getBuilderByCompatibleType(type);
            if (builder != null) holder = builder.buildDataHolder(config);
        }

        return holder;
    }

    public Set<DataHolderBuilder> getHolderBuilders() {
        return builders;
    }
}
