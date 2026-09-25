package com.example.mechanicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechanicapp.Models.OrdersModel;
import com.example.mechanicapp.R;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.Viewholder>{


    private final Context context;
    private final ArrayList<OrdersModel> ordersModelArrayList;

    // Constructor
    public OrdersAdapter(Context context, ArrayList<OrdersModel> ordersModelArrayList) {
        this.context = context;
        this.ordersModelArrayList = ordersModelArrayList;
    }

    @NonNull
    @Override
    public OrdersAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_orders_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.Viewholder holder, int position) {

        OrdersModel model = ordersModelArrayList.get(position);
        holder.order_detail.setText(model.getOrder_detail());

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return ordersModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview



    public class Viewholder extends RecyclerView.ViewHolder{
        private final TextView order_detail;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            order_detail = itemView.findViewById(R.id.order_detail);
        }
    }
}
