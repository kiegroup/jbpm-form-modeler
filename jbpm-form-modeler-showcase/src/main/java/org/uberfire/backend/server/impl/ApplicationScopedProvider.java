package org.uberfire.backend.server.impl;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.DefaultSystemRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 4/12/13
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */

@ApplicationScoped
public class ApplicationScopedProvider {
        private final DefaultSystemRepository systemRepository = new DefaultSystemRepository();
    @Produces
    @Named("system")
    public Repository systemRepository() {
        return systemRepository;
    }
}
