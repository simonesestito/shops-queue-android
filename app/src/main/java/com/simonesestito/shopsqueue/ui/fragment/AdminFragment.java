package com.simonesestito.shopsqueue.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.databinding.AdminFragmentBinding;

public class AdminFragment extends AbstractAppFragment<AdminFragmentBinding> {
    @Override
    protected AdminFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return AdminFragmentBinding.inflate(layoutInflater, container, false);
    }
}
