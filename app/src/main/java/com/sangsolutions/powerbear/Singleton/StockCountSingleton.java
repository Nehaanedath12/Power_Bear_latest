package com.sangsolutions.powerbear.Singleton;

import com.sangsolutions.powerbear.Adapter.StockCountListAdapter.StockCountList;

import java.util.ArrayList;
import java.util.List;

public class StockCountSingleton {

    private static StockCountSingleton stockCountSingleton;
    public List<StockCountList> list = new ArrayList<>();

    private StockCountSingleton(){}

    public static StockCountSingleton getInstance(){
        if(stockCountSingleton ==null){
            stockCountSingleton = new StockCountSingleton();
        }
        return stockCountSingleton;
    }

    public List<StockCountList> getList(){return list;}
    public void setList(List<StockCountList> list){
        this.list = list;
    }
    public void clearList(){list.clear();
    }
}
