package com.gianteyes.gaarigarapp.screens.order_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.RateRequestModel;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rating extends Fragment {
    Long ratedTo;
    Long ratedBy;
    RatingBar ratingBar;
    TextInputEditText commentEditor;

    public Rating() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ratedBy = Long.parseLong(this.getArguments().getString("ratedBy"));
        ratedTo = Long.parseLong(this.getArguments().getString("ratedTo"));
        commentEditor = this.getView().findViewById(R.id.ratingCommentField);
        ratingBar = this.getView().findViewById(R.id.ratingIndicator);
        this.getView().findViewById(R.id.ratingButton).setOnClickListener(v -> {
            this.rateProvider();
        });
    }

    void rateProvider() {
        final Float rating = this.ratingBar.getRating();
        final String comment = this.commentEditor.getText().toString();

        // making the api call;
        final RateRequestModel rateRequestModel = RateRequestModel.builder()
                .ratedTo(this.ratedTo)
                .ratedBy(this.ratedBy)
                .rating(rating)
                .comment(comment)
                .build();
        APIClient.getInstance().getMyApi().rateProvider(rateRequestModel).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Rating.this.getActivity(), "User was rated successfully", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Rating.this.getActivity(), "User could not be rated.", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
