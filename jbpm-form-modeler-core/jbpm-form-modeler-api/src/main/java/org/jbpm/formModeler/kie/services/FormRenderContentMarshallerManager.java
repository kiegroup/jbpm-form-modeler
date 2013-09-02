package org.jbpm.formModeler.kie.services;

import org.kie.internal.task.api.ContentMarshallerContext;

public interface FormRenderContentMarshallerManager {
    void addContentMarshaller(String id, ContentMarshallerContext context);
    void removeContentMarshaller(String id);

    ContentMarshallerContext getContentMarshaller(String id);
}
