package com.gianteyes.gaarigarapp.screens.order_screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.adapters.OrdersAdapter;
import com.gianteyes.gaarigarapp.models.OrderModel;
import com.gianteyes.gaarigarapp.providers.CustomContextProvider;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONObject;

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
    TextView noOrdersText;

    public PastHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOrders();
        dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select dates")
                        .setSelection(
                                Pair.create(
                                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                        MaterialDatePicker.todayInUtcMilliseconds()
                                )
                        )
                        .build();
        noOrdersText = view.findViewById(R.id.noOrdersText);
        rangeSelectionButton = view.findViewById(R.id.pick_date_button);

        rangeSelectionButton.setOnClickListener(v -> {
            dateRangePicker.show(this.getChildFragmentManager(), "datePicker");
        });

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            try {
                getOrders();
            } catch (final Exception e) {
                // fail silently
            }
        });

        // fetch orders on spinner change
        final Spinner spinner = view.findViewById(R.id.statusSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                getOrders();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                getOrders();
            }
        });
    }

    public void getOrders() {
        // get current user from shared preferences
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", null);
        if (userId == null) {
            Toast.makeText(this.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        // cast to long
        long id = Long.parseLong(userId);
        String status = ((Spinner) this.getActivity().findViewById(R.id.statusSpinner)).getSelectedItem().toString();
        LocalDateTime startDate = LocalDateTime.now();
        startDate = startDate.minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        // get range from date picker
        if (dateRangePicker != null) {
            Pair<Long, Long> range = (Pair<Long, Long>) dateRangePicker.getSelection();
            final Date start = new Date(range.first);
            final Date end = new Date(range.second);
            startDate = convertToLocalDateTimeViaInstant(start);
            endDate = convertToLocalDateTimeViaInstant(end);
        }
        // remove milliseconds part
        startDate = startDate.withNano(0);
        endDate = endDate.withNano(0);
        APIClient.getInstance().getMyApi().getOrders(id, status, startDate, endDate).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.isSuccessful()) {
                    orders.clear();
                    orders.addAll(response.body());
                    RecyclerView ordersRV = getView().findViewById(R.id.idRVOrders);
                    if (PastHistory.this.orders.size() <= 0) {
                        PastHistory.this.noOrdersText.setVisibility(View.VISIBLE);
                        ordersRV.setVisibility(View.GONE);
                    } else {
                        PastHistory.this.noOrdersText.setVisibility(View.GONE);
                        ordersRV.setVisibility(View.VISIBLE);
                    }
                    RecyclerView.Adapter OrdersAdapter = new OrdersAdapter(R.id.action_pastHistory_to_orderDetails2, getActivity(), orders);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    ordersRV.setLayoutManager(linearLayoutManager);
                    ordersRV.setAdapter(OrdersAdapter);
                    CustomContextProvider.getInstance().setOrders(
                            orders
                    );
                    Toast.makeText(PastHistory.this.getContext(), "Orders fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        final JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(PastHistory.this.getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (final Exception e) {
                        Toast.makeText(PastHistory.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Toast.makeText(PastHistory.this.getContext(), "Failed to get orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
