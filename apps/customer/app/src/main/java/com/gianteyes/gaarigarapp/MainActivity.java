package com.gianteyes.gaarigarapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
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
