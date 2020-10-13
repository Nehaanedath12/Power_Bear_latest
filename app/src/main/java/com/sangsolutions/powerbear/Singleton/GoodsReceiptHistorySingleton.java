package com.sangsolutions.powerbear.Singleton;



import com.sangsolutions.powerbear.Adapter.GoodsReceiptHistoryAdapter.GoodsReceiptHistory;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptHistorySingleton {
    private static GoodsReceiptHistorySingleton goodsReceiptHistorySingleton;
    public List<GoodsReceiptHistory> list = new ArrayList<>();

    private GoodsReceiptHistorySingleton(){}

    public static GoodsReceiptHistorySingleton getInstance(){
        if(goodsReceiptHistorySingleton ==null){
            goodsReceiptHistorySingleton = new GoodsReceiptHistorySingleton();
        }
        return goodsReceiptHistorySingleton;
    }

    public List<GoodsReceiptHistory> getList(){return list;}
    public void setList(List<GoodsReceiptHistory> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
