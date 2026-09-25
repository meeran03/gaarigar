package com.gianteyes.gaarigarmechanic.screens.forgot_password_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarmechanic.R;

public class EnterNewPassword extends Fragment {

    NavController navController;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_new_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceBundle) {
        navController = Navigation.findNavController(view);
        this.getView().findViewById(R.id.newPassContinueButton).setOnClickListener(v -> {
            navController.navigate(R.id.action_enterNewPassword_to_loginScreen);
        });
    }
}
