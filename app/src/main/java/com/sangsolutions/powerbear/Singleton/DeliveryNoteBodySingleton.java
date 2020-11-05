package com.sangsolutions.powerbear.Singleton;



import com.sangsolutions.powerbear.Adapter.DeliveryNoteBodyAdapter.DeliveryNoteBody;

import java.util.ArrayList;
import java.util.List;

public class DeliveryNoteBodySingleton {
    private static
    DeliveryNoteBodySingleton deliveryNoteBodySingleton;
    public List<DeliveryNoteBody> list = new ArrayList<>();

    private DeliveryNoteBodySingleton(){}

    public static DeliveryNoteBodySingleton getInstance(){
        if(deliveryNoteBodySingleton ==null){
            deliveryNoteBodySingleton = new DeliveryNoteBodySingleton();
        }
        return deliveryNoteBodySingleton;
    }

    public List<DeliveryNoteBody> getList(){return list;}
    public void setList(List<DeliveryNoteBody> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
