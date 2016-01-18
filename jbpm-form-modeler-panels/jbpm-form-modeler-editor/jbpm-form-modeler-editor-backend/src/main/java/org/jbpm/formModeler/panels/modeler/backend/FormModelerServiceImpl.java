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
package org.jbpm.formModeler.panels.modeler.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.rendering.SubformFinderService;
import org.jbpm.formModeler.editor.model.FormEditorContextTO;
import org.jbpm.formModeler.editor.model.FormModelerContent;
import org.jbpm.formModeler.editor.service.FormModelerService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class FormModelerServiceImpl extends KieService<FormModelerContent> implements FormModelerService {

    private Logger log = LoggerFactory.getLogger( FormModelerServiceImpl.class );

    @Inject
    @Named( "ioStrategy" )
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
    private User identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private CommentedOptionFactory commentedOptionFactory;

    @Override
    public void changeContextPath( String ctxUID, Path path ) {
        if ( StringUtils.isEmpty( ctxUID ) ) return;
        formEditorContextManager.getFormEditorContext( ctxUID ).setPath( Paths.convert( path ).toUri().toString() );
    }

    @Override
    public void removeEditingForm( String ctxUID ) {
        formEditorContextManager.removeEditingForm( ctxUID );
    }

    @Override
    public FormEditorContextTO reloadContent( Path path, String ctxUID ) {
        try {
            Form form = findForm( Paths.convert( path ) );

            FormEditorContext context = formEditorContextManager.getFormEditorContext( ctxUID );

            context.setForm( form );

            return new FormEditorContextTO( context.getUID() );
        } catch ( Exception e ) {
            log.warn( "Error loading form " + path.toURI(), e );
            return null;
        }
    }


    @Override
    public Path save( Path path, FormEditorContextTO content, Metadata metadata, String comment ) {
        FormEditorContext ctx = formEditorContextManager.getFormEditorContext( content.getCtxUID() );
        ioService.write( Paths.convert( path ), formSerializationManager.generateFormXML( ctx.getForm() ), metadataService.setUpAttributes( path, metadata ), commentedOptionFactory.makeCommentedOption( comment ) );
        return path;
    }

    @Override
    public Path rename( Path path, String newName, String comment ) {
        return renameService.rename( path, newName, comment );
    }

    @Override
    public void delete( Path path, String comment ) {
        deleteService.delete( path, comment );
    }

    @Override
    public Path createForm( Path path, String formName ) {
        org.uberfire.java.nio.file.Path kiePath = Paths.convert( path ).resolve( formName );
        try {
            if ( ioService.exists( kiePath ) ) {
                throw new FileAlreadyExistsException( kiePath.toString() );
            }
            Form form = formManager.createForm( formName );

            ioService.write( kiePath, formSerializationManager.generateFormXML( form ), commentedOptionFactory.makeCommentedOption( "" ) );

            return Paths.convert( kiePath );
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public FormModelerContent loadContent( Path path ) {
        return super.loadContent( path );
    }

    @Override
    protected FormModelerContent constructContent( Path path, Overview overview ) {

        try {
            org.uberfire.java.nio.file.Path kiePath = Paths.convert( path );

            Form form = findForm( kiePath );

            FormEditorContextTO contextTO = new FormEditorContextTO();

            if (form == null) {
                contextTO.setLoadError( true );
                form = formManager.createForm(path.getFileName());
            }

            String formPath = kiePath.toUri().toString();

            FormEditorContext context = formEditorContextManager.newContext(form, formPath);

            contextTO.setCtxUID( context.getUID() );

            FormModelerContent result = new FormModelerContent();
            result.setContextTO( contextTO );
            result.setPath( path );
            result.setOverview( overview );

            resourceOpenedEvent.fire(new ResourceOpenedEvent( path, sessionInfo ));

            return result;
        } catch (Exception e) {
            log.warn("Error loading form " + path.toURI(), e);
        }
        return null;
    }

    protected Form findForm( org.uberfire.java.nio.file.Path path ) throws Exception {
        String xml = ioService.readAllString( path ).trim();

        return formSerializationManager.loadFormFromXML( xml, path.toUri().toString() );
    }
}
