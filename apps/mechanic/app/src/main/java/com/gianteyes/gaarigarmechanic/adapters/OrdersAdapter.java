package com.gianteyes.gaarigarmechanic.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.OrderModel;
import com.gianteyes.gaarigarmechanic.models.common.OrderType;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.Viewholder> {


    private final Context context;
    private final ArrayList<OrderModel> ordersModelArrayList;

    // Constructor
    public OrdersAdapter(Context context, ArrayList<OrderModel> ordersModelArrayList) {
        this.context = context;
        this.ordersModelArrayList = ordersModelArrayList;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_orders_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        OrderModel model = ordersModelArrayList.get(position);
//        holder.name.setText(model.get());
        holder.orderId_type.setText(
                model.getOrderId() + " #" + model.getType().toString()
        );
        holder.orderStatus.setText(model.getStatus());
        String color;
        if (model.getStatus().equals("ACCEPTED")) {
            color = "#00FF00";
        } else if (model.getStatus().equals("REQUESTED")) {
            color = "#FF0000";
        } else if (model.getStatus().equals("PENDING")) {
            color = "#FFA500";
        } else {
            color = "#0000FF";
        }
        holder.orderStatus.setTextColor(android.graphics.Color.parseColor(color));
        holder.status_badge.setVisibility(View.VISIBLE);
        // get only date part from LocalDateTime
        holder.orderDate.setText(model.getRequestedAt().split("T")[0]);

        if (model.getType().equals(OrderType.FUEL_DELIVERY.toString())) {
            holder.provider_name.setText(
                    model.getPetrolPump().getName());
            holder.provider_phone.setText(
                    model.getPetrolPump().getPhone()
            );
        } else if (model.getType().equals(OrderType.MECHANIC.toString())) {
            holder.provider_name.setText(
                    model.getMechanic().getName()
            );
            holder.provider_phone.setText(
                    model.getMechanic().getPhone()
            );
        } else {
            holder.provider_name.setText(
                    model.getMechanic().getName()
            );
            holder.provider_phone.setText(
                    model.getMechanic().getPhone()
            );
        }
        holder.order_card.setOnClickListener(v -> {
            // move to order details screen
            final Bundle bundle = new Bundle();
            bundle.putString("order", model.getOrderId().toString());
            Navigation.findNavController(v).navigate(R.id.action_activeOrders_to_orderDetails2, bundle);
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return ordersModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview


    public class Viewholder extends RecyclerView.ViewHolder {
        private final TextView orderId_type;
        private final TextView orderStatus;
        private final TextView orderDate;
        private final TextView provider_name;
        private final TextView provider_phone;
        private final CardView status_badge;
        private final CardView order_card;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            orderId_type = itemView.findViewById(R.id.order_id_and_type);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderDate = itemView.findViewById(R.id.order_date);
            provider_name = itemView.findViewById(R.id.provider_name);
            provider_phone = itemView.findViewById(R.id.providerPhoneNumber);
            status_badge = itemView.findViewById(R.id.status_badge);
            order_card = itemView.findViewById(R.id.active_order_item);
        }
    }
}
