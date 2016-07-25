package org.jbpm.formModeler.service.bb.mvc.components.handling;

import javax.servlet.http.HttpServletRequest;

import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.SendStreamResponse;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractBeanHandlerTest<T extends BeanHandler> {

    protected CommandRequest request;
    protected HttpServletRequest httpServletRequest;

    protected CurrentComponentRenderer renderer;

    protected BaseUIComponent component;

    protected T handlerComponent;

    public abstract T getHandlerComponent();

    protected void initTest( final boolean isAjax ) {

        renderer  = mock( CurrentComponentRenderer.class );

        component = mock( BaseUIComponent.class );

        when( component.getBaseComponentJSP() ).thenReturn( "/test/myComponent.jsp" );

        when( renderer.getCurrentComponent() ).thenReturn( component );

        handlerComponent = getHandlerComponent();

        request = mock( CommandRequest.class );

        httpServletRequest = mock( HttpServletRequest.class );

        when( request.getRequestObject() ).thenReturn( httpServletRequest );

        when( httpServletRequest.getParameter( "ajaxAction" ) ).thenReturn( isAjax ? "true" : "false" );
    }

    @Test
    public void testDefaultAjaxActionWithResponse() throws Exception {

        initTest( true );

        CommandResponse response = handlerComponent.handle( request, "withResponse" );

        verify( request, atLeastOnce() ).getRequestObject();

        verify( httpServletRequest ).getParameter( "ajaxAction" );

        assertNotNull( "Response cannot be null", response );
    }

    @Test
    public void testDefaultNonAjaxActionWithResponse() throws Exception {

        initTest( false );

        CommandResponse response = handlerComponent.handle( request, "withResponse" );

        verify( request, atLeastOnce() ).getRequestObject();

        verify( httpServletRequest ).getParameter( "ajaxAction" );

        assertNull( "Response must be null", response );
    }

    @Test
    public void testDefaultAjaxActionWithoutResponse() throws Exception {

        initTest( true );

        CommandResponse response = handlerComponent.handle( request, "withoutResponse" );

        verify( request, atLeastOnce() ).getRequestObject();

        verify( httpServletRequest ).getParameter( "ajaxAction" );

        assertNotNull( "Response cannot be null", response );
    }

    @Test
    public void testDefaultNonAjaxActionWithoutResponse() throws Exception {

        initTest( false );

        CommandResponse response = handlerComponent.handle( request, "withoutResponse" );

        verify( request, atLeastOnce() ).getRequestObject();

        verify( httpServletRequest ).getParameter( "ajaxAction" );

        assertNull( "Response be null", response );
    }

    @Test
    public void testActionWithStreamResponse() throws Exception {

        doTestActionWithStreamResponse( true );

        doTestActionWithStreamResponse( false );
    }

    protected void doTestActionWithStreamResponse( boolean ajax ) throws Exception {

        initTest( true );

        CommandResponse response = handlerComponent.handle( request, "download" );

        verify( request, never() ).getRequestObject();

        verify( httpServletRequest, never() ).getParameter( "ajaxAction" );

        assertNotNull( "Response cannot be null", response );

        assertTrue( "Response must be SendStreamResponse", response instanceof SendStreamResponse );

    }
}
