package com.example.mechanicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechanicapp.Models.OrdersModel;
import com.example.mechanicapp.Models.PastHistoryModel;
import com.example.mechanicapp.R;

import java.util.ArrayList;

public class PastHistoryAdapter extends RecyclerView.Adapter<PastHistoryAdapter.Viewholder>{


    private final Context context;
    private final ArrayList<PastHistoryModel> historyModelArrayList;

    // Constructor
    public PastHistoryAdapter(Context context, ArrayList<PastHistoryModel> historyModelArrayList) {
        this.context = context;
        this.historyModelArrayList = historyModelArrayList;
    }

    @NonNull
    @Override
    public PastHistoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_orders_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastHistoryAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        PastHistoryModel model = historyModelArrayList.get(position);
        holder.past_history_details.setText(model.getPast_history_details());

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return historyModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public class Viewholder extends RecyclerView.ViewHolder{
        private final TextView past_history_details;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            past_history_details = itemView.findViewById(R.id.past_history_details);
        }
    }
}
