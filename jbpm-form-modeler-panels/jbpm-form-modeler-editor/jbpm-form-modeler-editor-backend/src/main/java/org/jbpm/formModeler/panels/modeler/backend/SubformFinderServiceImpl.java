package org.jbpm.formModeler.panels.modeler.backend;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SubformFinderServiceImpl implements SubformFinderService {
    @Inject
    private Log log;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormEditorContextManager formEditorContextManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    @Override
    public Form getFormFromPath(String formPath, String ctxUID) {
        try {
            FormEditorContext editorContext = formEditorContextManager.getFormEditorContext(ctxUID);
            if (editorContext != null) return getForm(editorContext);

            FormRenderContext renderContext = formRenderContextManager.getFormRenderContext(ctxUID);

            ContentMarshallerContext contextMarshaller = (ContentMarshallerContext) renderContext.getMarshaller();
            ClassLoader classLoader = contextMarshaller.getClassloader();
            return formSerializationManager.loadFormFromXML(classLoader.getResourceAsStream(formPath));
        } catch (Exception e) {
            log.warn("Error getting form '" + formPath + "' from context '" + ctxUID + "': ", e);
        }
        return null;
    }

    protected Form getForm(FormEditorContext editorContext) {
        Path currentForm = (Path) editorContext.getPath();

        return null;
    }
}
