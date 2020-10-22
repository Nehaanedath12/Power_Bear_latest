package com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter;

import com.sangsolutions.powerbear.Adapter.RemarksTypeAdapter.RemarksType;

public class GoodsReceiptBody {
    String sPONo,Name,Code,iProduct,sWarehouse,iWarehouse,Barcode,fPOQty,fQty,TempQty,Unit,
            sRemarks,fMinorDamageQty,sMinorRemarks,sMinorAttachment,
            fDamagedQty,sDamagedRemarks,sDamagedAttachment,RemarksMinorType,RemarksDamagedType;

    public GoodsReceiptBody(String sPONo, String Name, String Code, String iProduct, String sWarehouse, String iWarehouse,
                            String barcode, String fPOQty, String fQty, String TempQty,
                            String unit, String sRemarks, String fMinorDamageQty, String sMinorRemarks, String sMinorAttachment,
                            String fDamagedQty, String sDamagedRemarks, String sDamagedAttachment, String RemarksMinorType,String RemarksDamagedType) {
        this.sPONo = sPONo;
        this.Name = Name;
        this.Code = Code;
        this.iProduct = iProduct;
        this.sWarehouse = sWarehouse;
        this.iWarehouse = iWarehouse;
        this.Barcode = barcode;
        this.fPOQty = fPOQty;
        this.fQty = fQty;
        this.TempQty = TempQty;
        this.Unit = unit;
        this.sRemarks = sRemarks;
        this.fMinorDamageQty = fMinorDamageQty;
        this.sMinorRemarks = sMinorRemarks;
        this.sMinorAttachment = sMinorAttachment;
        this.fDamagedQty = fDamagedQty;
        this.sDamagedRemarks = sDamagedRemarks;
        this.sDamagedAttachment = sDamagedAttachment;
        this.RemarksMinorType= RemarksMinorType;
        this.RemarksDamagedType =RemarksDamagedType;
    }

    public String getsPONo() {
        return sPONo;
    }

    public void setsPONo(String sPONo) {
        this.sPONo = sPONo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getiProduct() {
        return iProduct;
    }

    public void setiProduct(String iProduct) {
        this.iProduct = iProduct;
    }

    public String getsWarehouse() {
        return sWarehouse;
    }

    public void setsWarehouse(String sWarehouse) {
        this.sWarehouse = sWarehouse;
    }

    public String getiWarehouse() {
        return iWarehouse;
    }

    public void setiWarehouse(String iWarehouse) {
        this.iWarehouse = iWarehouse;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getfPOQty() {
        return fPOQty;
    }

    public void setfPOQty(String fPOQty) {
        this.fPOQty = fPOQty;
    }

    public String getfQty() {
        return fQty;
    }

    public void setfQty(String fQty) {
        this.fQty = fQty;
    }

    public String getTempQty() {
        return TempQty;
    }

    public void setTempQty(String tempQty) {
        TempQty = tempQty;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }

    public String getfMinorDamageQty() {
        return fMinorDamageQty;
    }

    public void setfMinorDamageQty(String fMinorDamageQty) {
        this.fMinorDamageQty = fMinorDamageQty;
    }

    public String getsMinorRemarks() {
        return sMinorRemarks;
    }

    public void setsMinorRemarks(String sMinorRemarks) {
        this.sMinorRemarks = sMinorRemarks;
    }

    public String getsMinorAttachment() {
        return sMinorAttachment;
    }

    public void setsMinorAttachment(String sMinorAttachment) {
        this.sMinorAttachment = sMinorAttachment;
    }

    public String getfDamagedQty() {
        return fDamagedQty;
    }

    public void setfDamagedQty(String fDamagedQty) {
        this.fDamagedQty = fDamagedQty;
    }

    public String getsDamagedRemarks() {
        return sDamagedRemarks;
    }

    public void setsDamagedRemarks(String sDamagedRemarks) {
        this.sDamagedRemarks = sDamagedRemarks;
    }

    public String getsDamagedAttachment() {
        return sDamagedAttachment;
    }

    public void setsDamagedAttachment(String sDamagedAttachment) {
        this.sDamagedAttachment = sDamagedAttachment;
    }

    public String getRemarksMinorType() {
        return RemarksMinorType;
    }

    public void setRemarksMinorType(String remarksMinorType) {
        RemarksMinorType = remarksMinorType;
    }

    public String getRemarksDamagedType() {
        return RemarksDamagedType;
    }

    public void setRemarksDamagedType(String remarksDamagedType) {
        RemarksDamagedType = remarksDamagedType;
    }
}
