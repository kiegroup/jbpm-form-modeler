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

import org.apache.commons.lang.builder.ToStringBuilder;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * @hibernate.class table="ddm_dyn_value"
 */
public class DynValue implements Serializable {

    private static transient org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DynValue.class.getName());

    /**
     * identifier field
     */
    private Long propertyId;

    /**
     * nullable persistent field
     */
    private String propertyName;

    /**
     * nullable persistent field
     */
    private String selector;

    /**
     * nullable persistent field
     */
    private BigDecimal numberValue;

    /**
     * nullable persistent field
     */
    private String stringValue;

    /**
     * nullable persistent field
     */
    private Date dateValue;

    /**
     * nullable persistent field
     */
    private byte[] binaryValue = new byte[]{};

    /**
     * persistent field
     */
    private Set objectDynValues;

    /**
     * full constructor
     */
    public DynValue(Long propertyId, String propertyName, String selector, BigDecimal numberValue, String stringValue, Date dateValue, byte[] binaryValue, Set objectDynvalues) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.selector = selector;
        this.numberValue = numberValue;
        this.stringValue = stringValue;
        this.dateValue = dateValue;
        this.binaryValue = binaryValue;
        this.objectDynValues = objectDynvalues;
    }

    /**
     * default constructor
     */
    public DynValue() {
    }

    /**
     * minimal constructor
     */
    public DynValue(Long propertyId, Set objectDynvalues) {
        this.propertyId = propertyId;
        this.objectDynValues = objectDynvalues;
    }

    /**
     * @hibernate.id generator-class="assigned"
     * type="java.lang.Long"
     * column="property_id"
     */
    public Long getPropertyId() {
        return this.propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * @hibernate.property column="property_name"
     * length="512"
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @hibernate.property column="selector"
     * length="512"
     */
    public String getSelector() {
        return this.selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    /**
     * @hibernate.property column="number_value"
     * length="-1"
     */
    public BigDecimal getNumberValue() {
        return this.numberValue;
    }

    public void setNumberValue(BigDecimal numberValue) {
        this.numberValue = numberValue;
    }

    /**
     * @hibernate.property column="string_value"
     */
    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * @hibernate.property column="date_value"
     * length="8"
     */
    public Date getDateValue() {
        return this.dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * @hibernate.property column="binary_value"
     * length="-1"
     */
    public byte[] getBinaryValue() {
        return this.binaryValue;
    }

    public void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    /**
     * @hibernate.set lazy="true"
     * inverse="true"
     * cascade="none"
     * @hibernate.collection-key column="property_id"
     * @hibernate.collection-one-to-many class="org.jbpm.ddm.dynObjects.model.ObjectDynValue"
     */
    public Set getObjectDynValues() {
        return this.objectDynValues;
    }

    public void setObjectDynValues(Set objectDynValues) {
        this.objectDynValues = objectDynValues;
    }


    public String toString() {
        return new ToStringBuilder(this)
                .append("propertyId", getPropertyId())
                .toString();
    }

    public boolean equals(Object other) {
        try {
            if (propertyId == null)
                return false;
            return propertyId.longValue() == (((DynValue) other).propertyId).longValue();
        }
        catch (Exception e) {
            return false;
        }
    }

    public int hashCode() {
        return 0;
    }
}
