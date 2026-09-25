package com.gianteyes.gaarigarmechanic.screens;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.screens.home_screens.Home;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    final Fragment fragment1 = new Home();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        NavigationBarView navigation = findViewById(R.id.bottom_navigation);
        final NavHostFragment navHostFragment = (NavHostFragment) this.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_home);

        if (navHostFragment != null) {
            final NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navigation, navController);
        }
    }

    @Override
    protected void attachBaseContext(Context baseContext) {
        final Context newContext;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            final DisplayMetrics displayMetrics = baseContext.getResources().getDisplayMetrics();
            final Configuration configuration = baseContext.getResources().getConfiguration();

            if (displayMetrics.densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE) {
                // Current density is different from Default Density. Override it
                configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
                newContext = baseContext.createConfigurationContext(configuration);
            } else {
                // Same density. Just use same context
                newContext = baseContext;
            }
        } else {
            // Old API. Screen zoom not supported
            newContext = baseContext;
        }
        super.attachBaseContext(newContext);
    }

}
