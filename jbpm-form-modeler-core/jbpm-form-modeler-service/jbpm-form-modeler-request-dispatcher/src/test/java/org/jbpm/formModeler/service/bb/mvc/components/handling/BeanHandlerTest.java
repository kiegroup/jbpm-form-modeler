package org.jbpm.formModeler.service.bb.mvc.components.handling;

import org.jbpm.formModeler.service.bb.mvc.components.handling.mocks.MyBeanHandler;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BeanHandlerTest extends AbstractBeanHandlerTest<BeanHandler> {

    @Override
    public BeanHandler getHandlerComponent() {
        return new MyBeanHandler( renderer );
    }
}
