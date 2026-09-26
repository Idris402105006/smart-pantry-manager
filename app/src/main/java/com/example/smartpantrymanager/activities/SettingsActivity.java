package com.example.smartpantrymanager.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME =
            "smart_pantry_preferences";

    private static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts_enabled";

    private MaterialSwitch switchExpiryAlerts;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_settings
        );

        sharedPreferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        initialiseViews();
        loadPreferences();
        setupListeners();
    }

    private void initialiseViews() {

        switchExpiryAlerts =
                findViewById(
                        R.id.switchExpiryAlerts
                );
    }

    private void loadPreferences() {

        boolean expiryAlertsEnabled =
                sharedPreferences.getBoolean(
                        KEY_EXPIRY_ALERTS,
                        true
                );

        switchExpiryAlerts.setChecked(
                expiryAlertsEnabled
        );
    }

    private void setupListeners() {

        switchExpiryAlerts
                .setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            sharedPreferences
                                    .edit()
                                    .putBoolean(
                                            KEY_EXPIRY_ALERTS,
                                            isChecked
                                    )
                                    .apply();
                        }
                );
    }
}