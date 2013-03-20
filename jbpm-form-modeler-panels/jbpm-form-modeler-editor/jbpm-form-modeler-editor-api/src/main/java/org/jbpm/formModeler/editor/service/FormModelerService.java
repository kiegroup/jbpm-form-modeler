package org.jbpm.formModeler.editor.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.formModeler.editor.model.FormTO;
import org.uberfire.backend.vfs.Path;

import java.io.Serializable;
import java.util.List;

@Remote
public interface FormModelerService extends Serializable {

    List<FormTO> getAllForms();

    FormTO getCurrentForm();

    Long setFormId(Long formId);

    void saveForm(Path path);

    Long loadForm(Path context);

    Path createForm(Path context, String formName);
}
