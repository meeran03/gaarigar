package com.gianteyes.gaarigarmechanic.screens.home_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

public class ProfileScreen extends Fragment {
    NavController navController;
    MaterialCardView serviceHistoryButton;
    LoginResponseModel user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_screen, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceBundle) {
        this.navController = Navigation.findNavController(view);
        super.onViewCreated(view, savedInstanceBundle);
        view.findViewById(R.id.updateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ProfileScreen.this.navController.navigate(R.id.action_profileScreen_to_updateProfile);
            }
        });
        this.serviceHistoryButton = view.findViewById(R.id.serviceHistoryButton);
        this.serviceHistoryButton.setOnClickListener(v -> {
            this.navController.navigate(R.id.pastHistory);
        });
    }
}
