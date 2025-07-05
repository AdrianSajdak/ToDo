package com.myapps.todoapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.myapps.todoapp.R;

public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "todoapp_settings";
    private static final String KEY_THEME_MODE = "theme_mode";
    
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    private SharedPreferences prefs;
    private RadioGroup themeRadioGroup;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Setup toolbar with back button
        setupToolbar();
        
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        themeRadioGroup = view.findViewById(R.id.theme_radio_group);
        
        // Set current theme selection
        int currentTheme = prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM);
        switch (currentTheme) {
            case THEME_LIGHT:
                themeRadioGroup.check(R.id.radio_light_theme);
                break;
            case THEME_DARK:
                themeRadioGroup.check(R.id.radio_dark_theme);
                break;
            case THEME_SYSTEM:
            default:
                themeRadioGroup.check(R.id.radio_system_theme);
                break;
        }
        
        // Handle theme changes
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedTheme = THEME_SYSTEM;
            int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            
            if (checkedId == R.id.radio_light_theme) {
                selectedTheme = THEME_LIGHT;
                nightMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radio_dark_theme) {
                selectedTheme = THEME_DARK;
                nightMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else if (checkedId == R.id.radio_system_theme) {
                selectedTheme = THEME_SYSTEM;
                nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
            
            // Save preference
            prefs.edit().putInt(KEY_THEME_MODE, selectedTheme).apply();
            
            // Apply theme
            AppCompatDelegate.setDefaultNightMode(nightMode);
        });
    }
    
    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            activity.getSupportActionBar().setTitle("Ustawienia");
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Reset toolbar when leaving settings
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
            activity.getSupportActionBar().setTitle("ToDoApp");
        }
    }
}
