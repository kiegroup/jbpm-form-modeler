package org.jbpm.formModeler.editor.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.api.model.FormTO;
import org.jbpm.formModeler.api.processing.FormEditorContext;
import org.jbpm.formModeler.api.processing.FormEditorContextManager;
import org.jbpm.formModeler.api.processing.FormEditorContextTO;
import org.jbpm.formModeler.api.processing.FormRenderContextManager;
import org.uberfire.backend.vfs.Path;

import java.io.Serializable;
import java.util.List;

@Remote
public interface FormModelerService extends FormEditorContextManager, Serializable {

    List<FormTO> getAllForms();

    FormTO getCurrentForm(String contextUri);

    Long setFormId(Long formId, String contextURI);

    void saveForm(String ctxUID);

    FormEditorContextTO loadForm(Path context);

    FormEditorContextTO setFormFocus(String ctxUID);

    void removeEditingForm(String ctxUID);

    Path createForm(Path context, String formName);
}
