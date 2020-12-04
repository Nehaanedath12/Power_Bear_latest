package com.sangsolutions.powerbear.Fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.Adapter.ProductDetailsSpareAdpater.SpareAdapter;
import com.sangsolutions.powerbear.Adapter.ProductDetailsSpareAdpater.SpareClass;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailSpareFragment extends Fragment {
    List<SpareClass> spareClassList;
    String iProduct;
    SpareAdapter spareAdapter;
    RecyclerView recyclerView;
    ImageView mProgressBar;
    private AnimationDrawable animationDrawable;
    FrameLayout empty_frame;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.product_detail_spare_fragment,container,false);
        mProgressBar=view.findViewById(R.id.main_progress);
        empty_frame=view.findViewById(R.id.empty_frame);
        mProgressBar.setBackgroundResource(R.drawable.loading);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();

        recyclerView=view.findViewById(R.id.rv_spare);
        spareClassList=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        spareAdapter=new SpareAdapter(getContext(),spareClassList);
        iProduct = getArguments().getString("iProduct");

        mProgressBar.setVisibility(View.VISIBLE);
        animationDrawable.start();


        AndroidNetworking.get("http://"+new Tools().getIP(requireActivity())+ URLs.GetProductDetails)
                .addQueryParameter("iProduct",iProduct)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        getSpareParts(response);
                        recyclerView.setAdapter(spareAdapter);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.toString());
                        mProgressBar.setVisibility(View.INVISIBLE);
                        animationDrawable.stop();

                    }
                });
        return view;
    }

    private void getSpareParts(JSONObject response) {
        mProgressBar.setVisibility(View.INVISIBLE);
        animationDrawable.stop();
        try {
            JSONArray jsonArray = new JSONArray(response.getString("spares"));
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SpareClass spareClass=new SpareClass(jsonObject.getString("Imagescount"),
                            jsonObject.getString("Filescount"),
                            jsonObject.getString("iId"),
                            jsonObject.getString("iProduct"),
                            jsonObject.getString("iPosition"),
                            jsonObject.getString("iSpareParts"),
                            jsonObject.getString("Name"),
                            jsonObject.getString("Code2"));
                    spareClassList.add(spareClass);

                }
            }
            else {
                empty_frame.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mProgressBar.setVisibility(View.INVISIBLE);
            animationDrawable.stop();
        }
    }

}
