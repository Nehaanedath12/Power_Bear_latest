package com.sangsolutions.powerbear.Adapter.ViewPagerAdapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> FragmentList = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentList.get(position);
    }

    @Override
    public int getCount() {
        return FragmentList.size();
    }

    public void add(Fragment fragment, String title,String warehouse,String EditMode,String voucherNo) {
        Bundle bundle = new Bundle();
        bundle.putString("warehouse",warehouse);
        bundle.putString("EditMode",EditMode);
        bundle.putString("voucherNo",voucherNo);
        fragment.setArguments(bundle);
        FragmentList.add(fragment);
        stringList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }

}
