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
import org.jbpm.formModeler.core.model.PojoDataHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public class PojoDataHolderBuilder implements DataHolderBuilder {

    public static final String HOLDER_TYPE_POJO_CLASSNAME = "className";

    private Logger log = LoggerFactory.getLogger(PojoDataHolderBuilder.class);

    @Override
    public String getId() {
        return HOLDER_TYPE_POJO_CLASSNAME;
    }

    @Override
    public DataHolder buildDataHolder(DataHolderBuildConfig config) {
        try {
            Class.forName(config.getValue());
            return new PojoDataHolder(config.getHolderId(), config.getInputId(), config.getOutputId(), config.getValue(), config.getRenderColor());
        } catch (ClassNotFoundException e) {
            log.warn("Unable to load class '{0}': {1}", config.getValue(), e);
        }
        return null;
    }

    @Override
    public boolean supportsPropertyType(String type, String path) {
        try {
            Class clazz = Class.forName(type);
            return clazz != null;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 100000;
    }

    @Override
    public String[] getSupportedHolderTypes() {
        return new String[0];
    }

    @Override
    public String getDataHolderName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.config.builders.dataHolder.messages", locale);
        return bundle.getString("dataHolder_className");
    }
}
