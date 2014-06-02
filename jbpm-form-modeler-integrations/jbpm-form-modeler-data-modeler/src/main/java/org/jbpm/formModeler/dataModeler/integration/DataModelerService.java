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
package org.jbpm.formModeler.dataModeler.integration;

import org.drools.core.util.StringUtils;
import org.guvnor.common.services.builder.LRUBuilderCache;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.core.config.builders.dataHolder.PojoDataHolderBuilder;
import org.jbpm.formModeler.core.config.builders.dataHolder.RangedDataHolderBuilder;
import org.jbpm.formModeler.dataModeler.model.DataModelerDataHolder;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.uberfire.io.IOService;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

@ApplicationScoped
public class DataModelerService implements RangedDataHolderBuilder {
    public static final String HOLDER_TYPE_DATA_MODEL = "dataModelerEntry";

    private Logger log = LoggerFactory.getLogger(DataModelerService.class);

    @Inject
    private org.kie.workbench.common.screens.datamodeller.service.DataModelerService dataModelerService;

    @Inject
    private LRUBuilderCache builderCache;

    @Inject
    private ProjectService projectService;

    @Inject
    private POMService pomService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public Map<String, String> getHolderSources(String path) {
        Map<String, String> result = new TreeMap<String, String>();
        try {
            DataModelTO dataModelTO = dataModelerService.loadModel(projectService.resolveProject(getPath(path)));
            if (dataModelTO != null && dataModelTO.getDataObjects() != null) {
                String className = "";
                for (DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
                    className = dataObjectTO.getClassName();
                    result.put(className, className);
                }
            }
        } catch (Throwable e) {
            result.put("-", "-");
        }
        return result;
    }

    @Override
    public DataHolder buildDataHolder(DataHolderBuildConfig config) {
        DataModelerDataHolder dataHolder = null;

        boolean isExternal = false;

        String path = config.getAttribute("path");
        if (StringUtils.isEmpty(path)) {
            dataHolder = new DataModelerDataHolder(config.getHolderId(), config.getInputId(), config.getOutputId(), config.getValue(), config.getRenderColor());
            isExternal = Boolean.TRUE.equals(config.getAttribute("supportedType"));
        } else {
            Class holderClass = findHolderClass(config.getValue(), config.getAttribute("path"));
            if (holderClass == null) return null;
            DataModelTO dataModelTO = dataModelerService.loadModel(projectService.resolveProject(getPath(path)));
            isExternal = dataModelTO.isExternal(config.getValue());
            dataHolder = new DataModelerDataHolder(config.getHolderId(), config.getInputId(), config.getOutputId(), holderClass, config.getRenderColor());
        }

        if (isExternal) dataHolder.setSupportedType(PojoDataHolderBuilder.HOLDER_TYPE_POJO_CLASSNAME);
        return dataHolder;
    }

    private Class findHolderClass(String className, String path) {
        ClassLoader classLoader = getProjectClassLoader(projectService.resolveProject(getPath(path)));
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            log.warn("Unable to load class '{}': {}", className, e);
        }
        return null;
    }

    @Override
    public String getId() {
        return HOLDER_TYPE_DATA_MODEL;
    }

    protected Path getPath(String path) {
        try {
            return Paths.convert(ioService.get(new URI(path)));
        } catch (Exception e) {
            log.error("Unable to build Path for {}': {}", path, e);
        }
        return null;
    }

    @Override
    public boolean supportsPropertyType(String className, String path) {
        return getDataObject(className, getPath(path)) != null;
    }

    protected DataObjectTO getDataObject(String className, Path path) {
        DataModelTO dataModelTO = getDataModel(path);

        DataObjectTO result = dataModelTO.getDataObjectByClassName(className);

        if (result == null) {
            for (DataObjectTO externalDataObject : dataModelTO.getExternalClasses()) {
                if (className.equals(externalDataObject.getClassName())) return externalDataObject;
            }
        }

        return result;
    }

    protected DataModelTO getDataModel(Path path) {
        Project project = projectService.resolveProject(path);
        return dataModelerService.loadModel(project);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String[] getSupportedHolderTypes() {
        return new String[]{PojoDataHolderBuilder.HOLDER_TYPE_POJO_CLASSNAME};
    }

    protected ClassLoader getProjectClassLoader( Project project ) {
        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();
        return classLoader;
    }

    @Override
    public String getDataHolderName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.dataModeler.messages", locale);
        return bundle.getString("dataHolder_dataModeler");
    }
}
