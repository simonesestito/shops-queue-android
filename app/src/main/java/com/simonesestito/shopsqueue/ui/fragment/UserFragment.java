package com.simonesestito.shopsqueue.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.simonesestito.shopsqueue.databinding.UserFragmentBinding;

public class UserFragment extends AbstractAppFragment<UserFragmentBinding> {
    @Override
    protected UserFragmentBinding onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container) {
        return UserFragmentBinding.inflate(layoutInflater, container, false);
    }
}
