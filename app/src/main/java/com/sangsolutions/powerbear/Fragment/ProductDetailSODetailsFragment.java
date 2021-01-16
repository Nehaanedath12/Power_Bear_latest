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
import com.sangsolutions.powerbear.Adapter.Report_SO_PO_Product_Details.SO_PO_Details;
import com.sangsolutions.powerbear.Adapter.Report_SO_PO_Product_Details.SO_PO_DetailsAdapter;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailSODetailsFragment extends Fragment {

    List<SO_PO_Details> so_PO_detailsList;
    String iProduct;
    SO_PO_DetailsAdapter detailsAdapter;
    RecyclerView recyclerView;
    ImageView mProgressBar;
    private AnimationDrawable animationDrawable;
    FrameLayout empty_frame;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.product_detail_so_details_fragment,container,false);
        mProgressBar=view.findViewById(R.id.main_progress);
        empty_frame=view.findViewById(R.id.empty_frame);
        mProgressBar.setBackgroundResource(R.drawable.loading);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();

        so_PO_detailsList =new ArrayList<>();
        recyclerView=view.findViewById(R.id.rv_so_details);
        iProduct = getArguments().getString("iProduct");
        detailsAdapter=new SO_PO_DetailsAdapter(requireActivity(), so_PO_detailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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
                        getSODetails(response);
                        recyclerView.setAdapter(detailsAdapter);
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

    private void getSODetails(JSONObject response) {

        mProgressBar.setVisibility(View.INVISIBLE);
        animationDrawable.stop();
        try {
            JSONArray jsonArray = new JSONArray(response.getString("sodetails"));
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SO_PO_Details so_PO_details =new SO_PO_Details(jsonObject.getString("Cusomer"),
                            jsonObject.getString("VoucherNo"),
                            jsonObject.getString("VoucherDate"),
                            jsonObject.getDouble("Qty"));
                    so_PO_detailsList.add(so_PO_details);

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