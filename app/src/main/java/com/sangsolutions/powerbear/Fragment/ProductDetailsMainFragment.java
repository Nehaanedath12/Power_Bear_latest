package com.sangsolutions.powerbear.Fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.Tools;
import com.sangsolutions.powerbear.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProductDetailsMainFragment extends Fragment {
    String iProduct;
    Button productName,brand,color,material,code,productType,addDesc,manufacture;
    View view;
    FrameLayout emptyIcon;
    ImageView mProgressBar;
    private AnimationDrawable animationDrawable;
    CardView card_details;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=LayoutInflater.from(getContext()).inflate(R.layout.product_detail_other_fragment,container,false);
        productName=view.findViewById(R.id.product_name);
        brand=view.findViewById(R.id.brand);
        color=view.findViewById(R.id.color);
        material=view.findViewById(R.id.material);
        code=view.findViewById(R.id.code);
        productType=view.findViewById(R.id.product_type);
        addDesc=view.findViewById(R.id.desc);
        manufacture=view.findViewById(R.id.manufacture);
        emptyIcon=view.findViewById(R.id.empty_frame);
        mProgressBar =view.findViewById(R.id.main_progress);
        card_details=view.findViewById(R.id.card_details);

        mProgressBar.setBackgroundResource(R.drawable.loading);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
        iProduct = getArguments().getString("iProduct");

        Log.d("iProduct",iProduct+" other");

        mProgressBar.setVisibility(VISIBLE);
        animationDrawable.start();

        AndroidNetworking.get("http://"+new Tools().getIP(requireActivity())+ URLs.GetProductDetails)
                .addQueryParameter("iProduct",iProduct)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        mProgressBar.setVisibility(VISIBLE);
                        animationDrawable.start();
                        Log.d("response",response.toString());
                        asyncTask(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("response",anError.getErrorBody());
                        emptyIcon.setVisibility(VISIBLE);
                        animationDrawable.stop();
                        mProgressBar.setVisibility(INVISIBLE);

                    }
                });

        return view;
    }

    private void asyncTask(JSONObject response) {
        animationDrawable.stop();
        mProgressBar.setVisibility(View.INVISIBLE);
        card_details.setVisibility(VISIBLE);
        emptyIcon.setVisibility(INVISIBLE);
        try {
            JSONArray jsonArray=new JSONArray(response.getString("data"));
            if(jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                productName.setText(jsonObject.getString("Name"));
                                brand.setText(jsonObject.getString("BrandYH"));
                                color.setText(jsonObject.getString("ColourYH"));
                                material.setText(jsonObject.getString("MaterialYH"));
                                code.setText(jsonObject.getString("Code2"));
                                productType.setText(jsonObject.getString("ProductTypeYH"));
                                addDesc.setText(jsonObject.getString("AddDescriptionYH"));
                                manufacture.setText(jsonObject.getString("ManufacturerYH"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                emptyIcon.setVisibility(VISIBLE);
                            }
                        }
                    });
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            emptyIcon.setVisibility(VISIBLE);
        }
}
}
