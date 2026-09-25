package com.gianteyes.gaarigarapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.PopularServicesModel;

import java.util.ArrayList;

public class ServicesCardAdapter extends RecyclerView.Adapter<ServicesCardAdapter.PopularServiceViewHolder> {

    ArrayList<PopularServicesModel> popularServiceModels;

    public ServicesCardAdapter(final ArrayList<PopularServicesModel> popularServiceModels) {
        this.popularServiceModels = popularServiceModels;
    }

    @NonNull
    @Override
    public PopularServiceViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_service_card, parent, false);
        final PopularServiceViewHolder popularServiceViewHolder = new PopularServiceViewHolder(view);

        return popularServiceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PopularServiceViewHolder holder, final int position) {
        final PopularServicesModel popularServicesModelHelper = this.popularServiceModels.get(position);
        holder.image.setImageResource(popularServicesModelHelper.getImage());
        holder.name.setText(popularServicesModelHelper.getName());
        holder.price.setText(String.valueOf(popularServicesModelHelper.getPrice()));
        holder.rating.setRating(popularServicesModelHelper.getRating());
    }

    @Override
    public int getItemCount() {
        return this.popularServiceModels.size();
    }

    public static class PopularServiceViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, price;
        RatingBar rating;

        public PopularServiceViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.image = itemView.findViewById(R.id.serviceImage);
            this.name = itemView.findViewById(R.id.serviceName);
            this.price = itemView.findViewById(R.id.servicePrice);
            this.rating = itemView.findViewById(R.id.serviceRating);
        }
    }
}
