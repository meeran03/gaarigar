package com.gianteyes.gaarigarapp.screens.order_screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.InitiateStandardServiceRequest;
import com.gianteyes.gaarigarapp.models.StandardServicesResponse;
import com.gianteyes.gaarigarapp.models.common.MLocation;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class InitiateServiceRequest extends Fragment {
    StandardServicesResponse service;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        this.service = gson.fromJson(getArguments().getString("service"), StandardServicesResponse.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_initiate_service_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.service.getStandardService().getImage() != null) {
            final ImageView serviceImage = view.findViewById(R.id.serviceImage);
            Glide.with(this).load(this.service.getStandardService().getImage()).into(serviceImage);
        }
        ((TextView) view.findViewById(R.id.serviceTitle)).setText(this.service.getStandardService().getName());
        ((TextView) view.findViewById(R.id.serviceDetails)).setText(this.service.getStandardService().getDescription());
        ((TextView) view.findViewById(R.id.servicePrice)).setText(this.service.getPrice().toString());
        ((TextView) view.findViewById(R.id.provider_name)).setText(this.service.getMechanic().getName());
        ((RatingBar) view.findViewById(R.id.providerRating)).setRating(this.service.getMechanic().getRating());
        view.findViewById(R.id.orderServiceBtn).setOnClickListener(v -> {
            this.initiateStandardServiceRequest();
        });
    }

    void initiateStandardServiceRequest() {
        String notes = ((TextInputEditText) this.getView().findViewById(R.id.notesTextInputEditText)).getText().toString();
        String paymentMethod = ((Spinner) this.getView().findViewById(R.id.paymentMethodSpinner)).getSelectedItem().toString();
        paymentMethod = paymentMethod.equals("Cash") ? "CASH" : "ONLINE_PAYMENT";
        InitiateStandardServiceRequest request = new InitiateStandardServiceRequest();
        request.setMechanicStandardServiceId(this.service.getId());
        request.setNotes(notes);
        request.setPaymentMethod(paymentMethod);
        Location loc = this.getCurrentLocation();
        MLocation location = new MLocation(loc.getLatitude(), loc.getLongitude());
        request.setLocation(location);

        APIClient.getInstance().getMyApi().createStandardOrder(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(final retrofit2.Call<Void> call, final retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InitiateServiceRequest.this.getContext(), "Order placed successfully, You will get confirmation within 30 minutes.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InitiateServiceRequest.this.getContext(), "Order placement failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final retrofit2.Call<Void> call, final Throwable t) {
                Toast.makeText(InitiateServiceRequest.this.getContext(), "Order placement failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Location getCurrentLocation() {
        if (isLocationPermissionGranted()) {
            final LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//            iterate through all providers to get the last known location
            Location bestLocation = null;
            for (final String provider : locationManager.getProviders(true)) {
                @SuppressLint("MissingPermission") final Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    return location;
                }

                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
            return bestLocation;
        }
        return null;
    }

    private Boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }
}
