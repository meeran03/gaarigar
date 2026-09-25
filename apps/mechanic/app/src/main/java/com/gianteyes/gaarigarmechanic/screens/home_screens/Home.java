package com.gianteyes.gaarigarmechanic.screens.home_screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarmechanic.R;


public class Home extends Fragment {
    NavController navController;
    RecyclerView popularServices;
    RecyclerView.Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(this.getView());
        this.getView().findViewById(R.id.manageStandardService).setOnClickListener(v -> {
            navController.navigate(R.id.action_home2_to_standardService);
        });

        this.getView().findViewById(R.id.requestVulcanizer).setOnClickListener(v -> {
            final Bundle bundle = new Bundle();
            bundle.putString("mechanicType", "Vulcanizer");
            bundle.putString("type", "Mechanic");
        });

        this.getView().findViewById(R.id.requestFuel).setOnClickListener(v -> {
            final Bundle bundle = new Bundle();
            bundle.putString("type", "PetrolPump");
        });
    }

}
