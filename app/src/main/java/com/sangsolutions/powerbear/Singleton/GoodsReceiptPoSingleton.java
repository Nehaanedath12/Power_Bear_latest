package com.sangsolutions.powerbear.Singleton;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptPoSingleton {
    private static GoodsReceiptPoSingleton goodsReceiptPoSingleton;
    public List<String> list = new ArrayList<>();

    private GoodsReceiptPoSingleton(){}

    public static GoodsReceiptPoSingleton getInstance(){
        if(goodsReceiptPoSingleton ==null){
            goodsReceiptPoSingleton = new GoodsReceiptPoSingleton();
        }
        return goodsReceiptPoSingleton;
    }

    public List<String> getList(){return list;}
    public void setList(List<String> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
