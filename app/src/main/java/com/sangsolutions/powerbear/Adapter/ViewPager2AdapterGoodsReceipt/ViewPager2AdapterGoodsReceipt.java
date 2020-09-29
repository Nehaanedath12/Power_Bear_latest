package com.sangsolutions.powerbear.Adapter.ViewPager2AdapterGoodsReceipt;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPager2AdapterGoodsReceipt extends FragmentStateAdapter {

    List<Fragment> list;
    public ViewPager2AdapterGoodsReceipt(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,List<Fragment> fragmentList) {
        super(fragmentManager, lifecycle);
        this.list = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
