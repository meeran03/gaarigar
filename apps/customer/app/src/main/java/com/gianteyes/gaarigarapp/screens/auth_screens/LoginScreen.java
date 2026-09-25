package com.gianteyes.gaarigarapp.screens.auth_screens;

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

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.LoginRequestModel;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.gianteyes.gaarigarapp.screens.HomeActivity;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends Fragment {
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    // store in shared preference'
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
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
                }
            });
    private NavController navController;

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
            // get fcm token
            if (!com.google.firebase.FirebaseApp.getApps(requireContext()).isEmpty()) FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new FCM registration token
                final String token = task.getResult();
                // Log and toast

                sharedPreferences.edit().putString("fcmToken", token).apply();
            });
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceBundle) {
        try {
            this.navController = Navigation.findNavController(view);
            this.getView().findViewById(R.id.login_button).setOnClickListener(v -> {
                this.loginCustomer(v);
            });
            this.getView().findViewById(R.id.signUpButton).setOnClickListener(v -> {
                this.navController.navigate(R.id.action_loginScreen_to_registerScreen);
            });
            this.getView().findViewById(R.id.forgotPasswordButton).setOnClickListener(v -> {
                this.navController.navigate(R.id.action_loginScreen_to_forgotPassword);
            });
            askNotificationPermission();
        } catch (Exception e) {
            // fails silently
        }
    }

    // function that will be called when login button is clicked
    public void loginCustomer(final View v) {
        final String phone = "+92" + ((TextInputEditText) this.getView().findViewById(R.id.phoneInputField)).getText().toString();
        final String password = ((TextInputEditText) this.getView().findViewById(R.id.passwordInputField)).getText().toString();
        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        // get the fcm token from shared preferences
        final String fcmToken = sharedPreferences.getString("fcmToken", "");
        final LoginRequestModel request = new LoginRequestModel(phone, password, fcmToken);
        APIClient.getInstance().getMyApi().loginCustomer(request).enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(final Call<LoginResponseModel> call, final Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    final LoginResponseModel loginResponseModel = response.body();
                    if (!loginResponseModel.getUserType().equals("CUSTOMER")) {
                        Toast.makeText(getContext(), "You need to be a customer to login to app.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    sharedPreferences.edit().putString("accessToken", loginResponseModel.getAccessToken()).apply();
                    sharedPreferences.edit().putString("refreshToken", loginResponseModel.getRefreshToken()).apply();
                    sharedPreferences.edit().putString("phone", phone).apply();
                    Gson gson = new Gson();
                    String json = gson.toJson(loginResponseModel);
                    sharedPreferences.edit().putString("user", json).apply();
                    sharedPreferences.edit().putString("id", loginResponseModel.getId().toString()).apply();
                    APIClient.getInstance().setToken(loginResponseModel.getAccessToken());
                    Toast.makeText(getContext(), "Logged In", Toast.LENGTH_SHORT).show();
                    final Intent intent = new Intent(LoginScreen.this.getActivity(), HomeActivity.class);
                    LoginScreen.this.getActivity().startActivity(intent);
                    LoginScreen.this.getActivity().finish();
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
            public void onFailure(final Call<LoginResponseModel> call, final Throwable t) {
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
