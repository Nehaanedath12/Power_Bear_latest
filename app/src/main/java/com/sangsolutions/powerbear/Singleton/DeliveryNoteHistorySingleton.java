package com.sangsolutions.powerbear.Singleton;

import com.sangsolutions.powerbear.Adapter.DliveryNoteHistoryAdapter.DeliveryNoteHistory;

import java.util.ArrayList;
import java.util.List;

public class DeliveryNoteHistorySingleton {
    private static
    DeliveryNoteHistorySingleton deliveryNoteHistorySingleton;
    public List<DeliveryNoteHistory> list = new ArrayList<>();

    private DeliveryNoteHistorySingleton(){}

    public static DeliveryNoteHistorySingleton getInstance(){
        if(deliveryNoteHistorySingleton ==null){
            deliveryNoteHistorySingleton = new DeliveryNoteHistorySingleton();
        }
        return deliveryNoteHistorySingleton;
    }

    public List<DeliveryNoteHistory> getList(){return list;}
    public void setList(List<DeliveryNoteHistory> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
