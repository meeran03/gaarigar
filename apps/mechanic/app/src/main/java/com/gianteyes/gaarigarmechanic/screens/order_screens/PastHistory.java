package com.gianteyes.gaarigarmechanic.screens.order_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.adapters.OrdersAdapter;
import com.gianteyes.gaarigarmechanic.models.OrderModel;
import com.gianteyes.gaarigarmechanic.providers.CustomContextProvider;
import com.gianteyes.gaarigarmechanic.services.APIClient;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastHistory extends Fragment {
    Button rangeSelectionButton;
    MaterialDatePicker dateRangePicker;
    ArrayList<OrderModel> orders = new ArrayList<>();

    public PastHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.getOrders();
        this.dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select dates")
                        .setSelection(
                                Pair.create(
                                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                        MaterialDatePicker.todayInUtcMilliseconds()
                                )
                        )
                        .build();
        this.rangeSelectionButton = view.findViewById(R.id.pick_date_button);

        this.rangeSelectionButton.setOnClickListener(v -> {
            this.dateRangePicker.show(getChildFragmentManager(), "datePicker");
        });

        this.dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            this.getOrders();
        });

        // fetch orders on spinner change
        Spinner spinner = view.findViewById(R.id.statusSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PastHistory.this.getOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PastHistory.this.getOrders();
            }
        });
    }

    public void getOrders() {
        // get current user from shared preferences
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("id", null);
        if (userId == null) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        // cast to long
        final long id = Long.parseLong(userId);
        final String status = ((Spinner) getActivity().findViewById(R.id.statusSpinner)).getSelectedItem().toString();
        LocalDateTime startDate = LocalDateTime.now();
        startDate = startDate.minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // get range from date picker
        if (this.dateRangePicker != null) {
            final Pair<Long, Long> range = (Pair<Long, Long>) this.dateRangePicker.getSelection();
            Date start = new Date(range.first);
            Date end = new Date(range.second);
            startDate = this.convertToLocalDateTimeViaInstant(start);
            endDate = this.convertToLocalDateTimeViaInstant(end);
        }
        // remove milliseconds part
        startDate = startDate.withNano(0);
        endDate = endDate.withNano(0);
        APIClient.getInstance().getMyApi().getOrders(id, status, startDate, endDate).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(final Call<List<OrderModel>> call, final Response<List<OrderModel>> response) {
                if (response.isSuccessful()) {
                    PastHistory.this.orders.clear();
                    PastHistory.this.orders.addAll(response.body());
                    final RecyclerView ordersRV = PastHistory.this.getView().findViewById(R.id.idRVOrders);
                    final RecyclerView.Adapter OrdersAdapter = new OrdersAdapter(PastHistory.this.getActivity(), PastHistory.this.orders);
                    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PastHistory.this.getActivity(), LinearLayoutManager.VERTICAL, false);
                    ordersRV.setLayoutManager(linearLayoutManager);
                    ordersRV.setAdapter(OrdersAdapter);
                    CustomContextProvider.getInstance().setOrders(
                            PastHistory.this.orders
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

    public LocalDateTime convertToLocalDateTimeViaInstant(final Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
