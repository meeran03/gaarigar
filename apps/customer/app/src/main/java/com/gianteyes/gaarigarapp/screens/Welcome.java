package com.gianteyes.gaarigarapp.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarapp.R;

public class Welcome extends Fragment {

    Button loginButton;
    Button signUpButton;
    private NavController navController;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.navController = Navigation.findNavController(view);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("firstTimeApp", "").equals("true"))
            sharedPreferences.edit().putString("firstTimeApp", "false");
        else {
            navController.navigate(R.id.action_welcome_to_loginScreen);
        }

        loginButton = getView().findViewById(R.id.login_button_go);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_welcome_to_loginScreen);
            }
        });

        signUpButton = getView().findViewById(R.id.signUp_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_welcome_to_registerScreen);
            }
        });
    }

}
