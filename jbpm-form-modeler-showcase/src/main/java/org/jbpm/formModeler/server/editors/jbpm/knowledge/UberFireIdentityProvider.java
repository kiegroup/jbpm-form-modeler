package org.jbpm.formModeler.server.editors.jbpm.knowledge;

import org.jbpm.kie.services.api.IdentityProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@SessionScoped
public class UberFireIdentityProvider implements IdentityProvider, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Identity identity;

    @Inject
    private HttpServletRequest request;

    @Override
    public String getName() {
        try {
            return identity.getName();
        } catch (Exception e) {
            if (request != null && request.getUserPrincipal() != null) {
                return request.getUserPrincipal().getName();
            }
            return null;
        }
    }

    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<String>();

        List<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }

        return roles;
    }

    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    public void aggregateProperty(String name,
                                  String value) {
    }

    public void removeProperty(String name) {
    }

    public String getProperty(String name,
                              String defaultValue) {
        return null;
    }

}
