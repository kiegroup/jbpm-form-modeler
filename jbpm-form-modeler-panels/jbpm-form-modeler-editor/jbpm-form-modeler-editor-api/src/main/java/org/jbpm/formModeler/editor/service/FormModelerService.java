package org.jbpm.formModeler.editor.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.client.FormEditorContextManager;
import org.jbpm.formModeler.api.client.FormEditorContextTO;
import org.uberfire.backend.vfs.Path;

import java.io.Serializable;

@Remote
public interface FormModelerService extends FormEditorContextManager, Serializable {
    void saveForm(String ctxUID);

    FormEditorContextTO loadForm(Path context);

    FormEditorContextTO setFormFocus(String ctxUID);

    void removeEditingForm(String ctxUID);

    Path createForm(Path context, String formName);
}
