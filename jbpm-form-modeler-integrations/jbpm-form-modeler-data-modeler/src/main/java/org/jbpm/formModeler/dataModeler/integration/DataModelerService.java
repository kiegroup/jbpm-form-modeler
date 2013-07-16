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

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.dataModeler.model.DataModelerDataHolder;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class DataModelerService implements DataHolderBuilder {

    @Inject
    private org.kie.workbench.common.screens.datamodeller.service.DataModelerService dataModelerService;

    @Inject
    private ProjectService projectService;

    @Override
    public Map getOptions(Object path) {
        Map result = new HashMap();
        try {
            DataModelTO dataModelTO = dataModelerService.loadModel(projectService.resolveProject(((Path) path)));
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
    public DataHolder buildDataHolder(Map<String, Object> config) {
        return createDataHolder(config.get("path"), (String) config.get("id"), (String) config.get("outId"), (String) config.get("value"), (String) config.get("color"));
    }

    @Override
    public String getId() {
        return Form.HOLDER_TYPE_CODE_POJO_DATA_MODEL;
    }

    public DataHolder createDataHolder(Object path, String id, String outId, String className, String renderColor) {
        if (path == null) return new DataModelerDataHolder(id, outId, className, renderColor);
        DataObjectTO dO = getDataObject(className, (Path) path);
        return new DataModelerDataHolder(id, outId, className, renderColor, dO);
    }

    @Override
    public boolean supportsPropertyType(String className, Object path) {
        return getDataObject(className, (Path) path) != null;
    }

    protected DataObjectTO getDataObject(String className, Path path) {
        Project project = projectService.resolveProject(((Path) path));

        DataModelTO dataModelTO = dataModelerService.loadModel(project);
        return dataModelTO.getDataObjectByClassName(className);
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
