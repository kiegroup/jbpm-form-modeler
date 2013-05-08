package com.google.gwt.event.dom.client;

import com.google.gwt.event.shared.HasHandlers;
import org.jbpm.formModeler.renderer.client.FormSubmittedHandler;

/**
 * Created with IntelliJ IDEA.
 * User: pefernan
 * Date: 07/05/13
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public interface HasFormSubmittedHandlers extends HasHandlers {
    void addFormSubmittedHandler(FormSubmittedHandler handler);
}
