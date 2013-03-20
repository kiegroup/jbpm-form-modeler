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
package org.jbpm.formModeler.core.valueConverters;

/**
 * All wrapper thar contains a byte array secuence that can be converted to a <code>java.io.InputStream</code> must
 * implement this interface. All higger programming layers which store files in ddm wrappers needs this
 * interface (and <code>org.jbpm.formModeler.core.valueConverters.FileInstanceCreator</code> one)
 * so as to not implement especific ddm oriented code, not reusable.
 * This interface offers transparency for File treatment in the DDM API.
 */
public interface StreamInstanceResolver extends InstanceResolver {

}
