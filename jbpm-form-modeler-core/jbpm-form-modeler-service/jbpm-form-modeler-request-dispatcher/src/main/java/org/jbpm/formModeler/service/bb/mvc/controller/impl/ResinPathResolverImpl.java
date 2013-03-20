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
package org.jbpm.formModeler.service.bb.mvc.controller.impl;

import org.jbpm.formModeler.service.bb.mvc.controller.ApplicationPathResolver;

import javax.servlet.ServletContext;

public class ResinPathResolverImpl implements ApplicationPathResolver {

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ResinPathResolverImpl.class.getName());

    public String resolvePath(ServletContext context) throws Exception {
        return context.getResource(".").getFile();
    }
}
