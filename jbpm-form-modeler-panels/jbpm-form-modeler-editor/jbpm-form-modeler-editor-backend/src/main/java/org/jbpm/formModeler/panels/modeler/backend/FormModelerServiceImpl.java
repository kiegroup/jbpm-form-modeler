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

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.editor.model.FormEditorContextTO;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class FormModelerServiceImpl implements FormModelerService {

    private Logger log = LoggerFactory.getLogger(FormModelerServiceImpl.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private RenameService renameService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private SubformFinderService subformFinderService;

    @Inject
    private FormManager formManager;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormEditorContextManager formEditorContextManager;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Override
    public void changeContextPath(String ctxUID, Path path) {
        if (StringUtils.isEmpty(ctxUID)) return;
        formEditorContextManager.getFormEditorContext(ctxUID).setPath(Paths.convert(path).toUri().toString());
    }

    @Override
    public void removeEditingForm(String ctxUID) {
        formEditorContextManager.removeEditingForm(ctxUID);
    }

    @Override
    public FormEditorContextTO loadForm(Path path) {
        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(path);

            String formPath = kiePath.toUri().toString();

            Form form = subformFinderService.getFormByPath(formPath);

            FormEditorContextTO result = new FormEditorContextTO();

            if (form == null) {
                result.setLoadError(true);
                form = formManager.createForm(path.getFileName());
            }

            FormEditorContext context = formEditorContextManager.newContext(form, formPath);

            result.setCtxUID(context.getUID());

            resourceOpenedEvent.fire(new ResourceOpenedEvent( path, sessionInfo ));

            return result;
        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(), e);
            return null;
        }
    }

    @Override
    public FormEditorContextTO reloadForm(Path path, String ctxUID) {
        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert(path);

            Form form = subformFinderService.getFormByPath(kiePath.toUri().toString());

            FormEditorContext context = formEditorContextManager.getFormEditorContext(ctxUID);

            context.setForm(form);

            return new FormEditorContextTO(context.getUID());
        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(), e);
            return null;
        }
    }


    @Override
    public Path save(Path path, FormEditorContextTO content, Metadata metadata, String comment) {
        FormEditorContext ctx = formEditorContextManager.getFormEditorContext(content.getCtxUID());
        ioService.write(Paths.convert(path), formSerializationManager.generateFormXML(ctx.getForm()), metadataService.setUpAttributes(path, metadata), makeCommentedOption(comment));
        return path;
    }

    @Override
    public Path rename(Path path, String newName, String comment) {
        return renameService.rename(path, newName, comment);
    }

    @Override
    public void delete(Path path, String comment) {
        deleteService.delete(path, comment);
    }

    @Override
    public Path createForm(Path path, String formName) {
        org.uberfire.java.nio.file.Path kiePath = Paths.convert(path).resolve(formName);
        try {
            if (ioService.exists(kiePath)) {
                throw new FileAlreadyExistsException(kiePath.toString());
            }
            Form form = formManager.createForm(formName);

            ioService.write(kiePath, formSerializationManager.generateFormXML(form), makeCommentedOption(""));

            return Paths.convert(kiePath);
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( sessionInfo.getId(),
                name,
                null,
                commitMessage,
                when );
        return co;
    }

}
