package com.simonesestito.shopsqueue.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.databinding.AdminFragmentBinding;
import com.simonesestito.shopsqueue.databinding.OwnerFragmentBinding;

public class OwnerFragment extends AbstractAppFragment<OwnerFragmentBinding> {
    @Override
    protected OwnerFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return OwnerFragmentBinding.inflate(layoutInflater, container, false);
    }
}
