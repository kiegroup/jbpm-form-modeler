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

import org.apache.commons.logging.Log;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.dataModeler.model.DataModelerDataHolder;
import org.kie.commons.io.IOService;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class DataModelerService implements DataHolderBuilder {
    @Inject
    private Log log;

    @Inject
    private org.kie.workbench.common.screens.datamodeller.service.DataModelerService dataModelerService;

    @Inject
    private ProjectService projectService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public Map getOptions(String path) {
        Map result = new HashMap();
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
    public DataHolder buildDataHolder(Map<String, String> config) {

        return createDataHolder(config.get("path"), config.get("id"), config.get("inputId"), config.get("outId"), config.get("value"), config.get("color"));
    }

    @Override
    public String getId() {
        return Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL;
    }


    public DataHolder createDataHolder(String path, String id, String inputId, String outId, String className, String renderColor) {
        if (path == null) return new DataModelerDataHolder(id, inputId, outId, className, renderColor);
        DataObjectTO dO = getDataObject(className, getPath(path));
        return new DataModelerDataHolder(id, inputId, outId, className, renderColor, dO);
    }

    protected Path getPath(String path) {
        try {
            return paths.convert(ioService.get(new URI(path)), false);
        } catch (Exception e) {
            log.error("Unable to build Path for '" + path + "': ", e);
        }
        return null;
    }

    @Override
    public boolean supportsPropertyType(String className, String path) {
        return getDataObject(className, getPath(path)) != null;
    }

    protected DataObjectTO getDataObject(String className, Path path) {
        Project project = projectService.resolveProject(path);

        DataModelTO dataModelTO = dataModelerService.loadModel(project);
        return dataModelTO.getDataObjectByClassName(className);
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
