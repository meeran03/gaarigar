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
import com.gianteyes.gaarigarapp.models.SendOTPRequestModel;
import com.gianteyes.gaarigarapp.models.SendOTPResponse;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class ForgotPassword extends Fragment {
    NavController navController;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceBundle) {
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.forgotPass_sendButton).setOnClickListener(v -> {
            final TextInputEditText forgotPass_phoneNumber = view.findViewById(R.id.phoneInputField);
            final String phoneNumber = "+92" + forgotPass_phoneNumber.getText().toString();
            if (phoneNumber.equals("+92")) {
                Toast.makeText(this.getContext(), "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            final SendOTPRequestModel sendOTPRequestModel = new SendOTPRequestModel(phoneNumber);
            APIClient.getInstance().getMyApi().sendOTP(sendOTPRequestModel).enqueue(new retrofit2.Callback<SendOTPResponse>() {
                @Override
                public void onResponse(final retrofit2.Call<SendOTPResponse> call, final retrofit2.Response<SendOTPResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this.getContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        final Bundle bundle = new Bundle();
                        bundle.putString("phoneNumber", phoneNumber);
                        ForgotPassword.this.navController.navigate(R.id.action_forgotPassword_to_enterOTP2, bundle);
                    } else {
                        try {
                            final JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(ForgotPassword.this.getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (final Exception e) {
                            Toast.makeText(ForgotPassword.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(final retrofit2.Call<SendOTPResponse> call, final Throwable t) {
                    Toast.makeText(ForgotPassword.this.getContext(), "Error sending OTP", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
