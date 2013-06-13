package org.uberfire.backend.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.commons.services.cdi.Startup;
import org.kie.commons.services.cdi.StartupType;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.security.server.cdi.SecurityFactory;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup {

    private static final String PLAYGROUND_SCHEME = "git";
    private static final String PLAYGROUND_ALIAS = "uf-playground";
    private static final String PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";
    private static final String PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String PLAYGROUND_PWD = "test1234";

    @Inject
    private RepositoryService repositoryService;


    @Inject
    private GroupService groupService;

    @PostConstruct
    public void assertPlayground() {
        final Repository repository = repositoryService.getRepository( PLAYGROUND_ALIAS );
        if ( repository == null ) {
            repositoryService.createRepository( PLAYGROUND_SCHEME, PLAYGROUND_ALIAS,
                                                new HashMap<String, Object>() {{
                                                    put( "origin", PLAYGROUND_ORIGIN );
                                                    put( "username", PLAYGROUND_UID );
                                                    put( "crypt:password", PLAYGROUND_PWD );
                                                }} );
        }
        // TODO in case groups are not defined
        Collection<Group> groups = groupService.getGroups();
        if ( groups == null || groups.isEmpty() ) {
            List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repository );
            groupService.createGroup( "demo",
                    "demo@jbpm.org",
                    repositories );
        }

        SecurityFactory.setAuthzManager(new RuntimeAuthorizationManager());
    }

}
