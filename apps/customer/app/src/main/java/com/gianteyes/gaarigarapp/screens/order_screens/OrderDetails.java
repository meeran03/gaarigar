package com.gianteyes.gaarigarapp.screens.order_screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.gianteyes.gaarigarapp.models.OrderModel;
import com.gianteyes.gaarigarapp.models.PaymentResponseDto;
import com.gianteyes.gaarigarapp.providers.CustomContextProvider;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.gson.Gson;
import com.stripe.android.Stripe;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetails extends Fragment {
    Button chatButton;
    NavController navController;
    OrderModel order;
    LoginResponseModel user;

    public OrderDetails() {
        // Required empty public constructor
    }


    private void payOrder() {
        final String apiVersion = Stripe.API_VERSION;
        APIClient.getInstance().getMyApi().payOrder(this.order.getOrderId(), apiVersion).enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(final Call<PaymentResponseDto> call, final Response<PaymentResponseDto> response) {
                if (response.isSuccessful()) {
                    final PaymentResponseDto paymentResponseDto = response.body();
                    CustomContextProvider.getInstance().setPaymentResponseDto(paymentResponseDto);
                    // start checkout activity
                    final Intent intent = new Intent(OrderDetails.this.getContext(), CheckoutActivity.class);
                    OrderDetails.this.startActivity(intent);
                    OrderDetails.this.getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Error while fetching payment details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<PaymentResponseDto> call, final Throwable t) {
                Toast.makeText(getContext(), "Error while fetching payment details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.navController = Navigation.findNavController(getView());
        chatButton = view.findViewById(R.id.chatButton);
        chatButton.setOnClickListener(v -> {
            final Bundle bundle = new Bundle();
            bundle.putString("orderId", this.order.getOrderId().toString());
            this.navController.navigate(R.id.action_orderDetails2_to_chatScreen, bundle);
        });
        // get order from bundle
        this.order = CustomContextProvider.getInstance().findByOrderId(Long.parseLong(getArguments().getString("order")));
        this.getView().findViewById(R.id.location_button).setOnClickListener(v -> {
            openGoogleMapWithUserAndProviderLocation();
        });
        setOrderDetails();
        this.getView().findViewById(R.id.payButton).setOnClickListener(v -> {
            payOrder();
        });
    }

    void setOrderDetails() {
        // set order details
        final TextView order_status = this.getView().findViewById(R.id.order_status);
        final TextView order_type = this.getView().findViewById(R.id.order_type);
        final TextView provider_name = this.getView().findViewById(R.id.provider_name);
        final TextView provider_phone = this.getView().findViewById(R.id.provider_phone);
        final TextView fuel_litres = this.getView().findViewById(R.id.fuel_litres);
        final TextView standard_service_name = this.getView().findViewById(R.id.standard_service_name);
        final TextView payment_method = this.getView().findViewById(R.id.payment_method);
        final TextView order_price = this.getView().findViewById(R.id.order_price);
        final Button ratingButton = this.getView().findViewById(R.id.ratingButton);
        if (order == null) {
            this.navController.popBackStack();
            return;
        }
        order_status.setText(order.getStatus());
        order_type.setText(order.getType().toString());
        final Bundle bundle = new Bundle();
        if (order.getType().toString().equals("MECHANIC")) {
            provider_name.setText(order.getMechanic().getName());
            provider_phone.setText(order.getMechanic().getPhone());
            bundle.putString("ratedTo", order.getMechanic().getId().toString());
        } else if (order.getType().toString().equals("FUEL_DELIVERY")) {
            provider_name.setText(order.getPetrolPump().getName());
            provider_phone.setText(order.getPetrolPump().getPhone());
            fuel_litres.setText(order.getLitres().toString());
            this.getView().findViewById(R.id.fuelLitersContainer).setVisibility(View.VISIBLE);
            bundle.putString("ratedTo", order.getPetrolPump().getId().toString());
        } else {
            provider_name.setText(order.getMechanic().getName());
            provider_phone.setText(order.getMechanic().getPhone());
            standard_service_name.setText(order.getStandardService().getName());
            this.getView().findViewById(R.id.standardServiceNameContainer).setVisibility(View.VISIBLE);
            bundle.putString("ratedTo", order.getMechanic().getId().toString());
        }
        final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
        bundle.putString("ratedBy", this.user.getId().toString());
        payment_method.setText(this.order.getPaymentMethod());
        if (this.order.getPrice() != null) {
            order_price.setText(order.getPrice().toString());
            this.getView().findViewById(R.id.orderPriceContainer).setVisibility(View.VISIBLE);
        }
        if (!this.order.getStatus().equals("COMPLETED") || !this.order.getStatus().equals("REJECTED") || !this.order.getStatus().equals("CANCELLED")) {
            this.getView().findViewById(R.id.location_button).setVisibility(View.VISIBLE);
        }
        if (this.order.getStatus().equals("IN_PROGRESS") && this.order.getPaymentMethod() != null && !this.order.getPaymentMethod().equals("CASH")) {
            this.getView().findViewById(R.id.payButton).setVisibility(View.VISIBLE);
        }
        if (this.order.getStatus().equals("IN_PROGRESS") || this.order.getStatus().equals("ACCEPTED")) {
            this.getView().findViewById(R.id.chatButton).setVisibility(View.VISIBLE);
        }
        if (!this.order.getStatus().equals("COMPLETED") && !this.order.getStatus().equals("REJECTED") && !this.order.getStatus().equals("CANCELLED")) {
            this.getView().findViewById(R.id.cancelButton).setVisibility(View.VISIBLE);
            this.getView().findViewById(R.id.cancelButton).setOnClickListener(v -> {
                APIClient.getInstance().getMyApi().cancelOrder(this.order.getOrderId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(final Call<Void> call, final Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                            OrderDetails.this.navController.popBackStack();
                        } else {
                            try {
                                final JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(OrderDetails.this.getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (final Exception e) {
                                Toast.makeText(OrderDetails.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(final Call<Void> call, final Throwable t) {
                        Toast.makeText(getContext(), "Error while cancelling order", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            this.getView().findViewById(R.id.cancelButton).setVisibility(View.GONE);
        }

        if (this.order.getStatus().equals("COMPLETED") || this.order.getStatus().equals("IN_PROGRESS")) {
            ratingButton.setVisibility(View.VISIBLE);
            ratingButton.setOnClickListener(v -> {
                navController.navigate(R.id.action_orderDetails2_to_rating, bundle);
            });
        } else {
            ratingButton.setVisibility(View.GONE);
        }
    }

    public void openGoogleMapWithUserAndProviderLocation() {
        // we open google map with mechanic and our current location's route
        // get current location
        final Location currentLocation = this.getCurrentLocation();
        if (currentLocation == null) {

            return;
        }
        // get mechanic location
        final com.gianteyes.gaarigarapp.models.common.MLocation mechanicLocation = this.order.getMechanic().getLocation();
        // open google map with route
        final String geoUri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" + mechanicLocation.getLatitude() + "," + mechanicLocation.getLongitude();
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        this.startActivity(intent);
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
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps_enabled && !network_enabled) {
                // notify user
                Toast.makeText(this.getActivity(), "Enable location service", Toast.LENGTH_SHORT).show();
            }
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
        } else {
            Toast.makeText(getContext(), "Grant location permission first", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
