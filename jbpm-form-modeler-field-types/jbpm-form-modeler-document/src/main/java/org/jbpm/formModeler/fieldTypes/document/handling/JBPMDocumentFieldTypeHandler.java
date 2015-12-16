/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.formModeler.fieldTypes.document.handling;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.document.Document;
import org.jbpm.document.marshalling.AbstractDocumentMarshallingStrategy;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.fieldHandlers.plugable.PlugableFieldHandler;
import org.jbpm.formModeler.service.bb.mvc.components.ControllerStatus;
import org.jbpm.formModeler.service.bb.mvc.controller.RequestContext;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

@Named("org.jbpm.formModeler.fieldTypes.document.handling.JBPMDocumentFieldTypeHandler")
public class JBPMDocumentFieldTypeHandler extends PlugableFieldHandler {

    private Logger log = LoggerFactory.getLogger(JBPMDocumentFieldTypeHandler.class);

    @Inject
    private DeploymentService deploymentService;

    @Inject
    private FormRenderContextManager formRenderContextManager;


    public final String SIZE_UNITS[] = new String[]{"bytes", "Kb", "Mb"};

    protected String dropIcon;
    protected String iconFolder;
    protected String defaultFileIcon;
    protected Map<String, String> icons;

    @PostConstruct
    public void init() {
        // Initializing the images paths that are going to be used in the UI
        dropIcon = "/formModeler/images/general/16x16/ico-trash.png";
        iconFolder = "/formModeler/images/fileTypeIcons/16x16/";
        defaultFileIcon = "RTF.png";

        icons = new HashMap<String, String>();
        icons.put("ace", "ACE.png");
        icons.put("css", "CSS.png");
        icons.put("csv", "CSV.png");
        icons.put("swf", "Flash.png");
        icons.put("fla", "Flash.png");
        icons.put("htm", "HTML.png");
        icons.put("html", "HTML.png");
        icons.put("jar", "JAR.png");
        icons.put("java", "Java.png");
        icons.put("jsp", "JSP.png");
        icons.put("mp3", "MP3.png");
        icons.put("doc", "MSWord.png");
        icons.put("pdf", "PDF.png");
        icons.put("rar", "RAR.png");
        icons.put("rtf", "RTF.png");
        icons.put("tar", "TAR.png");
        icons.put("txt", "TextPlain.png");
        icons.put("mov", "Video.png");
        icons.put("avi", "Video.png");
        icons.put("mpg", "Video.png");
        icons.put("mpeg", "Video.png");
        icons.put("pps", "PowerPoint.png");
        icons.put("ppt", "PowerPoint.png");
        icons.put("wma", "Video.png");
        icons.put("wmv", "Video.png");
        icons.put("mp4", "Video.png");
        icons.put("rm", "Video.png");
        icons.put("wav", "WAF.png");
        icons.put("xls", "MSExcel.png");
        icons.put("zip", "ZIP.png");
        icons.put("gif", "Image.png");
        icons.put("jpg", "Image.png");
        icons.put("jpeg", "Image.png");
        icons.put("bmp", "Image.png");
        icons.put("png", "Image.png");
    }

    @Override
    public Object getValue(Field field, String inputName, Map parametersMap, Map filesMap, String desiredClassName, Object previousValue) throws Exception {
        Document oldDoc = (Document) previousValue;

        // Expecting a delete parameter, if we receive that the current file will be deleted from the system
        String[] deleteParam = (String[]) parametersMap.get(inputName + "_delete");
        boolean delete = oldDoc != null && (deleteParam != null && deleteParam.length > 0 && deleteParam[0] != null && Boolean.valueOf(deleteParam[0]).booleanValue());

        // if there is an uploaded file for that field we will delete the previous one (if existed) and will return the uploaded file path.
        File file = (File) filesMap.get(inputName);
        if (file != null) {
            // Getting the DocumentMarshallingStrategy for this project if exists any we build the document
            AbstractDocumentMarshallingStrategy marshallingStrategy = getDocumentMarshallingStrategy( inputName );
            if (marshallingStrategy != null) {
                StringBuffer url = RequestContext.getCurrentContext().getRequest().getRequestObject().getRequestURL();

                Map<String, String> params = new HashMap<String, String>(  );
                params.put( "app.url", url.substring(0, url.indexOf("/Controller")) );

                Document doc = marshallingStrategy.buildDocument( file.getName(), file.length(), new Date(file.lastModified()), params );
                doc.setContent( FileUtils.readFileToByteArray( file ) );
                return doc;
            } else {
                Document doc = new DocumentImpl( file.getName(), file.length(), new Date(file.lastModified()) );
                doc.setContent( FileUtils.readFileToByteArray( file ) );
                return doc;
            }
        }

        // If we receive the delete parameter or we are uploading a new file the current file will be deleted
        if (delete) {
            return null;
        }

        return oldDoc;
    }

    protected AbstractDocumentMarshallingStrategy getDocumentMarshallingStrategy( String inputName ) {
        FormRenderContext context = formRenderContextManager.getRootContext( inputName );
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(context.getDeploymentId());
        InternalRuntimeManager manager = (InternalRuntimeManager) deployedUnit.getRuntimeManager();
        ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) manager.getEnvironment().getEnvironment().get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES );
        if (strategies != null) {
            for (ObjectMarshallingStrategy strategy : strategies) {
                if (strategy instanceof AbstractDocumentMarshallingStrategy) return (AbstractDocumentMarshallingStrategy) strategy;
            }
        }
        return null;
    }

    @Override
    public String getShowHTML(Object value, Field field, String inputName, String namespace) {
        return renderField((Document) value, field, inputName, false, false);
    }

    @Override
    public String getInputHTML(Object value, Field field, String inputName, String namespace, Boolean readonly) {
        return renderField((Document) value, field, inputName, true, readonly || field.getReadonly());
    }

    public String renderField(Document document, Field field, String inputName, boolean showInput, boolean readonly) {
        String str = null;
        try {
            String contextPath = ControllerStatus.lookup().getRequest().getRequestObject().getContextPath();

            Map<String, Object> context = new HashMap<String, Object>();

            // if there is a file in the specified id, the input will show a link to download it.
            context.put("inputId", inputName);
            if (document != null) {

                if (StringUtils.isEmpty(document.getIdentifier())) {
                    context.put("showLink", Boolean.FALSE);
                } else {
                    context.put("showLink", Boolean.TRUE);
                    context.put("downloadLink", document.getLink());
                }

                context.put("showDownload", Boolean.TRUE);
                context.put("fileName", StringEscapeUtils.escapeHtml4(document.getName()));
                context.put("fileSize", getFileSize(document.getSize()));
                context.put("fileIcon", contextPath + getFileIcon(document));
                context.put("dropIcon", contextPath + dropIcon);
            } else {
                context.put("showDownload", Boolean.FALSE);
            }
            context.put("readonly", readonly);
            context.put("showInput", showInput);

            InputStream src = this.getClass().getResourceAsStream("input.ftl");
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            BeansWrapper defaultInstance = new BeansWrapper();
            defaultInstance.setSimpleMapWrapper(true);
            cfg.setObjectWrapper(defaultInstance);
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(inputName, new InputStreamReader(src), cfg);
            StringWriter out = new StringWriter();
            temp.process(context, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            log.warn("Failed to process template for field '{}': {}", field.getFieldName(), e);
        }
        return str;
    }

    protected String getFileSize(long longSize) {
        double size = longSize;
        int position;
        for (position = 0; position < SIZE_UNITS.length && size > 1024; position++) {
            size = size / 1024;
        }
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(size) + " " + SIZE_UNITS[position];
    }

    protected String getFileIcon(Document document) {
        if (document != null) {
            int index = document.getName().lastIndexOf(".");
            if (index != -1) {
                String extension = document.getName().substring(index + 1).toLowerCase();
                if (icons.get(extension) != null) {
                    return iconFolder + icons.get(extension);
                }
            }
            return iconFolder + defaultFileIcon;
        }
        return null;
    }

    @Override
    public String[] getCompatibleClassNames() {
        return new String[]{Document.class.getName()};
    }

    @Override
    public boolean isEmpty(Object value) {
        return value == null;
    }

    @Override
    public Map getParamValue(Field field, String inputName, Object objectValue) {
        return Collections.EMPTY_MAP;
    }
}
