package org.jbpm.formModeler.editor.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.editor.model.FormTO;
import org.uberfire.backend.vfs.Path;

import java.io.Serializable;
import java.util.List;

@Remote
public interface FormModelerService extends Serializable {

    List<FormTO> getAllForms();

    FormTO getCurrentForm(String contextUri);

    Long setFormId(Long formId, String contextURI);

    void saveForm(Path path);

    Long loadForm(Path context);

    Long setFormFocus(Path context);

    Path createForm(Path context, String formName);
}
