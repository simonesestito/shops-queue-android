package com.simonesestito.shopsqueue.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

/**
 * Base class for app fragments
 * It uses ViewBinding
 */
public abstract class AbstractAppFragment<T extends ViewBinding> extends Fragment {
    private T viewBinding;

    protected abstract T onCreateViewBinding(LayoutInflater layoutInflater, @Nullable ViewGroup container);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = onCreateViewBinding(inflater, container);
        return viewBinding.getRoot();
    }

    protected T getViewBinding() {
        return viewBinding;
    }
}
