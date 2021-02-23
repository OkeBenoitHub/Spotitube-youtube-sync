package com.syncrotube.www.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.syncrotube.www.R;
import com.syncrotube.www.databinding.FragmentSyncTabBinding;

public class SyncTabFragment extends Fragment {
    private FragmentSyncTabBinding mFragmentSyncTabBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSyncTabBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sync_tab, container, false);
        return mFragmentSyncTabBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentSyncTabBinding = null;
    }
}
