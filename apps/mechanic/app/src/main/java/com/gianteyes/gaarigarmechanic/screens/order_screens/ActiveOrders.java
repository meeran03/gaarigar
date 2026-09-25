package com.gianteyes.gaarigarmechanic.screens.order_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.adapters.OrdersAdapter;
import com.gianteyes.gaarigarmechanic.models.OrderModel;
import com.gianteyes.gaarigarmechanic.providers.CustomContextProvider;
import com.gianteyes.gaarigarmechanic.services.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveOrders extends Fragment {
    ArrayList<OrderModel> orders = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_orders, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getActiveOrders();
    }

    public void getActiveOrders() {
        // get current user from shared preferences
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("id", null);
        if (userId == null) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        // cast to long
        final long id = Long.parseLong(userId);
        final String[] activeStatuses = {"ACCEPTED",
                "REQUESTED",
                "PENDING",
                "IN_PROGRESS"};
        String statusesConcat = "";
        for (String status : activeStatuses) {
            statusesConcat += status + ",";
        }
        statusesConcat = statusesConcat.substring(0, statusesConcat.length() - 1);
        APIClient.getInstance().getMyApi().getOrders(id, statusesConcat, null, null).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(final Call<List<OrderModel>> call, final Response<List<OrderModel>> response) {
                if (response.isSuccessful()) {
                    ActiveOrders.this.orders.addAll(response.body());
                    final RecyclerView ordersRV = ActiveOrders.this.getView().findViewById(R.id.idRVOrders);
                    final RecyclerView.Adapter OrdersAdapter = new OrdersAdapter(ActiveOrders.this.getActivity(), ActiveOrders.this.orders);
                    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActiveOrders.this.getActivity(), LinearLayoutManager.VERTICAL, false);
                    ordersRV.setLayoutManager(linearLayoutManager);
                    ordersRV.setAdapter(OrdersAdapter);
                    CustomContextProvider.getInstance().setOrders(
                            ActiveOrders.this.orders
                    );
                    Toast.makeText(getActivity(), "Orders fetched successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<List<OrderModel>> call, final Throwable t) {
                Toast.makeText(getActivity(), "Failed to get orders", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
