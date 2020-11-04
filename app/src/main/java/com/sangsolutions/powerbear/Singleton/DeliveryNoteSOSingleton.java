package com.sangsolutions.powerbear.Singleton;

import java.util.ArrayList;
import java.util.List;

public class DeliveryNoteSOSingleton {
    private static DeliveryNoteSOSingleton deliveryNoteSOSingleton;
    public List<String> list = new ArrayList<>();

    private DeliveryNoteSOSingleton(){}

    public static DeliveryNoteSOSingleton getInstance(){
        if(deliveryNoteSOSingleton ==null){
            deliveryNoteSOSingleton = new DeliveryNoteSOSingleton();
        }
        return deliveryNoteSOSingleton;
    }

    public List<String> getList(){return list;}
    public void setList(List<String> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}
