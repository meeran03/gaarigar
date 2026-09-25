package com.example.mechanicapp.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mechanicapp.Models.OrdersModel;
import com.example.mechanicapp.R;
import com.example.mechanicapp.adapters.OrdersAdapter;

import java.util.ArrayList;

public class ActiveOrders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_active_orders);
        RecyclerView ordersRV = findViewById(R.id.idRVOrders);

        // Here, we have created new array list and added data to it
        ArrayList<OrdersModel> ordersModelArrayList = new ArrayList<OrdersModel>();
        ordersModelArrayList.add(new OrdersModel("Oil Change"));
        ordersModelArrayList.add(new OrdersModel("Upgrade Engine"));
        ordersModelArrayList.add(new OrdersModel("Fix Engine"));
        ordersModelArrayList.add(new OrdersModel("Fix Headlights"));


        // we are initializing our adapter class and passing our arraylist to it.
        OrdersAdapter OrdersAdapter = new OrdersAdapter(this, ordersModelArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        ordersRV.setLayoutManager(linearLayoutManager);
        ordersRV.setAdapter(OrdersAdapter);
    }

}
