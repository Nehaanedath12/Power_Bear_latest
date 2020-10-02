package com.sangsolutions.powerbear.Fragment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProduct;
import com.sangsolutions.powerbear.Adapter.GoodsPOProductAdapter.GoodsPOProductAdapter;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBodyAdapter;
import com.sangsolutions.powerbear.Adapter.POListAdaptet.POList;
import com.sangsolutions.powerbear.Database.DatabaseHelper;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Singleton.GoodsReceiptPoSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoodsReceiptBodyFragment extends Fragment {
    FloatingActionButton add_fab;
    RecyclerView rv_products;
    // to load main recyclerView
    GoodsReceiptBodyAdapter goodsReceiptBodyAdapter;
    List<GoodsReceiptBody> listMain;

    //To load Alert With product recyclerView
    GoodsPOProductAdapter goodsPOProductAdapter;
    List<GoodsPOProduct> listPOProducts;

    DatabaseHelper helper;

    boolean selection_active = false;
    // methods for selecting product from DocNo
    public void closeSelection(){
        goodsPOProductAdapter.clearSelections();
        selection_active = false;
    }
    private void toggleSelection(int position) {
        goodsPOProductAdapter.toggleSelection(position);
        int count = goodsPOProductAdapter.getSelectedItemCount();
        if(count==0){
            closeSelection();
        }
    }
    private void enableActionMode(int position) {
        toggleSelection(position);
    }




    public void setMainRecyclerView(){
     if(goodsPOProductAdapter.getSelectedItemCount()>0){
         List<Integer> listSelected = goodsPOProductAdapter.getSelectedItems();
         for(int i=0;i<listSelected.size();i++){
             for (int j=0;j<listPOProducts.size();j++){


                 if(listSelected.get(i)==j){
                     listMain.add(new GoodsReceiptBody(
                             listPOProducts.get(j).getDocNo(),
                             "",
                             helper.GetBarcodeFromIProduct(listPOProducts.get(j).getProduct()),
                             listPOProducts.get(j).getPOQty(),
                             "0",
                             listPOProducts.get(j).getUnit(),
                             "",
                             "",
                             "",
                             "",
                             "",
                             "",
                             ""
                     ));
                 }
             }
             if(i+1==listSelected.size()){
                 goodsReceiptBodyAdapter.notifyDataSetChanged();
                 closeSelection();
             }
         }
     }

    }



    //Alert for selecting products from DocNo
    public void POProductSelectAlert(){
        List<POList> list = GoodsReceiptPoSingleton.getInstance().getList();
        List<String> ListPONos = new ArrayList<>();
        if(list!=null&&list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                ListPONos.add(list.get(i).getDocNo());
            }
            try {
            Cursor cursor = helper.GetGoodsPOProdcut(ListPONos);

            if(cursor !=null && cursor.moveToFirst()){
                listPOProducts.clear();
                for(int i = 0; i<cursor.getCount();i++){
                    listPOProducts.add(new GoodsPOProduct(
                       cursor.getString(cursor.getColumnIndex("DocNo")),
                            cursor.getString(cursor.getColumnIndex("Name")),
                            cursor.getString(cursor.getColumnIndex("Product")),
                            cursor.getString(cursor.getColumnIndex("Qty")),
                            cursor.getString(cursor.getColumnIndex("Unit"))
                    ));
                    cursor.moveToNext();

                    if(cursor.getCount()==i+1){
                        goodsPOProductAdapter.notifyDataSetChanged();
                    }
                }
            }else {
                Log.d("list","cursor is empty");
            }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    View view = LayoutInflater.from(getActivity()).inflate(R.layout.goods_product_select_alert,null,false);
        Button btn_apply = view.findViewById(R.id.apply);
        RecyclerView rv_product_select = view.findViewById(R.id.rv_product_selection);
        ImageView ic_close = view.findViewById(R.id.close);

        rv_product_select.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_product_select.setAdapter(goodsPOProductAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodsPOProductAdapter.getSelectedItemCount()<=0){
                    Toast.makeText(getActivity(), "Select at least one product to add!", Toast.LENGTH_SHORT).show();
                }else {
                    setMainRecyclerView();
                    alertDialog.dismiss();
                }
            }
        });

  goodsPOProductAdapter.setOnClickListener(new GoodsPOProductAdapter.OnClickListener() {
      @Override
      public void onItemClick(int pos) {
          if (selection_active) {
              enableActionMode(pos);
          } else {
              selection_active = true;
              enableActionMode(pos);
          }
      }
  });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.good_reseipt_body_fragment,container,false);
       add_fab = view.findViewById(R.id.fab_controller);
       rv_products = view.findViewById(R.id.rv_product);
       listMain = new ArrayList<>();
       listPOProducts = new ArrayList<>();

       goodsReceiptBodyAdapter = new GoodsReceiptBodyAdapter(requireActivity(), listMain);
        goodsPOProductAdapter = new GoodsPOProductAdapter(listPOProducts,requireActivity());

        helper = new DatabaseHelper(requireActivity());

       rv_products.setLayoutManager(new LinearLayoutManager(requireActivity()));
       rv_products.setAdapter(goodsReceiptBodyAdapter);
       add_fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               POProductSelectAlert();
           }
       });
       return view;
    }
}
