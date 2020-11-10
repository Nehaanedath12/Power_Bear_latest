package com.sangsolutions.powerbear.Fragment;


import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.sangsolutions.powerbear.Commons;
import com.sangsolutions.powerbear.PublicData;
import com.sangsolutions.powerbear.R;
import com.sangsolutions.powerbear.StockCount;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class SyncFragment extends Fragment {
    Handler handler;
    private AnimationDrawable animationDrawable;
    private ImageView mProgressBar;
    Animation fadeIn,fadeOut;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView tv_status;TextView txt;
    boolean Blink = true, goodsShown=false, DeliveryShown =false, StockShown =false;
    private void blinkTextView(View view) {
        final Handler handler = new Handler();
        new Thread(() -> {
            int timeToBlink = 800;
            try {
                Thread.sleep(timeToBlink);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                if(txt==null) {
                    txt = view.findViewById(R.id.title_text);
                }
                if(Blink) {
                    if (txt.getVisibility() == View.VISIBLE) {
                        txt.startAnimation(fadeOut);
                        txt.setVisibility(View.INVISIBLE);
                    } else {
                        txt.startAnimation(fadeIn);
                        txt.setVisibility(View.VISIBLE);
                    }
                }
                blinkTextView(view);
            });
        }).start();
    }


    public void CloseFragment(){
        new Handler().postDelayed(() -> {
            try {
                PublicData.Syncing = false;
                requireActivity().getSupportFragmentManager().popBackStack();
            }catch (Exception e){
                e.printStackTrace();
            }
        },2000);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = LayoutInflater.from(requireActivity()).inflate(R.layout.sync_fragment,container,false);
        mProgressBar = view.findViewById(R.id.main_progress);
        mProgressBar.setBackgroundResource(R.drawable.loading);
        fadeIn = AnimationUtils.loadAnimation(requireActivity(), android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(requireActivity(), android.R.anim.fade_out);
        preferences = getActivity().getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();
        txt = view.findViewById(R.id.title_text);
        tv_status = view.findViewById(R.id.status);
        blinkTextView(view);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    mProgressBar.setVisibility(View.VISIBLE);
                    animationDrawable.start();
                    handler.postDelayed(this, 300);
                    if (Objects.equals(preferences.getString(Commons.WAREHOUSE_FINISHED, "false"), "true")
                            && Objects.equals(preferences.getString(Commons.PENDING_PO_FINISHED, "false"), "true")
                            && Objects.equals(preferences.getString(Commons.PENDING_SO_FINISHED, "false"), "true")
                            && Objects.equals(preferences.getString(Commons.REMARKS_FINISHED, "false"), "true")
                            && Objects.equals(preferences.getString(Commons.PRODUCT_FINISHED, "false"), "true")
                            && (Objects.equals(preferences.getString(Commons.STOCK_COUNT_FINISHED, "false"), "true") || Objects.equals(preferences.getString(Commons.STOCK_COUNT_FINISHED, "false"), "false"))
                            && (Objects.equals(preferences.getString(Commons.GOODS_RECEIPT_FINISHED, "false"), "true") || Objects.equals(preferences.getString(Commons.GOODS_RECEIPT_FINISHED, "false"), "false"))
                            && (Objects.equals(preferences.getString(Commons.DELIVERY_NOTE_FINISHED, "false"), "true") || Objects.equals(preferences.getString(Commons.DELIVERY_NOTE_FINISHED, "false"), "false"))

                    ) {
                        handler.removeCallbacksAndMessages(null);
                        animationDrawable.stop();
                        Glide.with(Objects.requireNonNull(getActivity())).load(R.raw.check).into(mProgressBar);
                        txt.setVisibility(View.VISIBLE);
                        Blink = false;
                        txt.setText("Done!");
                        CloseFragment();
                    }


                    if (Objects.equals(preferences.getString(Commons.GOODS_RECEIPT_FINISHED, "false"), "error")) {
                        if (!goodsShown) {
                            txt.setText("GRN Sync failed!");
                            tv_status.setText("GRN Sync failed!");
                        }
                        handler.removeCallbacksAndMessages(null);
                        animationDrawable.stop();
                        CloseFragment();
                        goodsShown = true;
                    }

                    if (Objects.equals(preferences.getString(Commons.DELIVERY_NOTE_FINISHED, "false"), "error")) {
                        if (!DeliveryShown) {
                            txt.setText("Delivery Note Sync failed!");
                            tv_status.setText("Delivery Note Sync failed!");
                        }
                        handler.removeCallbacksAndMessages(null);
                        animationDrawable.stop();
                        CloseFragment();
                        DeliveryShown = true;

                    }
                    if (Objects.equals(preferences.getString(Commons.STOCK_COUNT_FINISHED, "false"), "error")) {
                        if (!StockShown) {
                            txt.setText("Stock count Sync failed!");
                            tv_status.setText("Stock count Sync failed!");
                        }
                        handler.removeCallbacksAndMessages(null);
                        animationDrawable.stop();
                        CloseFragment();
                        StockShown = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        handler.postDelayed(r, 300);



        return view;
    }
}
