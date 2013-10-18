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
package org.jbpm.formModeler.core.fieldTypes.file;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.fieldTypes.CustomFieldType;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Sample implementation of a File input that can be used in forms to upload files on a storage system and save the File path inside a String property.
 *
 * This Custom Type must be used only on String properties
 *
 * This is just a Sample, use it only with test purposes.
 */
public class FileCustomType implements CustomFieldType {

    private Logger log = LoggerFactory.getLogger(FileCustomType.class);

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
    public String getDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.fieldTypes.file.messages", locale);
        return bundle.getString("description");
    }

    @Override
    public Object getValue(Map requestParameters, Map requestFiles, String fieldName, String namespace, Object previousValue, boolean required, boolean readonly, String... params) {
        String id = namespace + "_file_" + fieldName;

        String oldPath = (String) previousValue;

        // Expecting a delete parameter, if we receive that the current file will be deleted from the system
        String[] deleteParam = (String[]) requestParameters.get(id + "_delete");
        boolean delete = !StringUtils.isEmpty(oldPath) && (deleteParam != null && deleteParam.length > 0 && deleteParam[0] != null && Boolean.valueOf(deleteParam[0]).booleanValue());

        // if there is an uploaded file for that field we will delete the previous one (if existed) and will return the uploaded file path.
        File file = (File) requestFiles.get(id);
        if (file != null) {
            if (!StringUtils.isEmpty(oldPath)) fileStorageService.deleteFile(oldPath);
            return fileStorageService.saveFile(fieldName, file);
        }

        // If we receive the delete parameter or we are uploading a new file the current file will be deleted
        if (delete) fileStorageService.deleteFile(oldPath);

        return previousValue;
    }

    @Override
    public String getShowHTML(Object value, String fieldName, String namespace, boolean required, boolean readonly, String... params) {
        return renderField(fieldName, (String) value, namespace, false);
    }

    @Override
    public String getInputHTML(Object value, String fieldName, String namespace, boolean required, boolean readonly, String... params) {
        return renderField(fieldName, (String) value, namespace, true && !readonly);
    }

    public String renderField(String fieldName, String path, String namespace, boolean showInput) {
        /*
         * We are using a .ftl template to generate the HTML to show on screen, as it is a sample you can use any other way to do that.
         * To see the template format look at input.ftl on the resources folder.
         */
        String str = null;
        try {
            File file = null;
            if (!StringUtils.isEmpty(path)) {
                file = fileStorageService.getFile(path);
            }

            Map<String, Object> context = new HashMap<String, Object>();

            // if there is a file in the specified path, the input will show a link to download it.
            context.put("inputId", namespace + "_file_" + fieldName);
            if (file != null && file.exists()) {
                /*
                 * Building the parameter map for the download link.
                 * We are encoding the file path in order to make a download link cleaner
                 */
                Map params = new HashMap();
                params.put("content", Base64.encodeBase64String(path.getBytes()));

                /*
                 * Building the download link:
                 * For this sample we created a FileDownloadHandler that will execute the download action. We used the @Named("fdch") annotation to identify it.
                 * To generate the link we are using the URLMarkupGenerator from jbpm-form-modeler-request-dispatcher module. It generates a markup to the FileDownloadHandler
                 * using the parameters:
                 * "fdch"       -> Identifier of the Bean that will execute the action (you can also use the Canonical Name of the Bean "org.jbpm.formModeler.core.fieldTypes.file.FileDownloadHandler")
                 * "download"   -> Action to execute
                 * params       -> A map containing the parameters that the action requires. In that case only the file path in Base64.
                 */
                String downloadLink = urlMarkupGenerator.getMarkup("fdch", "download", params);

                context.put("showLink", Boolean.TRUE);
                context.put("downloadLink", downloadLink);
                context.put("fileName", file.getName());
                context.put("fileSize", getFileSize(file.length()));
                context.put("fileIcon", getFileIcon(file));
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
            log.warn("Failed to process template for field '{0}': {1}", fieldName, e);
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

    protected String getFileIcon(File file) {
        if (file != null) {
            int index = file.getName().lastIndexOf(".");
            if (index != -1) {
                String extension = file.getName().substring(index + 1).toLowerCase();
                if (icons.get(extension) != null) {
                    return iconFolder + icons.get(extension);
                }
            }
            return iconFolder + defaultFileIcon;
        }
        return null;
    }
}
