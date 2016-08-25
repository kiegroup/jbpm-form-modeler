/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.jbpm.formModeler.panels.modeler.backend.indexing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class FormIndexer extends AbstractFileIndexer {

    @Inject
    protected FormSerializationManager formSerializationManager;

    @Inject
    protected FormResourceTypeDefinition formType;

    @Override
    public boolean supportsPath( final Path path ) {
        return formType.accept( Paths.convert( path ) );
    }

    @Override
    public DefaultIndexBuilder fillIndexBuilder( final Path path ) throws Exception {
        Form form = formSerializationManager.loadFormFromXML( ioService.readAllString( path ).trim(),
                                                              path.toUri().toString() );

        final DefaultIndexBuilder builder = getIndexBuilder(path);
        if( builder == null ) {
            return null;
        }

        FormIndexVisitor formIndexVisitor = new FormIndexVisitor(form);
        formIndexVisitor.visit();
        addReferencedResourcesToIndexBuilder(builder, formIndexVisitor);

        return builder;
    }

}
