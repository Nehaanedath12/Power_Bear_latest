package com.sangsolutions.powerbear.Adapter.StockCountListAdapter;

public class StockCountList {
    String VNo,Date,TotalQty,Warehouse,WarehouseId;


    public StockCountList(String VNo, String date, String totalQty, String warehouse,String WarehouseId) {
        this.VNo = VNo;
        this.Date = date;
        this.TotalQty = totalQty;
        this.Warehouse = warehouse;
        this.WarehouseId = WarehouseId;
    }


    public String getVNo() {
        return VNo;
    }

    public void setVNo(String VNo) {
        this.VNo = VNo;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(String totalQty) {
        TotalQty = totalQty;
    }

    public String getWarehouse() {
        return Warehouse;
    }

    public void setWarehouse(String warehouse) {
        Warehouse = warehouse;
    }

    public String getWarehouseId() {
        return WarehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        WarehouseId = warehouseId;
    }
}
