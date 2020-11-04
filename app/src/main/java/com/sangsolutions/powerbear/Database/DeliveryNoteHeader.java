package com.sangsolutions.powerbear.Database;

public class DeliveryNoteHeader {
    String sVoucherNo,sDate, dProposedDate,iUser,sContactPerson,sSOPNo,iCustomer,sNarration;


    public DeliveryNoteHeader(String sVoucherNo, String sDate, String dProposedDate, String iUser, String sContactPerson, String sSOPNo, String iCustomer, String sNarration) {
        this.sVoucherNo = sVoucherNo;
        this.sDate = sDate;
        this.dProposedDate = dProposedDate;
        this.iUser = iUser;
        this.sContactPerson = sContactPerson;
        this.sSOPNo = sSOPNo;
        this.iCustomer = iCustomer;
        this.sNarration = sNarration;
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

    public String getdProposedDate() {
        return dProposedDate;
    }

    public void setdProposedDate(String dProposedDate) {
        this.dProposedDate = dProposedDate;
    }

    public String getiUser() {
        return iUser;
    }

    public void setiUser(String iUser) {
        this.iUser = iUser;
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
}
