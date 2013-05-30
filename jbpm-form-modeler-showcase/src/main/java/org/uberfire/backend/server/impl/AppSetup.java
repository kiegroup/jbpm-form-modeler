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
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;


import org.kie.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class AppSetup {

    private static final String REPO_PLAYGROUND = "jbpm-playground";
    private static final String REPO_URL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private static final String REPO_USERNAME = "guvnorngtestuser1";
    private static final String REPO_PASS = "test1234";

    private final IOService ioService = new IOServiceDotFileImpl();

    @Inject
    private GroupService groupService;

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Inject
    private RepositoryService repositoryService;

    @PostConstruct
    public void onStartup() {

        Repository repository = repositoryService.getRepository(REPO_PLAYGROUND);
        if(repository == null) {

            final Map<String, Object> env = new HashMap<String, Object>( 3 );
            env.put( "origin", REPO_URL );
            env.put( "username", REPO_USERNAME);
            env.put( "crypt:REPO_PASS", REPO_PASS);

            repositoryService.createRepository( "git", REPO_PLAYGROUND, env );
            repository = repositoryService.getRepository( REPO_PLAYGROUND );
        }

        Collection<Group> groups = groupService.getGroups();
        if ( groups == null || groups.isEmpty() ) {
            List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repository );
            groupService.createGroup( "demo", "demo@jbpm.org", repositories );
        }

        try {
            ioService.newFileSystem(URI.create(repository.getUri()), repository.getEnvironment());

        } catch (FileSystemAlreadyExistsException e) {
            ioService.getFileSystem(URI.create(repository.getUri()));

        }
    }

}
