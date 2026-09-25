package com.gianteyes.gaarigarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.PastHistoryModel;

import java.util.ArrayList;

public class PastHistoryAdapter extends RecyclerView.Adapter<PastHistoryAdapter.Viewholder> {


    private final Context context;
    private final ArrayList<PastHistoryModel> historyModelArrayList;

    // Constructor
    public PastHistoryAdapter(final Context context, final ArrayList<PastHistoryModel> historyModelArrayList) {
        this.context = context;
        this.historyModelArrayList = historyModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        // to inflate the layout for each item of recycler view.
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_history_row, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        // to set data to textview and imageview of each card layout
        final PastHistoryModel model = this.historyModelArrayList.get(position);
        holder.past_history_details.setText(model.getPast_history_details());

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return this.historyModelArrayList.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public class Viewholder extends RecyclerView.ViewHolder {
        private final TextView past_history_details;

        public Viewholder(@NonNull final View itemView) {
            super(itemView);
            this.past_history_details = itemView.findViewById(R.id.past_history_details);
        }
    }
}
