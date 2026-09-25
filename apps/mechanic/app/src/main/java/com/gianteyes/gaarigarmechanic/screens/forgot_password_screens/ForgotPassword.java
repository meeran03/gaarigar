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

public class ForgotPassword extends Fragment {
    NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        this.navController = Navigation.findNavController(view);
        getView().findViewById(R.id.forgotPass_sendButton).setOnClickListener(v -> {
            this.navController.navigate(R.id.action_forgotPassword_to_enterOTP2);
        });
    }

}
