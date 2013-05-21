package org.jbpm.formModeler.panels.modeler.backend;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jbpm.formModeler.api.config.FormManager;
import org.jbpm.formModeler.api.config.FormSerializationManager;
import org.jbpm.formModeler.api.processing.FormEditorContextTO;
import org.jbpm.formModeler.api.processing.FormRenderContext;
import org.jbpm.formModeler.api.processing.FormRenderContextManager;
import org.jbpm.formModeler.api.processing.FormEditorContext;
import org.jbpm.formModeler.api.util.helpers.EditorHelper;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.kie.commons.io.IOService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@ApplicationScoped
public class FormModelerServiceImpl implements FormModelerService {

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    protected Map<String, FormEditorContext> formEditorContextMap = new HashMap<String, FormEditorContext>();

    private Menus menus;

    @Override
    public List<FormTO> getAllForms() {
        List<FormTO> result = new ArrayList<FormTO>();
        for (Form form : formManager.getAllForms()) {
            if (form.isVisibleStatus()) result.add(new FormTO(form.getId(), form.getName()));
        }
        return result;
    }

    @Override
    public Long setFormId(Long formId, String contextUri) {
        EditorHelper helper = getHelper(contextUri);

        if (helper != null) {
            helper.setOriginalForm(formId);
            helper.setFormToEdit(contextUri, formManager.getFormById(formId));
            return formId;
        }

        return null;
    }

    @Override
    public FormEditorContextTO setFormFocus(String ctxUID) {
        if (StringUtils.isEmpty(ctxUID)) return null;
        return getFormEditorContext(ctxUID).getFormEditorContextTO();
    }

    @Override
    public void removeEditingForm(String ctxUID) {
        formEditorContextMap.remove(ctxUID);

    }

    @Override
    public FormEditorContextTO loadForm(Path context) {
        try {
            org.kie.commons.java.nio.file.Path kiePath = paths.convert( context );

            String xml = ioService.readAllString(kiePath).trim();
            Form form = formSerializationManager.loadFormFromXML(xml, context);

            return newContext(form, context).getFormEditorContextTO();
        } catch (Exception e) {
            Logger.getLogger(FormModelerServiceImpl.class.getName()).log(Level.WARNING, null, e);
            return null;
        }
    }

    @Override
    public FormEditorContext newContext(Form form, Object path) {
        FormRenderContext ctx = formRenderContextManager.newContext(form, new HashMap<String, Object>());
        FormEditorContext formEditorContext = new FormEditorContext(ctx, path);
        formEditorContextMap.put(ctx.getUID(), formEditorContext);
        return formEditorContext;
    }

    @Override
    public FormEditorContext getFormEditorContext(String UID) {
        return formEditorContextMap.get(UID);
    }

    @Override
    public FormTO getCurrentForm(String contextUri) {
        EditorHelper helper = getHelper(contextUri);
        formManager.replaceForm(helper.getOriginalForm(), helper.getFormToEdit(contextUri));
        clearHelper();
        return new FormTO(helper.getFormToEdit(contextUri).getId(), helper.getFormToEdit(contextUri).getName());
    }

    private void clearHelper() {
        RpcContext.getHttpSession().removeAttribute("EditorHelper");
        RpcContext.getHttpSession().removeAttribute("contextURI");
    }

    protected EditorHelper getHelper(String contextUri) {
        EditorHelper helper = (EditorHelper) RpcContext.getHttpSession().getAttribute("EditorHelper");

        if (helper == null) helper = new EditorHelper();

        RpcContext.getHttpSession().setAttribute("EditorHelper", helper);
        RpcContext.getHttpSession().setAttribute("contextURI", contextUri);

        return helper;
    }

    @Override
    public void saveForm(String ctxUID) {

        FormEditorContext ctx = getFormEditorContext(ctxUID);
        formManager.replaceForm(ctx.getOriginalForm(), ctx.getForm());
        org.kie.commons.java.nio.file.Path kiePath = paths.convert((Path)ctx.getPath());
        ioService.write(kiePath, formSerializationManager.generateFormXML(ctx.getForm()));

    }

    @Override
    public Path createForm(Path context, String formName) {
        org.kie.commons.java.nio.file.Path kiePath = paths.convert(context ).resolve(formName);

        ioService.createFile(kiePath);

        Form form = formManager.createForm(formName);

        ioService.write(kiePath, formSerializationManager.generateFormXML(form));
        EditorHelper helper = getHelper(context.toURI());

        if( helper!=null){
            helper.setFormToEdit(context.toURI(), form);
            helper.setOriginalForm(form.getId());
            getHelper(context.toURI());
        }

        resourceAddedEvent.fire(new ResourceAddedEvent(context));

        final Path path = paths.convert(kiePath, false);

        return path;
    }
}
