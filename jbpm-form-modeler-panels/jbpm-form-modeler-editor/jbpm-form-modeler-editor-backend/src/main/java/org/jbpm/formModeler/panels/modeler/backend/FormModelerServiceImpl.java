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
package org.jbpm.formModeler.panels.modeler.backend;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.client.FormEditorContextTO;
import org.jbpm.formModeler.api.client.FormRenderContext;
import org.jbpm.formModeler.api.client.FormRenderContextManager;
import org.jbpm.formModeler.api.model.Field;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.processing.FormProcessor;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

@Service
@ApplicationScoped
public class FormModelerServiceImpl implements FormModelerService, FormEditorContextManager {
    public static final String EDIT_FIELD_LITERAL = "editingFormFieldId";

    private Logger log = LoggerFactory.getLogger(FormModelerServiceImpl.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private SubformFinderService subformFinderService;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormRenderContextManager formRenderContextManager;

    protected Map<String, FormEditorContext> formEditorContextMap = new HashMap<String, FormEditorContext>();

    @Override
    public void changeContextPath(String ctxUID, Path path) {
        if (StringUtils.isEmpty(ctxUID)) return;
        getFormEditorContext(ctxUID).setPath(Paths.convert(path).toUri().toString());
    }

    @Override
    public FormEditorContextTO setFormFocus(String ctxUID) {
        if (StringUtils.isEmpty(ctxUID)) return null;
        return getFormEditorContext(ctxUID).getFormEditorContextTO();
    }

    @Override
    public void removeEditingForm(String ctxUID) {
        formEditorContextMap.remove(ctxUID);
        formRenderContextManager.removeContext(ctxUID);
    }

    @Override
    public FormEditorContextTO loadForm(Path context) {
        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(context);

            Form form = subformFinderService.getFormByPath(kiePath.toUri().toString());

            return newContext(form, context).getFormEditorContextTO();
        } catch (Exception e) {
            log.warn("Error loading form " + context.toURI(), e);
            return null;
        }
    }

    @Override
    public FormEditorContextTO reloadForm(Path path, String ctxUID) {
        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(path);

            Form form = subformFinderService.getFormByPath(kiePath.toUri().toString());

            FormEditorContext context = getFormEditorContext(ctxUID);

            context.setForm(form);

            return context.getFormEditorContextTO();

        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(), e);
            return null;
        }
    }

    @Override
    public FormEditorContext newContext(Form form, Object path) {
        FormRenderContext ctx = formRenderContextManager.newContext(form, new HashMap<String, Object>());
        org.uberfire.java.nio.file.Path kpath = Paths.convert((Path) path);

        FormEditorContext formEditorContext = new FormEditorContext(ctx, kpath.toUri().toString());
        formEditorContextMap.put(ctx.getUID(), formEditorContext);
        return formEditorContext;
    }

    @Override
    public FormEditorContext getFormEditorContext(String UID) {
        return formEditorContextMap.get(UID);
    }

    @Override
    public String generateFieldEditionNamespace(String UID, Field field) {
        return UID + FormProcessor.NAMESPACE_SEPARATOR + EDIT_FIELD_LITERAL + FormProcessor.CUSTOM_NAMESPACE_SEPARATOR + field.getId();
    }

    @Override
    public FormEditorContext getRootEditorContext(String UID) {
        if (StringUtils.isEmpty(UID)) return null;
        int separatorIndex = UID.indexOf(FormProcessor.NAMESPACE_SEPARATOR);
        if (separatorIndex != -1) UID = UID.substring(0, separatorIndex);
        return formEditorContextMap.get(UID);
    }

    @Override
    public void saveForm(String ctxUID) throws Exception {
        saveContext(ctxUID);
    }

    @Override
    public void saveContext(String ctxUID) throws Exception {
        FormEditorContext ctx = getFormEditorContext(ctxUID);

        org.uberfire.java.nio.file.Path kiePath = ioService.get(new URI(ctx.getPath()));
        ioService.write(kiePath, formSerializationManager.generateFormXML(ctx.getForm()));
    }

    @Override
    public Path createForm(Path context, String formName) {
        org.uberfire.java.nio.file.Path kiePath = Paths.convert(context).resolve(formName);
        try {
            ioService.createFile(kiePath);

            Form form = formManager.createForm(formName);

            ioService.write(kiePath, formSerializationManager.generateFormXML(form));

            return Paths.convert(kiePath);
        } catch (FileAlreadyExistsException e) {
            throw new IllegalArgumentException( kiePath.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(kiePath.toString());
        }

    }

    @Override
    public boolean deleteForm(Path context) {
        if (context == null) return false;
        org.uberfire.java.nio.file.Path kiePath = Paths.convert(context);
        return ioService.deleteIfExists(kiePath);
    }


}
