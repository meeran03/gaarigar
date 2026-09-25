package com.gianteyes.gaarigarapp.screens.home_screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarapp.MainActivity;
import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

public class ProfileScreen extends Fragment {
    NavController navController;
    MaterialCardView serviceHistoryButton;
    MaterialCardView logOutButton;
    LoginResponseModel user;
    MaterialTextView name;
    MaterialTextView phoneNumber;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        this.user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_screen, container, false);
    }


    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        navController = Navigation.findNavController(view);

        super.onViewCreated(view, savedInstanceBundle);
        view.findViewById(R.id.updateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_profileScreen_to_updateProfile);
            }
        });
        name = view.findViewById(R.id.userName);
        phoneNumber = view.findViewById(R.id.userPhoneNumber);
        name.setText(this.user.getFirstName() + " " + this.user.getLastName());
        phoneNumber.setText(this.user.getPhone());
        serviceHistoryButton = view.findViewById(R.id.serviceHistoryButton);
        logOutButton = view.findViewById(R.id.logOutButton);
        serviceHistoryButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileScreen_to_pastHistory);
        });
        logOutButton.setOnClickListener(v -> {
            Toast.makeText(this.getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        });
    }
}
