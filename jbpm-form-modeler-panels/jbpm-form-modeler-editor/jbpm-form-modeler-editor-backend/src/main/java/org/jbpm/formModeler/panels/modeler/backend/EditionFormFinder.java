/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.formModeler.panels.modeler.backend;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Project;
import org.jbpm.formModeler.api.client.FormEditorContext;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.core.rendering.FormFinder;
import org.kie.workbench.common.services.datamodeller.util.FileUtils;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class EditionFormFinder implements FormFinder {
    public static final String MAIN_RESOURCES_PATH = "src/main/resources";

    private Logger log = LoggerFactory.getLogger( EditionFormFinder.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private FormSerializationManager formSerializationManager;

    @Inject
    private FormEditorContextManager formEditorContextManager;

    @Override
    public Form getForm( String ctxUID ) {
        FormEditorContext context = formEditorContextManager.getRootEditorContext( ctxUID );
        if ( context != null ) return context.getForm();
        return null;
    }

    @Override
    public Form getFormByPath( String ctxUID, String formPath ) {
        FormEditorContext editorContext = formEditorContextManager.getRootEditorContext( ctxUID );
        if ( editorContext != null ) {
            try {
                Path currentForm = Paths.convert( ioService.get( new URI( editorContext.getPath() ) ) );

                org.uberfire.java.nio.file.Path path = Paths.convert( currentForm ).getParent().resolve( formPath );

                String xml = ioService.readAllString( path ).trim();

                return formSerializationManager.loadFormFromXML( xml, path.toUri().toString() );

            } catch ( Exception e ) {
                log.warn( "Error getting form {} from context {}: {}", formPath, ctxUID, e );
            }
        }
        return null;
    }

    @Override
    public Form getFormById( String ctxUID, long formId ) {
        FormEditorContext editorContext = formEditorContextManager.getRootEditorContext( ctxUID );
        if ( editorContext != null ) {
            try {
                if ( editorContext.getForm().getId().equals( new Long( formId ) ) ) {
                    return editorContext.getForm();
                }

                Path currentForm = Paths.convert( ioService.get( new URI( editorContext.getPath() ) ) );

                Project project = projectService.resolveProject( currentForm );

                FileUtils utils = FileUtils.getInstance();

                List<org.uberfire.java.nio.file.Path> nioPaths = new ArrayList<org.uberfire.java.nio.file.Path>();
                nioPaths.add( Paths.convert( project.getRootPath() ) );

                Collection<FileUtils.ScanResult> forms = utils.scan( ioService, nioPaths, "form", true );

                String header = formSerializationManager.generateHeaderFormFormId( formId );

                for ( FileUtils.ScanResult form : forms ) {
                    org.uberfire.java.nio.file.Path formPath = form.getFile();
                    org.uberfire.java.nio.file.Path path = Paths.convert( project.getRootPath() ).resolve( MAIN_RESOURCES_PATH ).resolve( formPath );

                    String xml = ioService.readAllString( path ).trim();

                    if ( xml.startsWith( header ) ) {
                        return formSerializationManager.loadFormFromXML( xml );
                    }
                }
            } catch ( Exception e ) {
                log.warn( "Error getting form {} from context {}: {}", formId, ctxUID, e );
            }

        }
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
