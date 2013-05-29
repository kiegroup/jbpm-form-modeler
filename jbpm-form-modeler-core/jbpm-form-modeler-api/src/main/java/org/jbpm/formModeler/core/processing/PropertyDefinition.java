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
package org.jbpm.formModeler.core.processing;

import java.util.Map;

/**
 *  Helper class to get information about a java POJO
 */
public interface PropertyDefinition {

    public static final String PROP_IDENTIFIER = "isIdentifier";
    public static final String PROP_RELATION = "isRelation";
    public static final String[] PROPERTY_FLAGS = {PROP_RELATION, PROP_IDENTIFIER};

    /**
     * Property class
     *
     * @return the property class
     */
    public Class getPropertyClass();

    /**
     * @return a class name representation for this property definition
     */
    public String getPropertyClassName();

    /**
     * Property definition id, that is a combination  of class and attributes
     *
     * @return Property definition id
     */
    public String getId();


    public Map getPropertyAttributes();


    public boolean isIdentifier();

    /**
     * Given a flag name, determine if this property definition has it on.
     * @param propFlag flag name, one of PROPERTY_FLAGS
     * @return
     */
    boolean is(String propFlag);

    /**
     * Determines if current property can be not null
     *
     * @return true if current property can be not null
     */
    boolean isRequired();
}
