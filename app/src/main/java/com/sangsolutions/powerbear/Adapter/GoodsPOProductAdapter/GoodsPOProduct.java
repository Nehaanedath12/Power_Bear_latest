package com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter;

public class GoodsPOProduct {
    String DocNo,Name,code,Product,POQty,TempQty,Unit;

    public GoodsPOProduct(String docNo, String name,String code, String product, String POQty,String TempQty, String unit) {
        this.DocNo = docNo;
        this.Name = name;
        this.code = code;
        this.Product = product;
        this.POQty = POQty;
        this.TempQty = TempQty;
        this.Unit = unit;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }

    public String getPOQty() {
        return POQty;
    }

    public void setPOQty(String POQty) {
        this.POQty = POQty;
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
}
