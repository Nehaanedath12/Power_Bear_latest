package com.sangsolutions.powerbear.Singleton;
import com.sangsolutions.powerbear.Adapter.POListAdaptet.POList;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptPoSingleton {
    private static GoodsReceiptPoSingleton goodsReceiptPoSingleton;
    public List<POList> list = new ArrayList<>();

    private GoodsReceiptPoSingleton(){}

    public static GoodsReceiptPoSingleton getInstance(){
        if(goodsReceiptPoSingleton ==null){
            goodsReceiptPoSingleton = new GoodsReceiptPoSingleton();
        }
        return goodsReceiptPoSingleton;
    }

    public List<POList> getList(){return list;}
    public void setList(List<POList> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
