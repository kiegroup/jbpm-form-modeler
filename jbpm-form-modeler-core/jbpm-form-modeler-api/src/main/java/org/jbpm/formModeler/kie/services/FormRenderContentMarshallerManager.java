package org.jbpm.formModeler.kie.services;

import org.kie.internal.task.api.ContentMarshallerContext;

import java.io.Serializable;

public interface FormRenderContentMarshallerManager extends Serializable {
    void addContentMarshaller(String id, ContentMarshallerContext context);
    void removeContentMarshaller(String id);

    ContentMarshallerContext getContentMarshaller(String id);
}
