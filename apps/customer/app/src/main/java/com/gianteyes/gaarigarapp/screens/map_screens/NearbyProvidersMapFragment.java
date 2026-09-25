package com.gianteyes.gaarigarapp.screens.map_screens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.ServiceProvider;
import com.gianteyes.gaarigarapp.providers.NearbyServiceProvider;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyProvidersMapFragment extends Fragment {
    private ServiceProvider selected;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            NearbyServiceProvider nearbyServiceProviders = NearbyServiceProvider.getInstance();
            final String type = NearbyProvidersMapFragment.this.getArguments().getString("type");
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ServiceProvider provider : nearbyServiceProviders.getNearbyServicers()) {
                LatLng providerLocation = new LatLng(provider.getLocation().getLatitude(), provider.getLocation().getLongitude());
                final int width = 100;
                final int height = 100;
                // scale icon
                Bitmap b = null;
                if (type.equals("mechanic")) {
                    b = BitmapFactory.decodeResource(getResources(), R.drawable.provider_marker);
                } else {
                    b = BitmapFactory.decodeResource(getResources(), R.drawable.petrol_pump_marker);
                }
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                final Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(providerLocation)
                                .title(provider.getName())
                                .icon(smallMarkerIcon)
                                .snippet(provider.getPhone())
                );
                builder.include(providerLocation);
            }
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.layout_info_window, null);
                    ServiceProvider mechanic = nearbyServiceProviders.findByPhone(marker.getSnippet());
                    final TextView name = v.findViewById(R.id.mechanic_name);
                    name.setText(mechanic.getName());

                    final TextView phoneNumber = v.findViewById(R.id.phone_number);
                    phoneNumber.setText(mechanic.getPhone());

                    final TextView ratings = v.findViewById(R.id.mechanic_rating);
                    ratings.setText(mechanic.getRating() == null ? "0" : mechanic.getRating().toString());

                    final CircleImageView iv = v.findViewById(R.id.marker_image);
                    Glide.with(NearbyProvidersMapFragment.this.getActivity()).load(mechanic.getImage()).into(iv);
                    return v;
                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    NearbyProvidersMapFragment.this.selected = nearbyServiceProviders.findByPhone(marker.getSnippet());
                    final Button bookNow = getView().findViewById(R.id.btn_book_mechanic);
                    if (bookNow.getVisibility() == View.GONE) {
                        bookNow.setVisibility(View.VISIBLE);
                        final TextView tv = getView().findViewById(R.id.tv_select_mechanic);
                        tv.setVisibility(View.GONE);
                    }
                    return true;
                }
            });
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            googleMap.animateCamera(cu);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_providers_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        final Button bookNow = view.findViewById(R.id.btn_book_mechanic);
        final String typeOfService = this.getArguments().getString("type");
        if (typeOfService.equals("mechanic")) {
            bookNow.setText("Book Mechanic");
        } else {
            bookNow.setText("Request Petrol");
        }
        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (NearbyProvidersMapFragment.this.selected != null) {
                        final String type = NearbyProvidersMapFragment.this.getArguments().getString("type");
                        final NearbyServiceProvider nearbyServiceProviders = NearbyServiceProvider.getInstance();
                        if (type.equals("mechanic")) {
                            APIClient.getInstance().getMyApi().askMechanic(NearbyProvidersMapFragment.this.selected.getId(), nearbyServiceProviders.getRequestId()).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(NearbyProvidersMapFragment.this.getContext(), "Provider has been notified", Toast.LENGTH_SHORT).show();
                                        // go back to 2 previous fragments
                                        Navigation.findNavController(getView()).popBackStack();
                                    } else {
                                        try {
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            // fails silently
                                            //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(NearbyProvidersMapFragment.this.getContext(), "Provider could not be notified", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                        }
                        APIClient.getInstance().getMyApi().askPetrolPump(NearbyProvidersMapFragment.this.selected.getId(), nearbyServiceProviders.getRequestId()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(NearbyProvidersMapFragment.this.getContext(), "Provider has been notified", Toast.LENGTH_SHORT).show();
                                    // go back to 2 previous fragments
                                    Navigation.findNavController(getView()).popBackStack();
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        // fails silently
                                        //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(NearbyProvidersMapFragment.this.getContext(), "Provider could not be notified", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(NearbyProvidersMapFragment.this.getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
