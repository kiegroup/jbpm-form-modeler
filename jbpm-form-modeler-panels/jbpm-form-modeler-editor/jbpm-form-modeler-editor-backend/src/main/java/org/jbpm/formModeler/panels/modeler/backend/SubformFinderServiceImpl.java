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
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.kie.commons.io.IOService;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

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
    public Form getFormFromPath(String formPath, String ctxUID) {
        try {
            if (StringUtils.isEmpty(formPath)) return null;

            FormEditorContext editorContext = formEditorContextManager.getFormEditorContext(ctxUID);
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

    protected Form getForm(String formPath, FormEditorContext editorContext) throws Exception{
        Path currentForm = (Path) editorContext.getPath();

        Project project = projectService.resolveProject(currentForm);

        org.kie.commons.java.nio.file.Path path = paths.convert(project.getRootPath()).resolve(MAIN_RESOURCES_PATH).resolve(formPath);

        String xml = ioService.readAllString(path).trim();

        return formSerializationManager.loadFormFromXML(xml, paths.convert(path));
    }
}
