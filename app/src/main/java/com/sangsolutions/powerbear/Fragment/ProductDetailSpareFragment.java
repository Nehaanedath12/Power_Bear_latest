package com.sangsolutions.powerbear.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONObject;

public class ProductDetailSpareFragment extends Fragment {
    String iProduct;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.product_detail_spare_fragment,container,false);
        RecyclerView recyclerView=view.findViewById(R.id.rv_spare);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        iProduct = getArguments().getString("iProduct");

        AndroidNetworking.get("http://"+new Tools().getIP(requireActivity())+ URLs.GetProductDetails)
                .addQueryParameter("iProduct"+iProduct)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        getSpareParts(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());

                    }
                });
        return view;
    }

    private void getSpareParts(JSONObject response) {

    }

}
