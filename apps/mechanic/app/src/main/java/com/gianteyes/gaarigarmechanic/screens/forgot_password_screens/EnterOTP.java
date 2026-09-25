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

public class EnterOTP extends Fragment {

    NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enter_otp, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        this.navController = Navigation.findNavController(view);
        getView().findViewById(R.id.verifyOTPButton).setOnClickListener(v -> {
            this.navController.navigate(R.id.action_enterOTP2_to_enterNewPassword);
        });
    }
}
