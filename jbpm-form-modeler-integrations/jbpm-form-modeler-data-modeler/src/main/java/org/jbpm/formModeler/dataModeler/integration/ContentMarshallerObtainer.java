package org.jbpm.formModeler.dataModeler.integration;

import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.ContentMarshallerContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ContentMarshallerObtainer {
    @Inject
    private DeploymentService deploymentService;

    public ContentMarshallerContext getMarshaller(String deploymentId) {
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
        if (deployedUnit == null) {
            return new ContentMarshallerContext();
        }
        InternalRuntimeManager manager = (InternalRuntimeManager) deployedUnit.getRuntimeManager();
        return new ContentMarshallerContext(manager.getEnvironment().getEnvironment(), manager.getEnvironment().getClassLoader());
    }

}
