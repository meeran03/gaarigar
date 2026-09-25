package com.gianteyes.gaarigarmechanic.screens.auth_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.RegisterRequestModel;
import com.gianteyes.gaarigarmechanic.models.RegisterResponseModel;
import com.gianteyes.gaarigarmechanic.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterScreen extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_screen, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        this.getView().findViewById(R.id.register_button).setOnClickListener(v -> {
            registerCustomer(v);
        });

    }


    public void registerCustomer(View v) {
        String firstName = ((TextInputEditText) getView().findViewById(R.id.firstNameInputField)).getText().toString();
        String lastname = ((TextInputEditText) getView().findViewById(R.id.lastNameInputField)).getText().toString();
        String phone = ((TextInputEditText) getView().findViewById(R.id.phoneInputField)).getText().toString();
        String password = ((TextInputEditText) getView().findViewById(R.id.passwordInputField)).getText().toString();
        String confirmPassword = ((TextInputEditText) getView().findViewById(R.id.confirmPasswordInputField)).getText().toString();
        String email = ((TextInputEditText) getView().findViewById(R.id.emailInputField)).getText().toString();

        RegisterRequestModel request = new RegisterRequestModel(firstName, lastname, phone, password, email);

        APIClient.getInstance().getMyApi().registerCustomer(request).enqueue(new Callback<RegisterResponseModel>() {
            @Override
            public void onResponse(Call<RegisterResponseModel> call, Response<RegisterResponseModel> response) {
                if (response.isSuccessful()) {
                    RegisterResponseModel registerResponseModel = response.body();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("phone", registerResponseModel.getPhone()).apply();
                    sharedPreferences.edit().putString("firstName", registerResponseModel.getFirstName()).apply();
                    sharedPreferences.edit().putString("lastName", registerResponseModel.getLastName()).apply();
                    sharedPreferences.edit().putString("userType", registerResponseModel.getUserType()).apply();
                    sharedPreferences.edit().putString("password", registerResponseModel.getPassword()).apply();
                    Toast.makeText(getActivity(), "Registered", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
