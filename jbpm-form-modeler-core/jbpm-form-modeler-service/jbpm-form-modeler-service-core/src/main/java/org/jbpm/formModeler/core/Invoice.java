package org.jbpm.formModeler.core;

import org.jbpm.formModeler.api.model.i18n.I18nSet;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: nmirasch
 * Date: 4/17/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Invoice {
    private String id;
    private String name;
    private String address;
    private String city;
    private String zip;
    private String email;
    private String phone;

    private Double accountBalance;
    private Double accountBalance_counting;
    private Double availableCredit;
    private Double totalBalance;


    private Date createdDate;
    private Date updatedDate;

    private Boolean Enable;
    private I18nSet notes;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Double getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(Double availableCredit) {
        this.availableCredit = availableCredit;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Boolean getEnable() {
        return Enable;
    }

    public void setEnable(Boolean enable) {
        Enable = enable;
    }

    public I18nSet getNotes() {
        return notes;
    }

    public void setNotes(I18nSet notes) {
        this.notes = notes;
    }

    public Double getAccountBalance_counting() {
        return accountBalance_counting;
    }

    public void setAccountBalance_counting(Double accountBalance_counting) {
        this.accountBalance_counting = accountBalance_counting;
    }
}
