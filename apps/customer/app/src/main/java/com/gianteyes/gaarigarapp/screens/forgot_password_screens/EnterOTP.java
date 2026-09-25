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

import com.chaos.view.PinView;
import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.VerifyOTPModel;
import com.gianteyes.gaarigarapp.models.VerifyOTPResponseDTO;
import com.gianteyes.gaarigarapp.services.APIClient;

public class EnterOTP extends Fragment {

    NavController navController;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enter_otp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceBundle) {
        navController = Navigation.findNavController(view);
        this.getView().findViewById(R.id.verifyOTPButton).setOnClickListener(v -> {
            final PinView pinView = view.findViewById(R.id.pin_view);
            final String pin = pinView.getText().toString();

            final String phoneNumber = this.getArguments().getString("phoneNumber");
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this.getContext(), "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pin.isEmpty() || pin.length() != 6) {
                Toast.makeText(this.getContext(), "Please enter a pin", Toast.LENGTH_SHORT).show();
                return;
            }
            final VerifyOTPModel verifyOTPModel = new VerifyOTPModel();
            verifyOTPModel.setPhoneNumber(phoneNumber);
            verifyOTPModel.setOTPMessage(pin);
            APIClient.getInstance().getMyApi().verifyOTP(verifyOTPModel).enqueue(new retrofit2.Callback<VerifyOTPResponseDTO>() {
                @Override
                public void onResponse(final retrofit2.Call<VerifyOTPResponseDTO> call, final retrofit2.Response<VerifyOTPResponseDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EnterOTP.this.getContext(), "Successful", Toast.LENGTH_SHORT).show();
                        final Bundle bundle = new Bundle();
                        bundle.putString("phoneNumber", phoneNumber);
                        bundle.putString("token", response.body().getSuccessMessage());
                        EnterOTP.this.navController.navigate(R.id.action_enterOTP2_to_enterNewPassword, bundle);
                    } else {
                        Toast.makeText(EnterOTP.this.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(final retrofit2.Call<VerifyOTPResponseDTO> call, final Throwable t) {
                    Toast.makeText(EnterOTP.this.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
