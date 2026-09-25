package com.gianteyes.gaarigarmechanic.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarmechanic.R;

public class Welcome extends Fragment {

    Button loginButton;
    Button signUpButton;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        this.loginButton = this.getView().findViewById(R.id.login_button_go);
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Welcome.this.navController.navigate(R.id.action_welcome_to_loginScreen);
            }
        });

        this.signUpButton = this.getView().findViewById(R.id.signUp_button);
        this.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Welcome.this.navController.navigate(R.id.action_welcome_to_registerScreen);
            }
        });
    }

}
