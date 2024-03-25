package com.example.thinktwice.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thinktwice.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Get a reference to the TextView
        TextView textView = binding.textHome;

        // Set initial text from the ViewModel
        homeViewModel.getText().observe(getViewLifecycleOwner(), newText -> textView.setText(newText));

        // Get a reference to the Button
        Button button = binding.button;

        // Set OnClickListener on the Button
        button.setOnClickListener(v -> updateText("New Text"));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateText(String newText) {
        homeViewModel.setText(newText);
    }
}
