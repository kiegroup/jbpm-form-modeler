/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
