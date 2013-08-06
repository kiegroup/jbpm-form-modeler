package org.jbpm.formModeler.panels.modeler.backend;

import org.apache.commons.logging.Log;
import org.drools.core.util.StringUtils;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.kie.commons.io.IOService;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.*;

@ApplicationScoped
public class SubformFinderServiceImpl implements SubformFinderService {
    @Inject
    private Log log;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private Paths paths;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormEditorContextManager formEditorContextManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Override
    public Form getFormById(long formId, String ctxUID) {

        try {
            FormEditorContext editorContext = formEditorContextManager.getRootEditorContext(ctxUID);
            if (editorContext != null) return getForm(formId, editorContext);

            // find root context in order to load the subform
            FormRenderContext renderContext = formRenderContextManager.getRootContext(ctxUID);
            if (renderContext != null) {
                if (renderContext.getForm().getId().equals(new Long(formId))) return renderContext.getForm();
                // at the moment forms aren't available on marshaller classloader
                /*
                ContentMarshallerContext contextMarshaller = (ContentMarshallerContext) renderContext.getMarshaller();
                ClassLoader classLoader = contextMarshaller.getClassloader();
                return formSerializationManager.loadFormFromXML(classLoader.getResourceAsStream(formPath));
                 */
                Map forms = renderContext.getContextForms();
                String header = formSerializationManager.generateHeaderFormFormId(formId);
                for (Iterator it = forms.keySet().iterator(); it.hasNext();) {
                    String key = (String) it.next();
                    Object form = forms.get(key);
                    if (form instanceof Form) {
                        if (((Form) form).getId().equals(formId)) return (Form) form;
                    }
                    else if (form instanceof String && form.toString().trim().startsWith(header)) {
                        Form result = formSerializationManager.loadFormFromXML((String) form);
                        renderContext.getContextForms().put(key, result);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error getting form '" + formId + "' from context '" + ctxUID + "': ", e);
        }
        return null;
    }

    @Override
    public Form getFormFromPath(String formPath, String ctxUID) {
        try {
            if (StringUtils.isEmpty(formPath)) return null;

            FormEditorContext editorContext = formEditorContextManager.getRootEditorContext(ctxUID);
            if (editorContext != null) return getForm(formPath, editorContext);

            // find root context in order to load the subform
            FormRenderContext renderContext = formRenderContextManager.getRootContext(ctxUID);

            if (renderContext != null) {
                // at the moment forms aren't available on marshaller classloader
                /*
                ContentMarshallerContext contextMarshaller = (ContentMarshallerContext) renderContext.getMarshaller();
                ClassLoader classLoader = contextMarshaller.getClassloader();
                return formSerializationManager.loadFormFromXML(classLoader.getResourceAsStream(formPath));
                 */
                Object form = renderContext.getContextForms().get(formPath);
                if (form != null) {
                    if (form instanceof Form) return (Form) form;
                    else if (form instanceof String) {
                        Form result = formSerializationManager.loadFormFromXML((String) form);
                        renderContext.getContextForms().put(formPath, result);
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error getting form '" + formPath + "' from context '" + ctxUID + "': ", e);
        }
        return null;
    }

    protected Form getForm(long formId, FormEditorContext editorContext) throws Exception {
        if (editorContext.getForm().getId().equals(new Long(formId))) return editorContext.getForm();

        Path currentForm = paths.convert(ioService.get(new URI(editorContext.getPath())));

        Project project = projectService.resolveProject(currentForm);

        FileUtils utils  = FileUtils.getInstance();

        List<org.kie.commons.java.nio.file.Path> nioPaths = new ArrayList<org.kie.commons.java.nio.file.Path>();
        nioPaths.add(paths.convert(project.getRootPath()));

        Collection<FileUtils.ScanResult> forms = utils.scan(ioService, nioPaths, "form", true);

        String header = formSerializationManager.generateHeaderFormFormId(formId);

        for (FileUtils.ScanResult form : forms) {
            org.kie.commons.java.nio.file.Path formPath = form.getFile();
            org.kie.commons.java.nio.file.Path path = paths.convert(project.getRootPath()).resolve(MAIN_RESOURCES_PATH).resolve(formPath);

            String xml = ioService.readAllString(path).trim();

            if (xml.startsWith(header)) return formSerializationManager.loadFormFromXML(xml);
        }
        return null;
    }

    protected Form getForm(String formPath, FormEditorContext editorContext) throws Exception{

        Path currentForm = paths.convert(ioService.get(new URI(editorContext.getPath())));

        org.kie.commons.java.nio.file.Path subFormPath = paths.convert(currentForm).getParent().resolve(formPath);

        String xml = ioService.readAllString(subFormPath).trim();

        return formSerializationManager.loadFormFromXML(xml,subFormPath.toUri().toString());
    }
}