package com.e.wallzhub.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e.wallzhub.Constants.Adapters.ViewPagerAdapter;
import com.e.wallzhub.Dashbaord.Dashboard;
import com.e.wallzhub.R;
import com.google.android.material.tabs.TabLayout;

public class FragmentParent extends Fragment {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private View mView;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_parent, container, false);
        adapter = new ViewPagerAdapter(getFragmentManager(), getContext());

        mViewPager = mView.findViewById(R.id.viewPager);
        tabLayout = mView.findViewById(R.id.tablayout_main);

        mViewPager.setAdapter(adapter);


        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return mView;
    }

    public void addPage(String collection) {
        Bundle bundle = new Bundle();
        bundle.putString("title", collection);
        FragmentChild fragmentChild = new FragmentChild();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, collection);
        adapter.notifyDataSetChanged();
        for (int i = 0; i < Dashboard.collectionsMain.size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setText(Dashboard.collectionsMain.get(i).getCollection());
        }
        mViewPager.setCurrentItem(0);
    }
}