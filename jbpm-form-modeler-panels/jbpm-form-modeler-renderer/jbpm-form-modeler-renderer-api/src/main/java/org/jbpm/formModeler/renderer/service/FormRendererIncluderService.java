package org.jbpm.formModeler.renderer.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.client.FormRenderContextTO;

@Remote
public interface FormRendererIncluderService {
    FormRenderContextTO launchTest();

    Boolean persistContext(String ctxUID);

    Boolean clearContext(String ctxUID);
}
