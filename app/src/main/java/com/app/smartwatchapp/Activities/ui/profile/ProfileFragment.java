package com.app.smartwatchapp.Activities.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.smartwatchapp.R;
import com.app.smartwatchapp.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    View root;
    Context context;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        ((TextView) root.findViewById(R.id.tv_welcome)).setText("Profile");
        context = getActivity();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}