package org.jbpm.formModeler.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.cdi.Kjar;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

/**
 * This class should contain all ApplicationScoped producers
 * required by the application.
 */
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class ApplicationScopedProvider {

    @Inject
    private IOWatchServiceNonDotImpl watchService;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private IOService ioService;

    @Inject
    @Named("luceneConfig")
    private LuceneConfig config;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    @Kjar
    private org.jbpm.services.api.DeploymentService deploymentService;

    @PostConstruct
    public void setup() {
        final IOService service = new IOServiceIndexedImpl( watchService,
                                                            config.getIndexEngine(),
                                                            DublinCoreView.class,
                                                            VersionAttributeView.class,
                                                            OtherMetaView.class );

        if ( clusterServiceFactory == null ) {
            ioService = service;
        } else {
            ioService = new IOServiceClusterImpl( service,
                                                  clusterServiceFactory,
                                                  false );
        }
    }

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if ( this.emf == null ) {
            // this needs to be here for non EE containers
            try {
                this.emf = InitialContext.doLookup( "jBPMEMF" );
            } catch ( NamingException e ) {
                this.emf = Persistence.createEntityManagerFactory( "org.jbpm.domain" );
            }

        }
        return this.emf;
    }

    @Produces
    public DeploymentService produceKjarDeployService() {
        return deploymentService;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }

    @Produces
    public AuthorizationManager getAuthManager() {
        return new RuntimeAuthorizationManager();
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }
}
