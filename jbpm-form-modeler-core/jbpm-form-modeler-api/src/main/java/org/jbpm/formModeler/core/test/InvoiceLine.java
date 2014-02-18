package org.jbpm.formModeler.core.test;

import java.io.Serializable;

public class InvoiceLine implements Serializable {
    private String idLine;
    private Character letter;
    private String description;
    private Double amount;
    private Byte shortRef;

    public String getIdLine() {
        return idLine;
    }

    public void setIdLine(String idLine) {
        this.idLine = idLine;
    }

    public Character getLetter() {
        return letter;
    }

    public void setLetter( Character letter ) {
        this.letter = letter;
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

    public Byte getShortRef() {
        return shortRef;
    }

    public void setShortRef( Byte shortRef ) {
        this.shortRef = shortRef;
    }

    @Override
    public String toString() {
        String response = "Invoice Line:";
        response += "\nid: " + idLine;
        response += "\nletter: " + letter;
        response += "\ndescription: " + description;
        response += "\namount: " + amount;
        response += "\nshort reference: " + shortRef;
        return response;
    }
}
