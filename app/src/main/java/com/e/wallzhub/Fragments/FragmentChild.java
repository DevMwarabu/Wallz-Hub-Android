package com.e.wallzhub.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e.wallzhub.R;

public class FragmentChild extends Fragment {
    private View mView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_child, container, false);
        //getting bandle arguments
        Bundle bundle = getArguments();
        title = bundle.getString("title");

        mRecyclerView = mView.findViewById(R.id.recycler_main);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_main);

        return mView;
    }
}