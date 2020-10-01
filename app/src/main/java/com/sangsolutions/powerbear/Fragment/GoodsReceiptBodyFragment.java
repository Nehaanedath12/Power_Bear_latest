package com.sangsolutions.powerbear.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBody;
import com.sangsolutions.powerbear.Adapter.GoodsReceiptBodyAdapter.GoodsReceiptBodyAdapter;
import com.sangsolutions.powerbear.R;

import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptBodyFragment extends Fragment {
    FloatingActionButton add_fab;
    RecyclerView rv_products;
    GoodsReceiptBodyAdapter goodsReceiptBodyAdapter;
    List<GoodsReceiptBody> list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.good_reseipt_body_fragment,container,false);
       add_fab = view.findViewById(R.id.fab_controller);
       rv_products = view.findViewById(R.id.rv_product);
       list = new ArrayList<>();
       goodsReceiptBodyAdapter = new GoodsReceiptBodyAdapter(requireActivity(),list);

       rv_products.setLayoutManager(new LinearLayoutManager(requireActivity()));
       rv_products.setAdapter(goodsReceiptBodyAdapter);
       add_fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               list.clear();
            for(int i = 0;i<100;i++){
                list.add(new GoodsReceiptBody("data","data","data","data","data","data","data","data","data","data","data","data","data"));
                goodsReceiptBodyAdapter.notifyDataSetChanged();

            }
           }
       });
       return view;
    }
}
