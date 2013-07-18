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
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.DataHolderManager;
import org.jbpm.formModeler.core.config.FieldTypeManager;
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

    @Inject
    private FieldTypeManager fieldTypeManager;

    public String buildFormXML(FileSystem fs, String fileName, String uri, Definitions source, String id) throws Exception {
            Path formPath = PathFactory.newPath(fs, fileName, uri);
            org.kie.commons.java.nio.file.Path kiePath = paths.convert(formPath);
        
            String xml = null;
            String xmlForm = null;

            Set holders = getDataHolders(source, formPath, id);
            //TODO check this criteria. By the moment if we don't have data holders there's no data to enter.
            if (holders == null || holders.size() == 0) return null;

            if (ioService.exists(kiePath)) {
                xml = ioService.readAllString(kiePath).trim();
            }

            if (StringUtils.isEmpty(xml)) {
                Form form = formManager.createForm(fileName);
                for(Iterator it = holders.iterator(); it.hasNext();) {
                    DataHolder holder = (DataHolder) it.next();
                    formManager.addAllDataHolderFieldsToForm(form, holder);
                }
                xmlForm = formSerializationManager.generateFormXML(form);
            } else {
                Form form = formSerializationManager.loadFormFromXML(xml);
                for(Iterator it = holders.iterator(); it.hasNext();) {
                    DataHolder holder = (DataHolder) it.next();
                    if (!form.containsHolder(holder)) {
                        //Version 1 merge algorithm.
                        formManager.addAllDataHolderFieldsToForm(form, holder);
                    }
                }
                xmlForm = formSerializationManager.generateFormXML(form);
            }
        return xmlForm;
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
                        associations.put(prop.getId(), config);
                        if (index == colors.length - 1) index = 0;
                        else index++;
                    }

                    for(FlowElement fe : process.getFlowElements()) {
                        if(fe instanceof UserTask && fe.getId().equals(resourceId)) {
                            UserTask utask = (UserTask) fe;
                            List<DataInputAssociation> dataInputAssociations = utask.getDataInputAssociations();

                            if (dataInputAssociations != null) {
                                for (DataInputAssociation inputAssociation : dataInputAssociations) {

                                    if (inputAssociation.getSourceRef() != null && inputAssociation.getSourceRef().size() > 0 && inputAssociation.getTargetRef() != null) {

                                        String variableId = inputAssociation.getSourceRef().get(0).getId();
                                        DataInput input = (DataInput)inputAssociation.getTargetRef();
                                        String id = input != null ? input.getName() : null;
                                        Map config = ((variableId != null) && (id != null)) ? associations.get(variableId) : null;

                                        if (config != null) config.put("id", id);
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

                                        Map config = ((variableId != null) && (outId != null)) ? associations.get(variableId) : null;
                                        if (config != null) config.put("outId", outId);
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
            DataHolder dataHolder;

            if (config.size() > 3) {
                dataHolder = createDataHolder(config);
                if (dataHolder != null) result.add(dataHolder);
            }
        }

        return result;
    }

    private Set<DataHolder> getProcessDataHolders(List<Property> processProperties, Path path) {
        Set<DataHolder> result = new TreeSet<DataHolder>();

        String[] colors = dataHolderManager.getHolderColors().keySet().toArray(new String[0]);

        int index = 0;

        for (Property prop : processProperties) {
            String propertyName = prop.getId();
            String propertyType = prop.getItemSubjectRef().getStructureRef();
            DataHolder dataHolder = null;

            Map<String, Object> config = new HashMap<String, Object>();
            config.put("outId", propertyName);
            config.put("color", colors[index]);
            config.put("value", propertyType);
            config.put("path", path);

            dataHolder = createDataHolder(config);
            if (dataHolder != null) result.add(dataHolder);

            if (index == colors.length - 1) index = 0;
            else index++;
        }

        return result;
    }
    
    private DataHolder createDataHolder(Map<String, Object> config) {

        String type = (String) config.get("value");
        String className;
        if (isBaseType(type)) type = normalizeBaseType(type);

        DataHolderBuilder builder = dataHolderManager.getBuilderByHolderValueType(type, config.get("path"));
        FieldType fieldType = fieldTypeManager.getTypeByClass(type);
        if (fieldType != null && builder != null) {
            className = "Subform".equals(fieldType.getCode()) || "MultipleSubform".equals(fieldType.getCode()) ? type : fieldType.getCode();
            config.put("value", className);
            return builder.buildDataHolder(config);
        }

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
            "BigDecimal".equals(type) || java.math.BigDecimal.class.getName().equals(type);
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
        }
        return type;
    }
}
