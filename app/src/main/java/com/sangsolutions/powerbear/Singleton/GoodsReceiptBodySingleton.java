package com.sangsolutions.powerbear.Singleton;

import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptBodySingleton { private static
GoodsReceiptBodySingleton goodsReceiptBodySingleton;
    public List<GoodsReceiptBody> list = new ArrayList<>();

    private GoodsReceiptBodySingleton(){}

    public static GoodsReceiptBodySingleton getInstance(){
        if(goodsReceiptBodySingleton ==null){
            goodsReceiptBodySingleton = new GoodsReceiptBodySingleton();
        }
        return goodsReceiptBodySingleton;
    }

    public List<GoodsReceiptBody> getList(){return list;}
    public void setList(List<GoodsReceiptBody> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
