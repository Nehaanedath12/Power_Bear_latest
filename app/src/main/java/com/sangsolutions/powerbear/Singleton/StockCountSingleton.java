package com.sangsolutions.powerbear.Singleton;

import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;

import java.util.ArrayList;
import java.util.List;

public class StockCountSingleton {
    private static StockCountSingleton stockCountSingleton;
    public List<ListProduct> list = new ArrayList<>();

    private StockCountSingleton(){}

    public static StockCountSingleton getInstance(){
        if(stockCountSingleton==null){
            stockCountSingleton= new StockCountSingleton();
        }
        return stockCountSingleton;
    }

    public List<ListProduct> getList(){return list;}
    public void setList(List<ListProduct> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
