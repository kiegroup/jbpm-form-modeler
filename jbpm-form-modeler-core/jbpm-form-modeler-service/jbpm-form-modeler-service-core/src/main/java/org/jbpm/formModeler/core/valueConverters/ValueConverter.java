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

import java.util.List;
import java.util.Locale;

/**
 * 
 *
 */
public interface ValueConverter {

    public static final int TYPE_PRIMITIVE = 0;
    public static final int TYPE_INSTANCE = 1;
    public static final int TYPE_MULTIPLE_INSTANCE = 2;
    public static final int TYPE_REMOTE_OBJECT = 3;
    public static final int TYPE_MULTIPLE_REMOTE_OBJECT = 4;

    /**
     * Get the class accepted by this converter
     *
     * @return Accepted class
     */
    public String getAcceptedItemType();

    /**
     * Describe the class in human language.
     *
     * @param lang Language to be used
     * @return A readable description for the class
     * @deprecated Use version that uses default locale
     */
    public String getTypeDescription(Locale lang);

    /**
     * Describe the class in human language.
     *
     * @return A readable description for the class
     */
    public String getTypeDescription();

    /**
     * Determine if given item type name is accepted by the converter
     *
     * @param itemTypeName
     * @return true if it accepts this item type name, false otherwise
     */
    public boolean accepts(String itemTypeName);

    /**
     * Given a value, whose class must be the one accepted by the converter,
     * convert it to a list of DynValue objects.
     *
     * @param value Value to convert
     * @return a List representation for this value
     */
    public List writeValue(Object value);

    /**
     * Given a list of DynValue objects, convert it to a Object of accepted
     * class.
     *
     * @param values Values to convert
     * @return Object read from values
     */
    public Object readValue(List values);

    /**
     * Given an Object belonging to an accepted class, convert it to a String
     * value representing it.
     *
     * @param value Value to convert
     * @return a String representation for this object.
     */
    public String getTextValue(Object value);

    /**
     * Given a text value, which is a representation for an object, convert it
     * to the original object.
     *
     * @param textValue
     * @return an object read from a String value
     */
    public Object readTextValue(String textValue);


    /**
     * Convert a value of accepted class into a flat String value
     *
     * @param value
     * @param lang
     * @return a String representation for this object to be used in search indexes
     */
    public String flatValue(Object value, String lang);

    /**
     * Convert a value of accepted class into a raw flat String value that is, without using formats for
     * numbers, dates ...
     *
     * @param value
     * @param lang
     * @return a String representation for this object to be used in search indexes
     */
    public String rawFlatValue(Object value, String lang);

    /**
     * Determines if this converter is selectable to create a new property for an entity.
     *
     * @return true if this converter is selectable to create a new property for an entity.
     */
    public boolean isVisible();

    /**
     * Determine the converter type:  TYPE_PRIMITIVE, TYPE_INSTANCE, TYPE_MULTIPLE_INSTANCE, TYPE_REMOTE_OBJECT or TYPE_MULTIPLE_REMOTE_OBJECT
     *
     * @return the converter type
     */
    public int getType();

    /**
     * Determine if the data type managed is tokenizable when indexed
     *
     * @return true if the data type managed is tokenizable when indexed
     */
    boolean isTokenizable();
}
