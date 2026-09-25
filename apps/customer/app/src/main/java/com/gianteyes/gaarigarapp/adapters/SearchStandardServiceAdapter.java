package com.gianteyes.gaarigarapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.StandardServicesResponse;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SearchStandardServiceAdapter extends RecyclerView.Adapter<SearchStandardServiceAdapter.StandardServiceViewHolder> {

    ArrayList<StandardServicesResponse> standardServices;
    Context myActivity;

    public SearchStandardServiceAdapter(Context activity, final ArrayList<StandardServicesResponse> standardServices) {
        this.myActivity = activity;
        this.standardServices = standardServices;
    }

    @NonNull
    @Override
    public StandardServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_service_card, parent, false);
        StandardServiceViewHolder searchStandardServiceViewHolder = new StandardServiceViewHolder(view);

        return searchStandardServiceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StandardServiceViewHolder holder, int position) {
        StandardServicesResponse SSRM = standardServices.get(position);
        if (SSRM.getStandardService().getImage() != null) {
            Glide.with(myActivity).load(SSRM.getStandardService().getImage()).into(holder.image);
        }
        holder.name.setText(SSRM.getStandardService().getName());
        holder.price.setText(String.valueOf(SSRM.getPrice()));
        holder.rating.setRating(SSRM.getMechanic().getRating());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String standardService = gson.toJson(SSRM);
                Bundle bundle = new Bundle();
                bundle.putString("service", standardService);
                Navigation.findNavController(v).navigate(R.id.action_search_to_initiateServiceRequest, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.standardServices.size();
    }

    public void addData(ArrayList<StandardServicesResponse> data) {
        this.standardServices = data;
    }

    public static class StandardServiceViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, price;
        RatingBar rating;
        CardView cardView;

        public StandardServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.serviceImage);
            name = itemView.findViewById(R.id.serviceName);
            price = itemView.findViewById(R.id.servicePrice);
            rating = itemView.findViewById(R.id.serviceRating);
            cardView = itemView.findViewById(R.id.serviceCardContainer);
        }
    }
}
