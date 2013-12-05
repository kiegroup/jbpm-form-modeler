package org.jbpm.formModeler.editor.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.client.FormEditorContextTO;
import org.uberfire.backend.vfs.Path;

import java.io.Serializable;

@Remote
public interface FormModelerService extends Serializable {
    void saveForm(String ctxUID) throws Exception;

    FormEditorContextTO loadForm(Path context);

    FormEditorContextTO reloadForm(Path path, String ctxUID);

    FormEditorContextTO setFormFocus(String ctxUID);

    void changeContextPath(String ctxUID, Path path);

    void removeEditingForm(String ctxUID);

    Path createForm(Path context, String formName);

    boolean deleteForm(Path context);
}
