package com.gianteyes.gaarigarapp.screens.home_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.adapters.SearchStandardServiceAdapter;
import com.gianteyes.gaarigarapp.models.Category;
import com.gianteyes.gaarigarapp.models.StandardServicesResponse;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends Fragment {

    //hooks
    Chip filtersShowButton;
    LinearLayout filtersLayout;
    TextInputEditText searchBoxText;
    RecyclerView StandardServicesRV;
    SearchStandardServiceAdapter standardServiceAdapter;
    Button searchButton;
    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<StandardServicesResponse> data = new ArrayList<>();
    RangeSlider rangeSlider;
    Spinner categoriesSpinner;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loadCategories();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.searchBoxText = view.findViewById(R.id.searchServiceInputField);
        this.filtersShowButton = view.findViewById(R.id.filtersShowButton);
        this.rangeSlider = view.findViewById(R.id.priceRangeSlider);
        this.categoriesSpinner = view.findViewById(R.id.categoriesSpinner);
        this.filtersLayout = view.findViewById(R.id.filterScreen);
        this.filtersShowButton.setOnClickListener(v -> {
            if (filtersLayout.getVisibility() == View.VISIBLE) {
                filtersLayout.setVisibility(View.GONE);
            } else {
                filtersLayout.setVisibility(View.VISIBLE);
            }
        });

        this.StandardServicesRV = view.findViewById(R.id.standardServicesGridRV);
        this.searchButton = view.findViewById(R.id.searchButton);
        this.searchButton.setOnClickListener(v -> {
            this.searchStandardServices(this.searchBoxText.getText().toString());
        });

        StandardServicesRV.setHasFixedSize(true);
        StandardServicesRV.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false));
        this.standardServiceAdapter = new SearchStandardServiceAdapter(this.getContext(), data);
        this.StandardServicesRV.setAdapter(this.standardServiceAdapter);
    }

    void loadCategories() {
        APIClient.getInstance().getMyApi().getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(final Call<ArrayList<Category>> call, final Response<ArrayList<Category>> response) {
                if (response.isSuccessful()) {
                    Search.this.categories = response.body();
                    final ArrayAdapter cAdapter = new ArrayAdapter(getActivity(), R.layout.layout_category_spinner, categories);
                    cAdapter.setDropDownViewResource(R.layout.layout_category_spinner);
                    categoriesSpinner.setAdapter(cAdapter);
                } else {
                    Toast.makeText(Search.this.getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<Category>> call, final Throwable t) {
                Toast.makeText(Search.this.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchStandardServices(final String query) {
        // extract filters(if any)
        Long minPrice = Long.valueOf(Math.round(rangeSlider.getValues().get(0)));
        Long maxPrice = Long.valueOf(Math.round(rangeSlider.getValues().get(1)));
        String category = categoriesSpinner.getSelectedItem().toString();
        Long categoryId = null;
        for (Category c : categories) {
            if (c.getName().equals(category)) {
                categoryId = c.getId();
                break;
            }
        }
        APIClient.getInstance().getMyApi().searchStandardServices(query, categoryId, maxPrice, minPrice).enqueue(new Callback<ArrayList<StandardServicesResponse>>() {
            @Override
            public void onResponse(final Call<ArrayList<StandardServicesResponse>> call, final Response<ArrayList<StandardServicesResponse>> response) {
                if (response.isSuccessful()) {
                    Search.this.data = response.body();
                    standardServiceAdapter.addData(Search.this.data);
                    // notify in UI thread
                    Search.this.getActivity().runOnUiThread(() -> standardServiceAdapter.notifyDataSetChanged());
                } else {
                    Toast.makeText(Search.this.getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<ArrayList<StandardServicesResponse>> call, final Throwable t) {
                Toast.makeText(Search.this.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
