package com.gianteyes.gaarigarapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarapp.models.InitiateRequestModel;
import com.gianteyes.gaarigarapp.models.InitiateRequestResponseModel;
import com.gianteyes.gaarigarapp.models.common.MLocation;
import com.gianteyes.gaarigarapp.models.common.OrderType;
import com.gianteyes.gaarigarapp.providers.NearbyServiceProvider;
import com.gianteyes.gaarigarapp.services.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitiateRequest extends Fragment {
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }
    };
    public NavController navController;
    String type;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_initiate_request, container, false);
    }

    //    get type of request passed in onActivityCreated
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceBundle) {
        navController = Navigation.findNavController(view);
        super.onViewCreated(view, savedInstanceBundle);
        String type = getArguments().getString("type");
        this.type = type;
        TextView text = getView().findViewById(R.id.subtitleText);
        if (type.equals("Mechanic")) {
            final String mechanicType = getArguments().getString("mechanicType");
            text.setText("Fill the form below to initiate a mechanic request");
            this.getView().findViewById(R.id.mechanicTypesSpinner).setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.mechanic_types, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = getView().findViewById(R.id.mechanicTypesSpinner);
            spinner.setAdapter(adapter);
            spinner.setSelection(adapter.getPosition(mechanicType));
            this.getView().findViewById(R.id.litresTextInputLayout).setVisibility(View.GONE);
        } else if (type.equals("PetrolPump")) {
            text.setText("Fill the form below to initiate a fuel delivery request");
            this.getView().findViewById(R.id.litresTextInputLayout).setVisibility(View.VISIBLE);
            this.getView().findViewById(R.id.mechanicTypesSpinner).setVisibility(View.GONE);
        }

        getView().findViewById(R.id.requestButton).setOnClickListener(v -> this.performInitiateRequest());

    }

    private Boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    private Location getCurrentLocation() {
        if (isLocationPermissionGranted()) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//            iterate through all providers to get the last known location
            Location bestLocation = null;
            for (String provider : locationManager.getProviders(true)) {
                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
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


    void performInitiateRequest() {
        Location location = getCurrentLocation();
        if (location == null) {
            Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        String customerId = sharedPreferences.getString("id", null);
        final InitiateRequestModel model = InitiateRequestModel.builder()
                .location(new MLocation(location.getLatitude(), location.getLongitude()))
                .notes(((TextView) getView().findViewById(R.id.titleTextInputEditText)).getText().toString())
                .customer(Long.parseLong(customerId))
                .paymentMethod(((Spinner) getView().findViewById(R.id.paymentMethodSpinner)).getSelectedItem().toString().toUpperCase())
                .build();
        if (type.equals("Mechanic")) {
            model.setMechanicType(((Spinner) getView().findViewById(R.id.mechanicTypesSpinner)).getSelectedItem().toString().toUpperCase());
            model.setRequestType(OrderType.MECHANIC);
            APIClient.getInstance().getMyApi().initiateMechanicRequest(model).enqueue(new Callback<InitiateRequestResponseModel>() {
                @Override
                public void onResponse(Call<InitiateRequestResponseModel> call, Response<InitiateRequestResponseModel> response) {
                    if (response.isSuccessful()) {
                        InitiateRequestResponseModel initiatedRequest = response.body();
                        if (initiatedRequest.getNearbyProviders() == null || initiatedRequest.getNearbyProviders().isEmpty()) {
                            Toast.makeText(getContext(), "No nearby providers currently", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        NearbyServiceProvider.getInstance().setNearbyServices(initiatedRequest.getNearbyProviders());
                        NearbyServiceProvider.getInstance().setRequestId(initiatedRequest.getId());
                        Toast.makeText(getContext(), "Request initiated", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "mechanic");
                        if (InitiateRequest.this.navController.getCurrentDestination().getId() == R.id.initiateRequest) {
                            InitiateRequest.this.navController.navigate(R.id.action_initiateRequest_to_nearbyMechanicsMapFragment, bundle);
                        }
                    } else {
                        Toast.makeText(getContext(), "Request initiation failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<InitiateRequestResponseModel> call, Throwable t) {
                    Toast.makeText(InitiateRequest.this.getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            model.setNoOfLitres(Float.parseFloat(((TextView) getView().findViewById(R.id.litresTextInputEditText)).getText().toString()));
            model.setRequestType(OrderType.FUEL_DELIVERY);
            APIClient.getInstance().getMyApi().initiateFuelDeliveryRequest(model).enqueue(new Callback<InitiateRequestResponseModel>() {
                @Override
                public void onResponse(Call<InitiateRequestResponseModel> call, Response<InitiateRequestResponseModel> response) {
                    if (response.isSuccessful()) {
                        InitiateRequestResponseModel initiatedRequest = response.body();
                        if (initiatedRequest.getNearbyProviders() == null || initiatedRequest.getNearbyProviders().isEmpty()) {
                            Toast.makeText(getContext(), "No nearby providers currently", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        NearbyServiceProvider.getInstance().setNearbyServices(initiatedRequest.getNearbyProviders());
                        NearbyServiceProvider.getInstance().setRequestId(initiatedRequest.getId());
                        Toast.makeText(getContext(), "Request initiated", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "PetrolPump");
                        InitiateRequest.this.navController.navigate(R.id.action_initiateRequest_to_nearbyMechanicsMapFragment, bundle);
                    } else {
                        Toast.makeText(getContext(), "Request initiation failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<InitiateRequestResponseModel> call, Throwable t) {
                    Toast.makeText(InitiateRequest.this.getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
