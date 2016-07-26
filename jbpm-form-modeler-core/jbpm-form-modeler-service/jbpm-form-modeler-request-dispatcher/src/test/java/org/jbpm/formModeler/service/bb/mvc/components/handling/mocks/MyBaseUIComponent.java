package org.jbpm.formModeler.service.bb.mvc.components.handling.mocks;

import org.jbpm.formModeler.service.LocaleManager;
import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BaseUIComponent;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.SendStreamResponse;

import static org.mockito.Mockito.mock;

public class MyBaseUIComponent extends BaseUIComponent {

    protected CurrentComponentRenderer renderer;

    public MyBaseUIComponent( LocaleManager localeManager, CurrentComponentRenderer renderer ) {
        this.localeManager = localeManager;
        this.renderer = renderer;

        this.setEnabledForActionHandling( true );
        this.setEnableDoubleClickControl( false );

        start();
    }

    @Override
    public String getBaseComponentJSP() {
        return "/test/myComponent.jsp";
    }

    @Override
    public void doStart( CommandRequest request ) {

    }

    public CommandResponse actionWithResponse( CommandRequest request ) {
        return new DoNothingResponse();
    }

    public void actionWithoutResponse( CommandRequest request ) {

    }

    public SendStreamResponse actionDownload( CommandRequest request ) {
        return mock( SendStreamResponse.class );
    }

    @Override
    public String getBeanJSP() {
        return getBaseComponentJSP();
    }

    @Override
    protected CurrentComponentRenderer getCurrentComponentRenderer() {
        return renderer;
    }
}
