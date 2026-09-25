package com.gianteyes.gaarigarapp.screens.auth_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.RegisterRequestModel;
import com.gianteyes.gaarigarapp.models.RegisterResponseModel;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

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
        String phone = "+92" + ((TextInputEditText) getView().findViewById(R.id.phoneInputField)).getText().toString();
        String password = ((TextInputEditText) getView().findViewById(R.id.passwordInputField)).getText().toString();
        String confirmPassword = ((TextInputEditText) getView().findViewById(R.id.confirmPasswordInputField)).getText().toString();
        String email = ((TextInputEditText) getView().findViewById(R.id.emailInputField)).getText().toString();
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }
        // regex for strong password
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")) {
            Toast.makeText(this.getContext(), "Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter and one special character", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequestModel request = new RegisterRequestModel(firstName, lastname, phone, password, email);

        APIClient.getInstance().getMyApi().registerCustomer(request).enqueue(new Callback<RegisterResponseModel>() {
            @Override
            public void onResponse(Call<RegisterResponseModel> call, Response<RegisterResponseModel> response) {
                if (response.isSuccessful()) {
                    RegisterResponseModel registerResponseModel = response.body();
                    Toast.makeText(getActivity(), "Registered", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponseModel> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
