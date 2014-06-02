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

import org.jbpm.formModeler.api.model.Form;
import org.jbpm.formModeler.core.config.FormSerializationManager;
import org.jbpm.formModeler.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
public class FormIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger(FormIndexer.class);

    @Inject
    protected FormResourceTypeDefinition formType;

    @Inject
    protected FormSerializationManager formSerializationManager;

    @Inject
    @Named("ioStrategy")
    protected IOService ioService;

    @Override
    public boolean supportsPath(Path path) {
        return formType.accept(Paths.convert(path));
    }

    @Override
    public KObject toKObject(Path path) {
        KObject index = null;

        try {
            Form form = formSerializationManager.loadFormFromXML(ioService.readAllString(path).trim(), path.toUri().toString());

            DefaultIndexBuilder builder = new DefaultIndexBuilder();

            FormVisitor formVisitor = new FormVisitor(builder, form);

            formVisitor.visit();

            index = KObjectUtil.toKObject(path, builder.build());
        } catch ( Exception e ) {
            logger.error( "Unable to index '" + path.toUri().toString() + "'.", e.getMessage() );
        }

        return index;
    }

    @Override
    public KObjectKey toKObjectKey(Path path) {
        return KObjectUtil.toKObjectKey(path);
    }
}
