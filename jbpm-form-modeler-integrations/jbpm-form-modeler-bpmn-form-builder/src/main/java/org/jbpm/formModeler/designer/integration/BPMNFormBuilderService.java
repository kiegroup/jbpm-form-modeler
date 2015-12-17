/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.designer.integration;


import org.eclipse.bpmn2.Definitions;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.Path;

public interface BPMNFormBuilderService {

    String buildFormXML(Path base, String fileName, String uri, Definitions source, String id) throws Exception;

    String buildEmptyFormXML(String fileName) throws Exception;

}
