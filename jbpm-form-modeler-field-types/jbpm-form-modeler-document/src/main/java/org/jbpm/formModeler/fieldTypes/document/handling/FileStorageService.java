/**
 * Copyright (C) 2012 JBoss Inc
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
package org.jbpm.formModeler.fieldTypes.document.handling;

import org.jbpm.formModeler.fieldTypes.document.Document;

import java.io.File;

/**
 * Simple storage service
 */
public interface FileStorageService {

    /**
     * Method to store the uploaded file on the system
     * @param file          The File that is going to be stored
     * @return              A Document
     */
    Document saveDocument(File file);

    /**
     * Method to obtain a File for the given storage id
     * @param id            The File id to obtain the File
     * @return              The java.io.File identified with the id
     */
    Document getDocument(String id);

    /**
     * Deletes the File identified by the given id
     * @param id            The File id to delete
     * @return              true if it was possible to remove, false if not
     */
    boolean deleteDocument(String id);


    boolean deleteDocument(Document doc);

    File getDocumentContent(Document doc);
}
