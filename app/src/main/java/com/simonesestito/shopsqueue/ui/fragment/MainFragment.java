package com.simonesestito.shopsqueue.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.simonesestito.shopsqueue.R;
import com.simonesestito.shopsqueue.lifecycle.viewmodel.LoginViewModel;
import com.simonesestito.shopsqueue.model.dto.input.AuthResponse;

/**
 * Main fragment
 * It shows a loading indicator until the activity navigates away
 */
public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }
}
