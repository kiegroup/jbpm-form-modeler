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
package org.jbpm.formModeler.designer.integration.impl;

import org.apache.commons.lang.StringUtils;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.DataHolderManager;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.config.builders.DataHolderBuilder;
import org.jbpm.formModeler.designer.integration.BPMNFormBuilderService;
import org.kie.commons.io.IOService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@ApplicationScoped
public class BPMNFormBuilderServiceImpl implements BPMNFormBuilderService {

    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private DataHolderManager dataHolderManager;


    public String buildFormXML(FileSystem fs, String fileName, String uri, Definitions source, String id) throws Exception {
            Path formPath = PathFactory.newPath(fs, fileName, uri);
            org.kie.commons.java.nio.file.Path kiePath = paths.convert(formPath);
            String xml = ioService.readAllString(kiePath).trim();
            Set holders = getDataHolders(source, formPath, id);

            if (holders == null) return null;

            if (StringUtils.isEmpty(xml)) {
                Form form = formManager.createForm(fileName);
                for(Iterator it = holders.iterator(); it.hasNext();) {
                    DataHolder holder = (DataHolder) it.next();
                    formManager.addAllDataHolderFieldsToForm(form, holder);
                    return formSerializationManager.generateFormXML(form);
                }
            } else {
                Form form = formSerializationManager.loadFormFromXML(xml);
                for(Iterator it = holders.iterator(); it.hasNext();) {
                    DataHolder holder = (DataHolder) it.next();

                    if (!form.containsHolder(holder)) {
                        formManager.addAllDataHolderFieldsToForm(form, holder);
                    }

                    return formSerializationManager.generateFormXML(form);
                }
            }
        return null;
    }

    public Set<DataHolder> getDataHolders(Definitions source, Path context, String resourceId) {
        if (source == null || context == null) return null;

        Map<String, Map> associations = new HashMap<String, Map>();

        List<RootElement> rootElements = source.getRootElements();

        for(RootElement re : rootElements) {
            if(re instanceof org.eclipse.bpmn2.Process) {
                Process process = (Process) re;
                if(process != null && process.getId() != null && process.getId().length() > 0) {
                    List<Property> processProperties = process.getProperties();

                    // if resourceId is null we want to create the process starting form, so we only check process vars.
                    if (StringUtils.isEmpty(resourceId)) return getProcessDataHolders(processProperties, context);

                    String[] colors = dataHolderManager.getHolderColors().keySet().toArray(new String[0]);

                    int index = 0;

                    for (Property prop : processProperties) {
                        Map<String, Object> config = new HashMap<String, Object>();

                        config.put("value", prop.getItemSubjectRef().getStructureRef());
                        config.put("path", context);
                        config.put("color", colors[index]);

                        associations.put(prop.getName(), config);
                        if (index == colors.length - 1) index = 0;
                        else index++;
                    }

                    for(FlowElement fe : process.getFlowElements()) {
                        if(fe instanceof UserTask && fe.getName().equals(resourceId)) {
                            UserTask utask = (UserTask) fe;
                            List<DataInputAssociation> dataInputAssociations = utask.getDataInputAssociations();

                            if (dataInputAssociations != null) {
                                for (DataInputAssociation inputAssociation : dataInputAssociations) {
                                    if (inputAssociation.getSourceRef() != null && inputAssociation.getTargetRef() != null) {
                                        DataInput input = (DataInput) inputAssociation.getSourceRef().get(0);
                                        Map config = associations.get(input.getName());

                                        if (config != null) config.put("id", ((DataInput) inputAssociation.getTargetRef()).getName());
                                    }
                                }
                            }

                            List<DataOutputAssociation> dataOutputAssociations = utask.getDataOutputAssociations();
                            if (dataOutputAssociations != null) {
                                for (DataOutputAssociation outputAssociation : dataOutputAssociations) {

                                    if (outputAssociation.getSourceRef() != null && outputAssociation.getTargetRef() != null) {
                                        Map config = associations.get(outputAssociation.getTargetRef().getId());

                                        if (config != null) {
                                            DataOutput output = (DataOutput) outputAssociation.getSourceRef().get(0);
                                            if (output != null) config.put("outId", output.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Set<DataHolder> result = new TreeSet<DataHolder>();

        for (Iterator it = associations.keySet().iterator(); it.hasNext();) {
            Map config = associations.get(it.next());
            if (config.size() > 3) result.add(dataHolderManager.createDataHolderByType((String) config.get("value"), config));
        }

        return result;
    }

    private Set<DataHolder> getProcessDataHolders(List<Property> processProperties, Path path) {
        Set<DataHolder> result = new TreeSet<DataHolder>();

        String[] colors = dataHolderManager.getHolderColors().keySet().toArray(new String[0]);

        int index = 0;

        for (Property prop : processProperties) {
            String propertyName = prop.getName();
            String propertyType = prop.getItemSubjectRef().getStructureRef();
            DataHolderBuilder builder = dataHolderManager.getBuilderByHolderValueType(propertyType, path);

            if (builder != null) {
                Map<String, Object> config = new HashMap<String, Object>();
                config.put("outId", propertyName);
                config.put("color", colors[index]);
                config.put("value", propertyType);
                config.put("path", path);
                result.add(builder.buildDataHolder(config));
                if (index == colors.length - 1) index = 0;
                else index++;
            }
        }

        return result;
    }
}
