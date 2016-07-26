package org.jbpm.formModeler.service.bb.mvc.components.handling;

import javax.servlet.http.HttpServletRequest;

import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.components.handling.mocks.MyBaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BaseUIComponentTest extends AbstractBeanHandlerTest<BaseUIComponent> {

    protected LocaleManager localeManager;

    @Override
    public BaseUIComponent getHandlerComponent() {
        localeManager = mock( LocaleManager.class );

        return new MyBaseUIComponent( localeManager, renderer );
    }

    @Test
    public void testStartComponent() throws Exception {

        initTest( true );

        CommandResponse response = handlerComponent.handle( request, "Start" );

        verify( request, atLeastOnce() ).getRequestObject();

        verify( httpServletRequest ).getParameter( "ajaxAction" );

        verify( localeManager, atLeastOnce() ).setCurrentLang( anyString() );

        assertNull( "Response must be null", response );

        assertFalse( "Shouldn't be first time", handlerComponent.isFirstTime() );
    }
}
