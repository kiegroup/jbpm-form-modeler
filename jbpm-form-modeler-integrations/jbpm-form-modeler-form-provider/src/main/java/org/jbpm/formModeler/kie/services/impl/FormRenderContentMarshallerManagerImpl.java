package org.jbpm.formModeler.kie.services.impl;

import org.jbpm.formModeler.kie.services.FormRenderContentMarshallerManager;
import org.kie.internal.task.api.ContentMarshallerContext;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import java.util.concurrent.ConcurrentHashMap;

@SessionScoped
public class FormRenderContentMarshallerManagerImpl implements FormRenderContentMarshallerManager {
    private ConcurrentHashMap<String, ContentMarshallerContext> marhsalContexts = new ConcurrentHashMap<String, ContentMarshallerContext>();

    @Override
    public void addContentMarshaller(String id, ContentMarshallerContext context) {
        marhsalContexts.put(id, context);
    }

    @Override
    public void removeContentMarshaller(String id) {
        marhsalContexts.remove(id);
    }

    @Override
    public ContentMarshallerContext getContentMarshaller(String id) {
        return marhsalContexts.get(id);
    }
}
