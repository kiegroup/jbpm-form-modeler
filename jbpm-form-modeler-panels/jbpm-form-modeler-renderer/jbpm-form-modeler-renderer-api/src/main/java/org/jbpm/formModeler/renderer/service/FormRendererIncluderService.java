package org.jbpm.formModeler.renderer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.processing.FormRenderContextTO;

@Remote
public interface FormRendererIncluderService {
    FormRenderContextTO launchTest();
}
