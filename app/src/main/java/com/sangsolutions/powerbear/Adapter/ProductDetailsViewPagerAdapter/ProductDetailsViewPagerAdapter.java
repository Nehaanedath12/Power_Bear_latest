package com.sangsolutions.powerbear.Adapter.ProductDetailsViewPagerAdapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sangsolutions.powerbear.Fragment.ProductDetailAttachmentsFragment;
import com.sangsolutions.powerbear.Fragment.ProductDetailSpareFragment;
import com.sangsolutions.powerbear.Fragment.ProductDetailsMainFragment;
import com.sangsolutions.powerbear.ProductDetails;

public class ProductDetailsViewPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    int totalTabs;
    String iProduct;
    public ProductDetailsViewPagerAdapter(FragmentManager fm, int behavior, ProductDetails context, int totalTabs, String iProduct) {
        super(fm, behavior);
        this.context = context;
        this.totalTabs = totalTabs;
        this.iProduct=iProduct;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("iProduct", iProduct);

        switch (position) {
            case 0:
                ProductDetailsMainFragment detailOtherFragment = new ProductDetailsMainFragment();
                detailOtherFragment.setArguments(bundle);
                return detailOtherFragment;


            case 1:
                ProductDetailSpareFragment spareFragment = new ProductDetailSpareFragment();
                spareFragment.setArguments(bundle);
                return spareFragment;

            case 2:

                ProductDetailAttachmentsFragment detailMainFragment = new ProductDetailAttachmentsFragment();
                detailMainFragment.setArguments(bundle);
                return detailMainFragment;

        }

        return null;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}