package org.jbpm.formModeler.core.test;

import java.io.Serializable;

public class InvoiceLine implements Serializable {
    private String idLine;
    private String description;
    private Double amount;

    public String getIdLine() {
        return idLine;
    }

    public void setIdLine(String idLine) {
        this.idLine = idLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        String response = "Invoice Line:";
        response += "\nid: " + idLine;
        response += "\ndescription: " + description;
        response += "\namount: " + amount;
        return response;
    }
}
