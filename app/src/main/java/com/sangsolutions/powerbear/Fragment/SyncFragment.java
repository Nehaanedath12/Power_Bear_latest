package com.sangsolutions.powerbear.Fragment;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.sangsolutions.powerbear.R;

public class SyncFragment extends Fragment {
    Handler handler;
    private AnimationDrawable animationDrawable;
    private ImageView mProgressBar;
    Animation fadeIn,fadeOut;

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
                TextView txt = view.findViewById(R.id.title_text);
                if (txt.getVisibility()==View.VISIBLE){
                    txt.startAnimation(fadeOut);
                    txt.setVisibility(View.INVISIBLE);
                } else{
                    txt.startAnimation(fadeIn);
                    txt.setVisibility(View.VISIBLE);
                }
                blinkTextView(view);
            });
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = LayoutInflater.from(requireActivity()).inflate(R.layout.sync_fragment,container,false);
        mProgressBar = view.findViewById(R.id.main_progress);
        mProgressBar.setBackgroundResource(R.drawable.loading);
         fadeIn = AnimationUtils.loadAnimation(requireActivity(), android.R.anim.fade_in);
         fadeOut = AnimationUtils.loadAnimation(requireActivity(), android.R.anim.fade_out);

        blinkTextView(view);
        animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

                    mProgressBar.setVisibility(View.VISIBLE);
                    animationDrawable.start();
                    handler.postDelayed(this, 1000);

                   //LoadCustomer();
                    handler.removeCallbacksAndMessages(null);

            }
        };
        handler.postDelayed(r, 1000);



        return view;
    }
}
