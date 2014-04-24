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
package org.jbpm.formModeler.fieldTypes.document.handling;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.core.processing.fieldHandlers.plugable.PlugableFieldHandler;
import org.jbpm.formModeler.fieldTypes.document.Document;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
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

    public final String SIZE_UNITS[] = new String[]{"bytes", "Kb", "Mb"};

    @Inject
    private FileStorageService fileStorageService;

    @Inject
    private URLMarkupGenerator urlMarkupGenerator;

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
            if (oldDoc != null) fileStorageService.deleteDocument(oldDoc);
            return fileStorageService.saveDocument(file);
        }

        // If we receive the delete parameter or we are uploading a new file the current file will be deleted
        if (delete) fileStorageService.deleteDocument(oldDoc);

        return previousValue;
    }

    @Override
    public String getShowHTML(Object value, String fieldName, String namespace, Field field) {
        return renderField(fieldName, (String) value, namespace, false);
    }

    @Override
    public String getInputHTML(Object value, String fieldName, String namespace, Field field) {
        return renderField(fieldName, (String) value, namespace, !field.getReadonly());
    }

    public String renderField(String fieldName, String id, String namespace, boolean showInput) {
        /*
         * We are using a .ftl template to generate the HTML to show on screen, as it is a sample you can use any other way to do that.
         * To see the template format look at input.ftl on the resources folder.
         */
        String str = null;
        try {
            Document document = null;
            if (!StringUtils.isEmpty(id)) {
                document = fileStorageService.getDocument(id);
            }

            Map<String, Object> context = new HashMap<String, Object>();

            // if there is a file in the specified id, the input will show a link to download it.
            context.put("inputId", namespace + "_file_" + fieldName);
            if (document != null) {
                /*
                 * Building the parameter map for the download link.
                 * We are encoding the file id in order to make a download link cleaner
                 */
                Map params = new HashMap();
                params.put("content", Base64.encodeBase64String(id.getBytes()));

                /*
                 * Building the download link:
                 * For this sample we created a FileDownloadHandler that will execute the download action. We used the @Named("fdch") annotation to identify it.
                 * To generate the link we are using the URLMarkupGenerator from jbpm-form-modeler-request-dispatcher module. It generates a markup to the FileDownloadHandler
                 * using the parameters:
                 * "fdch"       -> Identifier of the Bean that will execute the action (you can also use the Canonical Name of the Bean "org.jbpm.formModeler.core.fieldTypes.file.FileDownloadHandler")
                 * "download"   -> Action to execute
                 * params       -> A map containing the parameters that the action requires. In that case only the file id in Base64.
                 */
                String downloadLink = urlMarkupGenerator.getMarkup("fdch", "download", params);

                context.put("showLink", Boolean.TRUE);
                context.put("downloadLink", downloadLink);
                context.put("fileName", document.getName());
                context.put("fileSize", getFileSize(document.getSize()));
                context.put("fileIcon", getFileIcon(document));
                context.put("dropIcon", dropIcon);
            } else {
                context.put("showLink", Boolean.FALSE);
            }
            // If the field is readonly or we are just showing the field value we will hide the input file.
            context.put("showInput", showInput);

            InputStream src = this.getClass().getResourceAsStream("input.ftl");
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            BeansWrapper defaultInstance = new BeansWrapper();
            defaultInstance.setSimpleMapWrapper(true);
            cfg.setObjectWrapper(defaultInstance);
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(fieldName, new InputStreamReader(src), cfg);
            StringWriter out = new StringWriter();
            temp.process(context, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            log.warn("Failed to process template for field '{}': {}", fieldName, e);
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
    public Map getParamValue(String inputName, Object objectValue, String pattern) {
        return Collections.EMPTY_MAP;
    }
}
