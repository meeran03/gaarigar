package com.gianteyes.gaarigarapp.screens.order_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.adapters.OrdersAdapter;
import com.gianteyes.gaarigarapp.models.OrderModel;
import com.gianteyes.gaarigarapp.providers.CustomContextProvider;
import com.gianteyes.gaarigarapp.services.APIClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveOrders extends Fragment {
    ArrayList<OrderModel> orders = new ArrayList<>();
    TextView noOrdersText;
    RecyclerView ordersRV;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_orders, container, false);
    }

    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noOrdersText = view.findViewById(R.id.noActiveOrdersText);
        ordersRV = this.getView().findViewById(R.id.idRVOrders);
        try {
            getActiveOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getActiveOrders() {
        // get current user from shared preferences
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", null);
        if (userId == null) {
            Toast.makeText(this.getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        // cast to long
        long id = Long.parseLong(userId);
        String[] activeStatuses = {"ACCEPTED",
                "REQUESTED",
                "PENDING",
                "IN_PROGRESS"};
        String statusesConcat = "";
        for (final String status : activeStatuses) {
            statusesConcat += status + ",";
        }
        statusesConcat = statusesConcat.substring(0, statusesConcat.length() - 1);
        APIClient.getInstance().getMyApi().getOrders(id, statusesConcat, null, null).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.isSuccessful()) {
                    orders.clear();
                    orders.addAll(response.body());
                    RecyclerView ordersRV = getView().findViewById(R.id.idRVOrders);
                    if (ActiveOrders.this.orders.size() <= 0) {
                        ActiveOrders.this.noOrdersText.setVisibility(View.VISIBLE);
                        ordersRV.setVisibility(View.GONE);
                    } else {
                        ActiveOrders.this.noOrdersText.setVisibility(View.GONE);
                        ordersRV.setVisibility(View.VISIBLE);
                    }
                    RecyclerView.Adapter OrdersAdapter = new OrdersAdapter(R.id.action_activeOrders_to_orderDetails2, getActivity(), orders);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    ordersRV.setLayoutManager(linearLayoutManager);
                    ordersRV.setAdapter(OrdersAdapter);
                    CustomContextProvider.getInstance().setOrders(
                            orders
                    );
                    Toast.makeText(ActiveOrders.this.getActivity(), "Orders fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        final JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (final Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to get orders", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
