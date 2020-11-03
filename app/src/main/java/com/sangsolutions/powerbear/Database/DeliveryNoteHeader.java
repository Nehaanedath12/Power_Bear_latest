package com.sangsolutions.powerbear.Database;

public class DeliveryNoteHeader {
    String sVoucherNo,sDate,sSalesman,iUser,sJobNo,sContactPerson,sSOPNo,sDeliveryLocation,iCustomer,sNarration,sCustomerRef;


    public DeliveryNoteHeader(String sVoucherNo, String sDate, String sSalesman, String iUser, String sJobNo, String sContactPerson, String sSOPNo, String sDeliveryLocation, String iCustomer, String sNarration, String sCustomerRef) {
        this.sVoucherNo = sVoucherNo;
        this.sDate = sDate;
        this.sSalesman = sSalesman;
        this.iUser = iUser;
        this.sJobNo = sJobNo;
        this.sContactPerson = sContactPerson;
        this.sSOPNo = sSOPNo;
        this.sDeliveryLocation = sDeliveryLocation;
        this.iCustomer = iCustomer;
        this.sNarration = sNarration;
        this.sCustomerRef = sCustomerRef;
    }

    public String getiUser() {
        return iUser;
    }

    public void setiUser(String iUser) {
        this.iUser = iUser;
    }

    public String getsVoucherNo() {
        return sVoucherNo;
    }

    public void setsVoucherNo(String sVoucherNo) {
        this.sVoucherNo = sVoucherNo;
    }

    public String getsDate() {
        return sDate;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public String getsSalesman() {
        return sSalesman;
    }

    public void setsSalesman(String sSalesman) {
        this.sSalesman = sSalesman;
    }

    public String getsJobNo() {
        return sJobNo;
    }

    public void setsJobNo(String sJobNo) {
        this.sJobNo = sJobNo;
    }

    public String getsContactPerson() {
        return sContactPerson;
    }

    public void setsContactPerson(String sContactPerson) {
        this.sContactPerson = sContactPerson;
    }

    public String getsSOPNo() {
        return sSOPNo;
    }

    public void setsSOPNo(String sSOPNo) {
        this.sSOPNo = sSOPNo;
    }

    public String getsDeliveryLocation() {
        return sDeliveryLocation;
    }

    public void setsDeliveryLocation(String sDeliveryLocation) {
        this.sDeliveryLocation = sDeliveryLocation;
    }

    public String getiCustomer() {
        return iCustomer;
    }

    public void setiCustomer(String iCustomer) {
        this.iCustomer = iCustomer;
    }

    public String getsNarration() {
        return sNarration;
    }

    public void setsNarration(String sNarration) {
        this.sNarration = sNarration;
    }

    public String getsCustomerRef() {
        return sCustomerRef;
    }

    public void setsCustomerRef(String sCustomerRef) {
        this.sCustomerRef = sCustomerRef;
    }
}
