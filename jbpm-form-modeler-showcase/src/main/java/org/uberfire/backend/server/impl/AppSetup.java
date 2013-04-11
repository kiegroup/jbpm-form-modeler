/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.backend.server.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.repositories.DefaultSystemRepository;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

import static org.kie.commons.io.FileSystemType.Bootstrap.*;

@ApplicationScoped
public class AppSetup {
    //private static final String REPO_PLAYGROUND = "jbpm-playground";
    //private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    private static final String REPO_PLAYGROUND = "uf-playground";
    private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";


    //@Inject
    //private IOService ioService;
    private final IOService ioService         = new IOServiceDotFileImpl();

    @Inject
    private RepositoryService repositoryService;



    private FileSystem fs = null;

    @PostConstruct
    public void onStartup() {

        //configurationService.addConfiguration()

        Repository repository =null;
                try{
                    repository = repositoryService.getRepository(REPO_PLAYGROUND);
                }catch (Exception e){
                 System.out.println("error recuperando repo "+ e);
                }
        if(repository == null) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            repositoryService.cloneRepository("git", REPO_PLAYGROUND, ORIGIN_URL, userName, password);
            repository = repositoryService.getRepository(REPO_PLAYGROUND);
        }
        try {
            fs = ioService.newFileSystem(URI.create(repository.getUri()), repository.getEnvironment());
        } catch (FileSystemAlreadyExistsException e) {
            fs = ioService.getFileSystem(URI.create(repository.getUri()));

        }


/*        final String gitURL = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
        final String userName = "guvnorngtestuser1";
        final String password = "test1234";
        final URI fsURI = URI.create( "git://uf-playground" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( "username", userName );
            put( "password", password );
            put( "origin", gitURL );
        }};

        FileSystem fs = null;

        try {
            fs = ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        } catch ( FileSystemAlreadyExistsException ex ) {
            fs = ioService.getFileSystem( fsURI );
        }
 */
  //      activeFileSystems.addBootstrapFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
  //          put( "default://uf-playground", "uf-playground" );
  //      }}, fs.supportedFileAttributeViews() ) );
    }

    @Produces
    @Named("fileSystem")
    public FileSystem fileSystem() {
        return fs;
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

}
