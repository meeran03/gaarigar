package com.gianteyes.gaarigarmechanic.screens.standard_service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
import com.gianteyes.gaarigarmechanic.models.StandardServicesResponse;
import com.gianteyes.gaarigarmechanic.services.APIClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StandardService extends Fragment {
    LoginResponseModel user;
    List<StandardServicesResponse> standardServices;
    public StandardService() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStandardServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_standard_service, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        super.onViewCreated(view, savedInstanceBundle);

    }

    void getStandardServices() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        this.user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
        APIClient.getInstance().getMyApi().getMechanicStandardServices(user.getId()).enqueue(new Callback<List<StandardServicesResponse>>() {
            @Override
            public void onResponse(Call<List<StandardServicesResponse>> call, Response<List<StandardServicesResponse>> response) {
                if (response.isSuccessful()) {
                    standardServices = response.body();
                } else {
                    Toast.makeText(getContext(), "Could not fetch standard services", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<List<StandardServicesResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Could not fetch standard services", Toast.LENGTH_LONG);
            }
        });
    }
}
