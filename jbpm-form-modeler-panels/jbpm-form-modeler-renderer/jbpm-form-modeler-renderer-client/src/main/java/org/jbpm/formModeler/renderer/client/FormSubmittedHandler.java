package org.jbpm.formModeler.renderer.client;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: pefernan
 * Date: 07/05/13
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public interface FormSubmittedHandler extends EventHandler {
    void afterSubmit(FormSubmittedEvent event);
}
