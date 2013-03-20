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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.jbpm.formModeler.service.bb.commons.config.LocaleManager;
import org.jbpm.formModeler.service.bb.commons.config.componentsFactory.BasicFactoryElement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 */
public abstract class DefaultValueConverter extends BasicFactoryElement implements StaticValueConverter {
    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DefaultValueConverter.class.getName());

    public static final char SEPARATOR = '|';

    private boolean evictBinaryElements = true;
    private int minEvictSizeInBytes = 1000;

    public boolean isEvictBinaryElements() {
        return evictBinaryElements;
    }

    public void setEvictBinaryElements(boolean evictBinaryElements) {
        this.evictBinaryElements = evictBinaryElements;
    }

    public int getMinEvictSizeInBytes() {
        return minEvictSizeInBytes;
    }

    public void setMinEvictSizeInBytes(int minEvictSizeInBytes) {
        this.minEvictSizeInBytes = minEvictSizeInBytes;
    }

    /**
     * Get the class accepted by this converter
     *
     * @return Accepted class
     */
    public String getAcceptedItemType() {
        return getAcceptedClass().getName();
    }

    public String getTypeDescription(Locale lang) {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.valueConverters.descriptions", lang);
        try {
            return bundle.getString("type." + getAcceptedClass().getName());
        } catch (Exception e) {
            log.debug("Error describing " + getAcceptedClass().getName(), e);
        }
        return getAcceptedClass().getName().substring(getAcceptedClass().getName().lastIndexOf('.') + 1);
    }

    protected LocaleManager getLocaleManager() {
        return (LocaleManager) factoryLookup("org.jbpm.formModeler.service.LocaleManager");
    }

    public String getTypeDescription() {
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.valueConverters.descriptions", getLocaleManager().getCurrentLocale());
        try {
            return bundle.getString("type." + getAcceptedClass().getName());
        } catch (Exception e) {
            log.debug("Error describing " + getAcceptedClass().getName(), e);
        }
        return getAcceptedClass().getName().substring(getAcceptedClass().getName().lastIndexOf('.'));
    }

    /**
     * Determine if given item type name is accepted by the converter
     *
     * @param itemTypeName
     * @return true if it accepts this item type name, false otherwise
     */
    public boolean accepts(String itemTypeName) {
        return getAcceptedClass().getName().equals(itemTypeName);
    }

    protected String[] decodeStringArray(String textValue) {
        if (textValue == null || textValue.trim().length() == 0) return new String[0];
        CSVReader reader = new CSVReader(new StringReader(textValue.trim()));
        try {
            return reader.readNext();
        } catch (IOException e) {
            log.error("Error: ", e);
        }
        return null;
    }

    protected String encodeStringArray(String[] value) {
        if (value == null || value.length == 0) return "";
        StringWriter swriter = new StringWriter();
        CSVWriter writer = new CSVWriter(swriter);
        writer.writeNext(value);
        try {
            writer.close();
        } catch (IOException e) {
            log.error("Error: ", e);
            return "";
        }
        return swriter.toString();
    }

    public boolean isVisible() {
        return true;
    }

    public int getType() {
        return TYPE_PRIMITIVE;
    }

    public boolean isTokenizable() {
        return false;
    }

    public String rawFlatValue(Object value, String lang) {
        return flatValue(value, lang);
    } 
}
