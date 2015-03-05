package org.jbpm.formModeler.editor.model;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class FormModelerContent {
    private Path path;
    private Overview overview;
    private FormEditorContextTO contextTO;

    public Path getPath() {
        return path;
    }

    public void setPath( Path path ) {
        this.path = path;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview( Overview overview ) {
        this.overview = overview;
    }

    public FormEditorContextTO getContextTO() {
        return contextTO;
    }

    public void setContextTO( FormEditorContextTO contextTO ) {
        this.contextTO = contextTO;
    }
}
