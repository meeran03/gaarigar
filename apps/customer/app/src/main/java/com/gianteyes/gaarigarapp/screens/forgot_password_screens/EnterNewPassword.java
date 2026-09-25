package com.gianteyes.gaarigarapp.screens.forgot_password_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.ChangePasswordOTPModel;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;

public class EnterNewPassword extends Fragment {

    NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enter_new_password, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        this.navController = Navigation.findNavController(view);
        this.getView().findViewById(R.id.newPassContinueButton).setOnClickListener(v -> {
            final String phoneNumber = this.getArguments().getString("phoneNumber");
            final String token = this.getArguments().getString("token");
            final ChangePasswordOTPModel model = new ChangePasswordOTPModel();
            final String password = ((TextInputEditText) this.getView().findViewById(R.id.passwordInputField)).getText().toString();
            final String confirmPassword = ((TextInputEditText) this.getView().findViewById(R.id.confirmPasswordInputField)).getText().toString();
            if (!password.equals((confirmPassword))) {
                Toast.makeText(this.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            // regex for strong password
            if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")) {
                Toast.makeText(this.getContext(), "Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter and one special character", Toast.LENGTH_SHORT).show();
                return;
            }
            model.setPhoneNumber(phoneNumber);
            model.setNewToken(token);
            model.setNewPassword(password);
            APIClient.getInstance().getMyApi().changePassword(model).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(final retrofit2.Call<Void> call, final retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EnterNewPassword.this.getContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        EnterNewPassword.this.navController.navigate(R.id.action_enterNewPassword_to_loginScreen);
                    } else {
                        Toast.makeText(EnterNewPassword.this.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(final retrofit2.Call<Void> call, final Throwable t) {
                    Toast.makeText(EnterNewPassword.this.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
