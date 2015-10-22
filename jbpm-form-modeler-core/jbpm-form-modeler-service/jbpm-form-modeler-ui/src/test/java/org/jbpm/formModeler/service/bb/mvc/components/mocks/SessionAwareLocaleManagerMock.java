package org.jbpm.formModeler.service.bb.mvc.components.mocks;

import org.jbpm.formModeler.service.bb.mvc.components.SessionAwareLocaleManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import java.util.Locale;

@Specializes
@ApplicationScoped
public class SessionAwareLocaleManagerMock extends SessionAwareLocaleManager {

    @Override
    public Locale getCurrentLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public void setCurrentLocale(Locale currentLocale) {
    }
}
