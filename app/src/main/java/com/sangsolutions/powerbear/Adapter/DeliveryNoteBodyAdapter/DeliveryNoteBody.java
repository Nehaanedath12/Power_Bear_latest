package com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter;

public class DeliveryNoteBody {
    String sSONo,sSOQty,sName,sCode,fTempQty,iProduct,iWarehouse,sAttachment,sRemarks,fQty,Unit;

    public DeliveryNoteBody( String sSONo, String sSOQty,String sName, String sCode, String fTempQty, String iProduct, String iWarehouse, String sAttachment, String sRemarks, String fQty, String unit) {
        this.sSONo = sSONo;
        this.sSOQty = sSOQty;
        this.sName = sName;
        this.sCode = sCode;
        this.fTempQty = fTempQty;
        this.iProduct = iProduct;
        this.iWarehouse = iWarehouse;
        this.sAttachment = sAttachment;
        this.sRemarks = sRemarks;
        this.fQty = fQty;
        this.Unit = unit;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsSONo() {
        return sSONo;
    }

    public void setsSONo(String sSONo) {
        this.sSONo = sSONo;
    }

    public String getfSOQty() {
        return sSOQty;
    }

    public void setsSOQty(String sSOQty) {
        this.sSOQty = sSOQty;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode;
    }

    public String getfTempQty() {
        return fTempQty;
    }

    public void setfTempQty(String fTempQty) {
        this.fTempQty = fTempQty;
    }

    public String getiProduct() {
        return iProduct;
    }

    public void setiProduct(String iProduct) {
        this.iProduct = iProduct;
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
