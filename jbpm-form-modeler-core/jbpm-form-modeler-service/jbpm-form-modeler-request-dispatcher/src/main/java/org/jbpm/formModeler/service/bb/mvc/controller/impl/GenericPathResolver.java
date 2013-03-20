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
/*
* Project     : BBlocks
* Subsystem   : MVC
* Created on  : 07-may-2004
* CVS Id      : $Id: GenericPathResolver.java,v 1.3 2006-11-07 17:21:21 carlos Exp $
*
* (C) 2003 JBoss Inc, S.L.
* All rights reserved.
*/

package org.jbpm.formModeler.service.bb.mvc.controller.impl;

import org.jbpm.formModeler.service.bb.mvc.controller.ApplicationPathResolver;

import javax.servlet.ServletContext;
import java.io.File;

public class GenericPathResolver implements ApplicationPathResolver {
    private ApplicationPathResolver resinResolver = new ResinPathResolverImpl();
    private ApplicationPathResolver tomcatResolver = new TomcatPathResolverImpl();

    /**
     * Logger
     */
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(GenericPathResolver.class.getName());

    public String resolvePath(ServletContext context) throws Exception {
        String contextClass = context.getClass().getName();
        if (contextClass.startsWith("com.caucho"))
            return resinResolver.resolvePath(context);
        else if (contextClass.startsWith("org.apache.catalina"))
            return tomcatResolver.resolvePath(context);
        else
            //return new File(context.getRealPath(".")).getParent();
            //WM
            return new File(context.getRealPath(".")).getPath();


        //return context.getResource(".").getFile();
    }
}
