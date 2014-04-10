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
package org.jbpm.formModeler.components.editor;

import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.config.builders.dataHolder.RangedDataHolderBuilder;
import org.slf4j.Logger;

import org.jbpm.formModeler.core.config.DataHolderManager;
import org.jbpm.formModeler.core.config.FieldTypeManager;
import org.jbpm.formModeler.api.model.DataFieldHolder;
import org.jbpm.formModeler.api.model.DataHolder;
import org.jbpm.formModeler.api.model.FieldType;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.builders.dataHolder.DataHolderBuilder;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.Formatter;
import org.jbpm.formModeler.service.bb.mvc.taglib.formatter.FormatterException;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Named("DataHoldersFormFormatter")
public class DataHoldersFormFormatter extends Formatter {

    private Logger log = LoggerFactory.getLogger(DataHoldersFormFormatter.class);

    @Inject
    private WysiwygFormEditor wysiwygFormEditor;

    @Inject
    private DataHolderManager dataHolderManager;

    @Inject
    private FieldTypeManager fieldTypeManager;

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws FormatterException {
        try {
            if (WysiwygFormEditor.EDITION_OPTION_BINDINGS_FIELDS.equals(wysiwygFormEditor.getCurrentEditionOption())) {
                renderPendingFields();
            } else {
                renderDataHolders();
            }
        } catch (Exception e) {
            log.error(" DataHoldersFormFormatter rendering error");
        }
    }

    public void renderDataHolders() {
        try {
            renderFragment("outputStartHeader");
            for (DataHolderBuilder builder : dataHolderManager.getHolderBuilders()) {

                if (builder instanceof RangedDataHolderBuilder) notifyHolderBuilder((RangedDataHolderBuilder)builder, wysiwygFormEditor.getCurrentEditionContext().getPath());
                else notifyHolderBuilder(builder);
            }
            renderFragment("outputEndHeader");

            renderFragment("outputStart");

            Form form = wysiwygFormEditor.getCurrentForm();
            Set<DataHolder> holders = form.getHolders();
            String existingIds ="\"\"";
            String existingInputIds ="\"\"";
            String existingOutputIds ="\"\"";
            for (DataHolder holder : holders) {
                if (!StringUtils.isEmpty(holder.getInputId())) existingInputIds+= ", \""+holder.getInputId()+"\" ";
                if (!StringUtils.isEmpty(holder.getOuputId())) existingOutputIds+= ", \""+holder.getOuputId()+"\" ";
                if (!StringUtils.isEmpty(holder.getUniqeId())) existingIds+= ", \""+holder.getUniqeId()+"\" ";
            }

            setAttribute("existingIds", existingIds);
            setAttribute("existingInputIds", existingInputIds);
            setAttribute("existingOutputIds", existingOutputIds);
            renderFragment("outputFormAddHolderStart");

            Map<String, String> colors = dataHolderManager.getHolderColors();

            for (Iterator it = colors.keySet().iterator(); it.hasNext();) {
                String color = (String) it.next();
                String name = colors.get(color);
                setAttribute("color", color);
                setAttribute("name", name);
                renderFragment("color");
            }

            Map<String, String> builderNames = new HashMap<String, String>();

            // Render source types sorted by value.
            renderFragment("outputFormHolderTypes");

            for (DataHolderBuilder builder : dataHolderManager.getHolderBuilders()) {
                String holderName = builder.getDataHolderName(getLocale());
                builderNames.put(builder.getId(), holderName);
                setAttribute("holderType", builder.getId());
                setAttribute("holderName", holderName);
                renderFragment("outputHolderType");
            }

            renderFragment("outputEndHolderTypes");

            renderFragment("outputStartBindings");

            int i=0;
            for (DataHolder holder : holders) {
                setAttribute("id", StringUtils.defaultString(holder.getUniqeId()));
                setAttribute("input_id", StringUtils.defaultString(holder.getInputId()));
                setAttribute("outId", StringUtils.defaultString(holder.getOuputId()));
                setAttribute("deleteId", StringUtils.defaultString(holder.getUniqeId()));
                setAttribute("type", builderNames.get(StringUtils.defaultIfEmpty(holder.getSupportedType(), holder.getTypeCode())));
                setAttribute("renderColor", holder.getRenderColor());
                setAttribute("value", holder.getInfo());
                setAttribute("rowStyle",i%2==1 ? "skn-even_row":"skn-odd_row" );
                i++;
                renderFragment("outputBindings");
            }
            renderFragment("outputEndBindings");

            renderFragment("outputEnd");
        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    public void notifyHolderBuilder(DataHolderBuilder builder) throws Exception {
        setAttribute("id", builder.getId());
        renderFragment("notifyHolderBuilder");
    }


    public void notifyHolderBuilder(RangedDataHolderBuilder builder, String path) throws Exception {
        setAttribute("id", builder.getId());
        Map<String, String> values = builder.getHolderSources(path);

        StringBuffer comboJSON = new StringBuffer("[");

        if (values!= null ) {
            for (Iterator it = values.keySet().iterator(); it.hasNext();) {
                if (comboJSON.length() > 1) comboJSON .append(",");
                String key = (String) it.next();
                String value = values.get(key);
                comboJSON.append("{key:\"").append(key).append("\",").append("value:\"").append(value).append("\"}");
            }
        }
        comboJSON.append("]");
        setAttribute("comboValues", comboJSON.toString());
        renderFragment("notifyComboHolderBuilder");
    }

    public void renderPendingFields() throws Exception {
        WysiwygFormEditor wysiwygFormEditor = WysiwygFormEditor.lookup();
        Form form = wysiwygFormEditor.getCurrentForm();
        Set<DataHolder> holders = form.getHolders();

        renderFragment("outputStart");

        for (DataHolder dataHolder : holders) {
            Set<DataFieldHolder> dataFieldHolders = dataHolder.getFieldHolders();

            if (dataFieldHolders != null) {
                if (dataHolder.canHaveChildren()) {
                    setAttribute("id", dataHolder.getUniqeId());
                    setAttribute("type", dataHolder.getTypeCode());
                    setAttribute("renderColor", dataHolder.getRenderColor());

                    if (dataHolder.getUniqeId() != null && dataHolder.getUniqeId().equals(wysiwygFormEditor.getLastDataHolderUsedId())) {
                        setAttribute("open", Boolean.TRUE);
                    } else {
                        setAttribute("open", Boolean.FALSE);
                    }
                    String holderName = "";

                    holderName=dataHolder.getUniqeId();
                    if (holderName.length() > 20) holderName = holderName.substring(0, 19) + "...";

                    setAttribute("showHolderName", holderName);
                    renderFragment("outputBinding");
                }

                for (DataFieldHolder dataFieldHolder : dataFieldHolders) {
                    String fieldName = dataFieldHolder.getId();
                    if (fieldName != null && !form.isFieldBinded(dataHolder, fieldName)) {
                        renderAddField(fieldName, dataFieldHolder, dataHolder);
                    }
                }

                if (dataHolder.canHaveChildren()) {
                    renderFragment("outputEndBinding");
                }
            }

        }

        renderFragment("outputEnd");
    }

    public void renderAddField(String fieldName, DataFieldHolder dataFieldHolder, DataHolder dataHolder) {
        FieldType type = fieldTypeManager.getTypeByClass(dataFieldHolder.getClassName());

        if (type == null) return;
        setAttribute("renderColor", dataHolder.getRenderColor());;
        setAttribute("className", dataFieldHolder.getClassName());
        setAttribute("typeName", type.getCode());
        setAttribute("bindingId", dataHolder.getUniqeId());
        setAttribute("showFieldName", ((fieldName != null && fieldName.length() < 17) ? fieldName : fieldName.substring(0, 13) + "..."));

        setAttribute("iconUri", fieldTypeManager.getIconPathForCode(type.getCode()));
        setAttribute("fieldName", fieldName);
        renderFragment("outputField");
    }
}
