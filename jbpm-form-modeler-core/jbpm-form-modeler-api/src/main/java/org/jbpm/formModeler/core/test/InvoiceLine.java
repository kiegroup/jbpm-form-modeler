package org.jbpm.formModeler.core.test;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 6/13/13
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceLine {
    public static String idLine;
    public static String description;
    public static Double amount;

    public static String getIdLine() {
        return idLine;
    }

    public static void setIdLine(String idLine) {
        InvoiceLine.idLine = idLine;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        InvoiceLine.description = description;
    }

    public static Double getAmount() {
        return amount;
    }

    public static void setAmount(Double amount) {
        InvoiceLine.amount = amount;
    }
}
