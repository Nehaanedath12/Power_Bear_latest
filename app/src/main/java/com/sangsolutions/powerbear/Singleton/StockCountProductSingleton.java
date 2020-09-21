package com.sangsolutions.powerbear.Singleton;

import com.sangsolutions.powerbear.Adapter.ListProduct2.ListProduct;

import java.util.ArrayList;
import java.util.List;

public class StockCountProductSingleton {
    private static StockCountProductSingleton stockCountProductSingleton;
    public List<ListProduct> list = new ArrayList<>();

    private StockCountProductSingleton(){}

    public static StockCountProductSingleton getInstance(){
        if(stockCountProductSingleton ==null){
            stockCountProductSingleton = new StockCountProductSingleton();
        }
        return stockCountProductSingleton;
    }

    public List<ListProduct> getList(){return list;}
    public void setList(List<ListProduct> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
