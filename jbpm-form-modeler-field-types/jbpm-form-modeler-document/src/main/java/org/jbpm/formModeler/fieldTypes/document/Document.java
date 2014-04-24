package org.jbpm.formModeler.fieldTypes.document;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Document implements Serializable{
    private String identifier;
    private String name;
    private long size;
    private Date lastModified;

    private Map<String, String> attributes;

    public Document(String identifier, String name, long size, Date lastModified) {
        this.identifier = identifier;
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;

        attributes = new HashMap<String, String>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }

    public void addAttribute(String attributeName, String attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

}
