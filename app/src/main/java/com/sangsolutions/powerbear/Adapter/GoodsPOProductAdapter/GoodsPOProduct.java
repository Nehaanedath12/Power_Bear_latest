package com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter;

public class GoodsPOProduct {
    String DocNo,Name,Product,POQty,Unit;

    public GoodsPOProduct(String docNo, String name, String product, String POQty, String unit) {
        this.DocNo = docNo;
        this.Name = name;
        this.Product = product;
        this.POQty = POQty;
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

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }
}
