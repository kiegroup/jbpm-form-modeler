package org.jbpm.formModeler.service.bb.mvc.components.handling.mocks;

import org.jbpm.formModeler.service.bb.mvc.components.CurrentComponentRenderer;
import org.jbpm.formModeler.service.bb.mvc.components.handling.BeanHandler;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandRequest;
import org.jbpm.formModeler.service.bb.mvc.controller.CommandResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.DoNothingResponse;
import org.jbpm.formModeler.service.bb.mvc.controller.responses.SendStreamResponse;

import static org.mockito.Mockito.*;

public class MyBeanHandler extends BeanHandler {

    protected CurrentComponentRenderer renderer;

    public MyBeanHandler( CurrentComponentRenderer renderer ) {
        this.renderer = renderer;

        this.setEnabledForActionHandling( true );
        this.setEnableDoubleClickControl( false );

        this.start();
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
    protected CurrentComponentRenderer getCurrentComponentRenderer() {
        return renderer;
    }
}
