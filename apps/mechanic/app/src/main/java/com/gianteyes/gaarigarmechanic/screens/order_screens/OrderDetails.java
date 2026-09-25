package com.gianteyes.gaarigarmechanic.screens.order_screens;

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

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
import com.gianteyes.gaarigarmechanic.models.OrderModel;
import com.gianteyes.gaarigarmechanic.models.PaymentResponseDto;
import com.gianteyes.gaarigarmechanic.providers.CustomContextProvider;
import com.gianteyes.gaarigarmechanic.services.APIClient;
import com.google.gson.Gson;
import com.stripe.android.Stripe;

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
        String apiVersion = Stripe.API_VERSION;
        APIClient.getInstance().getMyApi().payOrder(order.getOrderId(), apiVersion).enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful()) {
                    PaymentResponseDto paymentResponseDto = response.body();
                    CustomContextProvider.getInstance().setPaymentResponseDto(paymentResponseDto);
                    // start checkout activity
                    Intent intent = new Intent(getContext(), CheckoutActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(OrderDetails.this.getContext(), "Error while fetching payment details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                Toast.makeText(OrderDetails.this.getContext(), "Error while fetching payment details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(this.getView());
        this.chatButton = view.findViewById(R.id.chatButton);
        this.chatButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("orderId", order.getOrderId().toString());
            navController.navigate(R.id.action_orderDetails2_to_chatScreen, bundle);
        });
        // get order from bundle
        order = CustomContextProvider.getInstance().findByOrderId(Long.parseLong(this.getArguments().getString("order")));
        getView().findViewById(R.id.location_button).setOnClickListener(v -> {
            this.openGoogleMapWithUserAndProviderLocation();
        });
        this.setOrderDetails();
        getView().findViewById(R.id.payButton).setOnClickListener(v -> {
            this.payOrder();
        });
    }

    void setOrderDetails() {
        // set order details
        TextView order_status = getView().findViewById(R.id.order_status);
        TextView order_type = getView().findViewById(R.id.order_type);
        TextView provider_name = getView().findViewById(R.id.provider_name);
        TextView provider_phone = getView().findViewById(R.id.provider_phone);
        TextView fuel_litres = getView().findViewById(R.id.fuel_litres);
        TextView standard_service_name = getView().findViewById(R.id.standard_service_name);
        TextView payment_method = getView().findViewById(R.id.payment_method);
        TextView order_price = getView().findViewById(R.id.order_price);
        Button ratingButton = getView().findViewById(R.id.ratingButton);
        order_status.setText(this.order.getStatus());
        order_type.setText(this.order.getType().toString());
        Bundle bundle = new Bundle();
        if (this.order.getType().toString().equals("MECHANIC")) {
            provider_name.setText(this.order.getMechanic().getName());
            provider_phone.setText(this.order.getMechanic().getPhone());
            bundle.putString("ratedTo", this.order.getMechanic().getId().toString());
        } else if (this.order.getType().toString().equals("FUEL_DELIVERY")) {
            provider_name.setText(this.order.getPetrolPump().getName());
            provider_phone.setText(this.order.getPetrolPump().getPhone());
            fuel_litres.setText(this.order.getLitres().toString());
            getView().findViewById(R.id.fuelLitersContainer).setVisibility(View.VISIBLE);
            bundle.putString("ratedTo", this.order.getPetrolPump().getId().toString());
        } else {
            provider_name.setText(this.order.getMechanic().getName());
            provider_phone.setText(this.order.getMechanic().getPhone());
            standard_service_name.setText(this.order.getStandardService().getName());
            getView().findViewById(R.id.standardServiceNameContainer).setVisibility(View.VISIBLE);
            bundle.putString("ratedTo", this.order.getMechanic().getId().toString());
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        this.user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
        bundle.putString("ratedBy", user.getId().toString());
        payment_method.setText(order.getPaymentMethod());
        if (order.getPrice() != null) {
            order_price.setText(this.order.getPrice().toString());
            getView().findViewById(R.id.orderPriceContainer).setVisibility(View.VISIBLE);
        }
        if (!order.getStatus().equals("COMPLETED") || !order.getStatus().equals("REJECTED") || !order.getStatus().equals("CANCELLED")) {
            getView().findViewById(R.id.location_button).setVisibility(View.VISIBLE);
        }
        if (order.getStatus().equals("IN_PROGRESS") && order.getPaymentMethod() != null && !order.getPaymentMethod().equals("CASH")) {
            getView().findViewById(R.id.payButton).setVisibility(View.VISIBLE);
        }
        if (order.getStatus().equals("IN_PROGRESS") || order.getStatus().equals("ACCEPTED")) {
            getView().findViewById(R.id.chatButton).setVisibility(View.VISIBLE);
        }
        if (!order.getStatus().equals("COMPLETED")) {
            getView().findViewById(R.id.cancelButton).setVisibility(View.VISIBLE);
        }

    }

    public void openGoogleMapWithUserAndProviderLocation() {
        // we open google map with mechanic and our current location's route
        // get current location
        Location currentLocation = getCurrentLocation();
        // get mechanic location
        com.gianteyes.gaarigarmechanic.models.common.MLocation mechanicLocation = order.getMechanic().getLocation();
        // open google map with route
        String geoUri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" + mechanicLocation.getLatitude() + "," + mechanicLocation.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        startActivity(intent);
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
