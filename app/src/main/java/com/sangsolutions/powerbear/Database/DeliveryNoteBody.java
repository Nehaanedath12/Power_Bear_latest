package com.sangsolutions.powerbear.Database;

public class DeliveryNoteBody {
    String sVoucherNo,sSONo,sItemCode,iUser,iProduct,sDescription,iWarehouse,sAttachment,sRemarks,fQty,Unit;


    public String getiUser() {
        return iUser;
    }

    public void setiUser(String iUser) {
        this.iUser = iUser;
    }

    public String getiProduct() {
        return iProduct;
    }

    public void setiProduct(String iProduct) {
        this.iProduct = iProduct;
    }

    public String getsSONo() {
        return sSONo;
    }

    public void setsSONo(String sSONo) {
        this.sSONo = sSONo;
    }

    public String getsVoucherNo() {
        return sVoucherNo;
    }

    public void setsVoucherNo(String sVoucherNo) {
        this.sVoucherNo = sVoucherNo;
    }

    public String getsItemCode() {
        return sItemCode;
    }

    public void setsItemCode(String sItemCode) {
        this.sItemCode = sItemCode;
    }

    public String getsDescription() {
        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getiWarehouse() {
        return iWarehouse;
    }

    public void setiWarehouse(String iWarehouse) {
        this.iWarehouse = iWarehouse;
    }

    public String getsAttachment() {
        return sAttachment;
    }

    public void setsAttachment(String sAttachment) {
        this.sAttachment = sAttachment;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }

    public String getfQty() {
        return fQty;
    }

    public void setfQty(String fQty) {
        this.fQty = fQty;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }
}
