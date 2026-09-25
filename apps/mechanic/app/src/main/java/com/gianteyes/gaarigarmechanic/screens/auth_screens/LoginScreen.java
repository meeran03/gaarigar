package com.gianteyes.gaarigarmechanic.screens.auth_screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.LoginRequestModel;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
import com.gianteyes.gaarigarmechanic.screens.HomeActivity;
import com.gianteyes.gaarigarmechanic.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends Fragment {
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    // store in shared preference'
                    SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
                    // get fcm token
                    if (!com.google.firebase.FirebaseApp.getApps(requireContext()).isEmpty()) FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast

                        sharedPreferences.edit().putString("fcmToken", token).apply();
                    });
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });
    private NavController navController;

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (this.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                this.requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                this.requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
            // get fcm token
            if (!com.google.firebase.FirebaseApp.getApps(requireContext()).isEmpty()) FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                // Log and toast

                sharedPreferences.edit().putString("fcmToken", token).apply();
            });
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_screen, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        navController = Navigation.findNavController(view);
        getView().findViewById(R.id.login_button).setOnClickListener(v -> {
            loginCustomer(v);
        });
        getView().findViewById(R.id.signUpButton).setOnClickListener(v -> {
            navController.navigate(R.id.action_loginScreen_to_registerScreen);
        });
        getView().findViewById(R.id.forgotPasswordButton).setOnClickListener(v -> {
            navController.navigate(R.id.action_loginScreen_to_forgotPassword);
        });
        this.askNotificationPermission();
    }

    // function that will be called when login button is clicked
    public void loginCustomer(View v) {
        String phone = "+92" + ((TextInputEditText) getView().findViewById(R.id.phoneInputField)).getText().toString();
        String password = ((TextInputEditText) getView().findViewById(R.id.passwordInputField)).getText().toString();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        // get the fcm token from shared preferences
        String fcmToken = sharedPreferences.getString("fcmToken", "");
        LoginRequestModel request = new LoginRequestModel(phone, password, fcmToken);
        APIClient.getInstance().getMyApi().loginCustomer(request).enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    LoginResponseModel loginResponseModel = response.body();
                    sharedPreferences.edit().putString("accessToken", loginResponseModel.getAccessToken()).apply();
                    sharedPreferences.edit().putString("refreshToken", loginResponseModel.getRefreshToken()).apply();
                    sharedPreferences.edit().putString("phone", phone).apply();
                    final Gson gson = new Gson();
                    final String json = gson.toJson(loginResponseModel);
                    sharedPreferences.edit().putString("user", json).apply();
                    sharedPreferences.edit().putString("id", loginResponseModel.getId().toString()).apply();
                    APIClient.getInstance().setToken(loginResponseModel.getAccessToken());
                    Toast.makeText(LoginScreen.this.getActivity(), "Logged In", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(LoginScreen.this.getActivity(), "Invalid phone or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Toast.makeText(LoginScreen.this.getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
