package com.sangsolutions.powerbear.Adapter.StockDetailsAdapter;

public class StockDetailsClass {
    String Product,ProductCode,Warehouse,BalQty;
    public StockDetailsClass(String product, String productCode, String warehouse, String balQty) {
        Product = product;
        ProductCode = productCode;
        Warehouse = warehouse;
        BalQty = balQty;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }


    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getWarehouse() {
        return Warehouse;
    }

    public void setWarehouse(String warehouse) {
        Warehouse = warehouse;
    }

    public String getBalQty() {
        return BalQty;
    }

    public void setBalQty(String balQty) {
        BalQty = balQty;
    }
}
