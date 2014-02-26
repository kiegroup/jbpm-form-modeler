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
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuildConfig;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuilder;
import org.jbpm.formModeler.designer.integration.BPMNFormBuilderService;
import org.uberfire.io.IOService;
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
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private DataHolderManager dataHolderManager;

    @Inject
    private FieldTypeManager fieldTypeManager;

    public String buildEmptyFormXML(String fileName) {
        Form form = formManager.createForm(fileName);
        String xmlForm = formSerializationManager.generateFormXML(form);
        return xmlForm;
    }
    
    
    public String buildFormXML(FileSystem fs, String fileName, String uri, Definitions source, String id) throws Exception {
        Path formPath = PathFactory.newPath(fs, fileName, uri);
        org.uberfire.java.nio.file.Path kiePath = Paths.convert(formPath);


        Form form;
        Set<DataHolder> holders;

        if (!ioService.exists(kiePath)) {
            form = formManager.createForm(fileName);
            holders = getDataHolders(source, Paths.convert(kiePath.getParent()), id);
        } else {
            form = formSerializationManager.loadFormFromXML(ioService.readAllString(kiePath).trim());
            holders = getDataHolders(source, formPath, id);
        }

        addHoldersToForm(form, holders);

        return formSerializationManager.generateFormXML(form);
    }

    protected void addHoldersToForm(Form form, Set<DataHolder> holders) throws Exception {
        if (holders == null) return;
        for(Iterator it = holders.iterator(); it.hasNext();) {
            DataHolder holder = (DataHolder) it.next();
            formManager.addAllDataHolderFieldsToForm(form, holder);
        }
    }

    public Set<DataHolder> getDataHolders(Definitions source, Path context, String resourceId) {
        if (source == null || context == null) return null;

        Map<String, DataHolderBuildConfig> associations = new HashMap<String, DataHolderBuildConfig>();
        List<RootElement> rootElements = source.getRootElements();

        String contextUri = Paths.convert(context).toUri().toString();

        for(RootElement re : rootElements) {
            if(re instanceof org.eclipse.bpmn2.Process) {
                Process process = (Process) re;
                if(process != null && process.getId() != null && process.getId().length() > 0) {
                    List<Property> processProperties = process.getProperties();

                    // if resourceId is null we want to create the process starting form, so we only check process vars.
                    if (StringUtils.isEmpty(resourceId)) return getProcessDataHolders(processProperties, contextUri);

                    String[] colors = dataHolderManager.getHolderColors().keySet().toArray(new String[0]);
                    int index = 0;

                    for (Property prop : processProperties) {
                        String holderClass = StringUtils.defaultIfEmpty(prop.getItemSubjectRef().getStructureRef(), "java.lang.Object");
                        DataHolderBuildConfig config = new DataHolderBuildConfig(prop.getId(), "", "", colors[index], holderClass);
                        config.addAttribute("path", contextUri);
                        associations.put(prop.getId(), config);
                        if (index == colors.length - 1) index = 0;
                        else index++;
                    }
                    getDataHoldersFromElements(process, resourceId, associations);
                }
            }
        }

        Set<DataHolder> result = new TreeSet<DataHolder>();

        for (Iterator it = associations.keySet().iterator(); it.hasNext();) {
            DataHolderBuildConfig config = associations.get(it.next());
            DataHolder dataHolder = createDataHolder(config);
            if (dataHolder != null) result.add(dataHolder);
        }

        return result;
    }

    private void getDataHoldersFromElements(FlowElementsContainer container, String resourceId, Map<String, DataHolderBuildConfig> associations) {
        for(FlowElement fe : container.getFlowElements()) {
            if(fe instanceof UserTask && fe.getId().equals(resourceId)) {
                UserTask utask = (UserTask) fe;
                List<DataInputAssociation> dataInputAssociations = utask.getDataInputAssociations();

                if (dataInputAssociations != null) {
                    for (DataInputAssociation inputAssociation : dataInputAssociations) {

                        if (inputAssociation.getSourceRef() != null && inputAssociation.getSourceRef().size() > 0 && inputAssociation.getTargetRef() != null) {

                            String variableId = inputAssociation.getSourceRef().get(0).getId();
                            DataInput input = (DataInput)inputAssociation.getTargetRef();
                            String id = input != null ? input.getName() : null;
                            DataHolderBuildConfig config = ((variableId != null) && (id != null)) ? associations.get(variableId) : null;

                            if (config != null) config.setInputId(id);
                        }
                    }
                }

                List<DataOutputAssociation> dataOutputAssociations = utask.getDataOutputAssociations();
                if (dataOutputAssociations != null) {
                    for (DataOutputAssociation outputAssociation : dataOutputAssociations) {

                        if (outputAssociation.getSourceRef() != null && outputAssociation.getSourceRef().size() > 0 && outputAssociation.getTargetRef() != null) {

                            String variableId = outputAssociation.getTargetRef().getId();
                            DataOutput output = (DataOutput) outputAssociation.getSourceRef().get(0);
                            String outId = output != null ? output.getName() : null;

                            DataHolderBuildConfig config = ((variableId != null) && (outId != null)) ? associations.get(variableId) : null;
                            if (config != null) config.setOutputId(outId);
                        }
                    }
                }
            } else if(fe instanceof FlowElementsContainer) {
                getDataHoldersFromElements((FlowElementsContainer) fe, resourceId, associations);
            }
        }
    }

    private Set<DataHolder> getProcessDataHolders(List<Property> processProperties, String path) {
        Set<DataHolder> result = new TreeSet<DataHolder>();

        String[] colors = dataHolderManager.getHolderColors().keySet().toArray(new String[0]);

        int index = 0;

        for (Property prop : processProperties) {
            String propertyName = prop.getId();
            String propertyType = StringUtils.defaultIfEmpty(prop.getItemSubjectRef().getStructureRef(), "java.lang.Object");

            DataHolderBuildConfig config = new DataHolderBuildConfig(propertyName, "", propertyName, colors[index], propertyType);
            config.addAttribute("path", path);

            DataHolder dataHolder = createDataHolder(config);
            if (dataHolder != null) result.add(dataHolder);

            if (index == colors.length - 1) index = 0;
            else index++;
        }

        return result;
    }
    
    private DataHolder createDataHolder(DataHolderBuildConfig config) {

        String type = config.getValue();
        if (isBaseType(type)) type = normalizeBaseType(type);

        DataHolderBuilder builder = dataHolderManager.getBuilderByHolderValueType(type, config.getAttribute("path"));
        config.setValue(type);
        if (builder != null) return builder.buildDataHolder(config);

        return null;
    }

    //TODO move this methods to another place
    private boolean isBaseType(String type) {
        return
            "String".equals(type) || String.class.getName().equals(type) ||
            "Integer".equals(type) || Integer.class.getName().equals(type) ||
            "Short".equals(type) || Short.class.getName().equals(type) ||
            "Long".equals(type) || Long.class.getName().equals(type) ||
            "Float".equals(type) || Float.class.getName().equals(type) ||
            "Double".equals(type) || Double.class.getName().equals(type) ||
            "Boolean".equals(type) || Boolean.class.getName().equals(type) ||
            "Date".equals(type) || Date.class.getName().equals(type) ||
            "BigDecimal".equals(type) || java.math.BigDecimal.class.getName().equals(type) ||
            "BigInteger".equals(type) || java.math.BigInteger.class.getName().equals(type);
    }
    
    private String normalizeBaseType(String type) {
        if (type.length() < 10) {
            if ("String".equals(type)) return String.class.getName();
            if ("Integer".equals(type)) return Integer.class.getName();
            if ("Short".equals(type)) return Short.class.getName();
            if ("Long".equals(type)) return Long.class.getName();
            if ("Float".equals(type)) return Float.class.getName();
            if ("Double".equals(type)) return Double.class.getName();
            if ("Boolean".equals(type)) return Boolean.class.getName();
            if ("Date".equals(type)) return Date.class.getName();
            if ("BigDecimal".equals(type)) return java.math.BigDecimal.class.getName();
            if ("BigInteger".equals(type)) return java.math.BigInteger.class.getName();
        }
        return type;
    }
}
