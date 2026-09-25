package com.gianteyes.gaarigarapp.screens.home_screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.gianteyes.gaarigarapp.models.common.MLocation;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home extends Fragment {
    NavController navController;
    RecyclerView popularServices;
    RecyclerView.Adapter adapter;
    LoginResponseModel user;
    MaterialTextView name;
    Location loc;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
        this.loc = getCurrentLocation();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            this.navController = Navigation.findNavController(getView());
            name = view.findViewById(R.id.userNameOnHome);
            this.name.setText(user.getFirstName());
            getView().findViewById(R.id.requestElectrician).setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("mechanicType", "Electrician");
                bundle.putString("type", "Mechanic");
                if (navController.getCurrentDestination().getId() == R.id.home2) {
                    this.navController.navigate(R.id.action_home2_to_initiateRequest, bundle);
                }
            });

            getView().findViewById(R.id.requestVulcanizer).setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("mechanicType", "Vulcanizer");
                bundle.putString("type", "Mechanic");
                if (navController.getCurrentDestination().getId() == R.id.home2) {
                    this.navController.navigate(R.id.action_home2_to_initiateRequest, bundle);
                }
            });

            getView().findViewById(R.id.requestFuel).setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("type", "PetrolPump");
                if (navController.getCurrentDestination().getId() == R.id.home2) {
                    this.navController.navigate(R.id.action_home2_to_initiateRequest, bundle);
                }
            });

            if (this.loc == null) {
                loc = this.getCurrentLocation();
                Toast.makeText(this.getContext(), "Please enable location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Location found", Toast.LENGTH_SHORT).show();
            }
            if (loc != null) {
                final Call<Void> call = APIClient.getInstance().getMyApi().updateLocation(new MLocation(loc.getLatitude(), loc.getLongitude()));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(final Call<Void> call, final Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(Home.this.getContext(), "Location updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Home.this.getContext(), "Location update failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(final Call<Void> call, final Throwable t) {
                        Toast.makeText(Home.this.getContext(), "Location update failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } catch (final Exception e) {
            // fail silently
        }
    }

    private Boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    private Location getCurrentLocation() {
        if (this.isLocationPermissionGranted()) {
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

}
