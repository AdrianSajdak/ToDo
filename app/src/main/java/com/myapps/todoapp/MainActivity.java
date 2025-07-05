package com.myapps.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private FloatingActionButton fab;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicjalizacja FAB
        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddOptionsDialog());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
            
            // Set up action bar with nav controller for back button support
            NavigationUI.setupActionBarWithNavController(this, navController);
            
            // Control FAB visibility based on current fragment
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.settingsFragment) {
                    fab.hide();
                    bottomNav.setVisibility(View.GONE);
                } else {
                    fab.show();
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            if (navController != null) {
                navController.navigate(R.id.settingsFragment);
            }
            return true;
        }
        // Handle up button (back arrow) in toolbar
        else if (item.getItemId() == android.R.id.home) {
            return NavigationUI.navigateUp(navController, (androidx.drawerlayout.widget.DrawerLayout) null);
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (androidx.drawerlayout.widget.DrawerLayout) null)
                || super.onSupportNavigateUp();
    }

    private void showAddOptionsDialog() {
        Log.d("MainActivity", "FAB clicked - showing dialog");
        String[] options = {"Nowe zadanie", "Nowa kategoria"};
        
        new AlertDialog.Builder(this)
                .setTitle("Dodaj nowy element")
                .setItems(options, (dialog, which) -> {
                    Log.d("MainActivity", "Dialog option selected: " + which);
                    if (which == 0) {
                        // Dodaj zadanie
                        startTaskDetailsActivity();
                    } else if (which == 1) {
                        // Dodaj kategorię
                        showAddCategoryDialog();
                    }
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void startTaskDetailsActivity() {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        startActivity(intent);
    }

    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        TextInputEditText categoryNameInput = dialogView.findViewById(R.id.edit_text_category_name);

        new AlertDialog.Builder(this)
                .setTitle("Nowa kategoria")
                .setView(dialogView)
                .setPositiveButton("Dodaj", (dialog, which) -> {
                    String categoryName = categoryNameInput.getText() != null ? 
                            categoryNameInput.getText().toString().trim() : "";
                    
                    if (validateCategoryName(categoryName)) {
                        try {
                            Category category = new Category();
                            category.setName(categoryName);
                            mainViewModel.insertCategory(category);
                            Toast.makeText(this, "Kategoria została dodana", Toast.LENGTH_SHORT).show();
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }
    
    /**
     * Validates category name according to business rules
     * @param categoryName Name to validate
     * @return true if valid, false otherwise
     */
    private boolean validateCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            Toast.makeText(this, "Nazwa kategorii nie może być pusta", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (categoryName.length() < 2) {
            Toast.makeText(this, "Nazwa kategorii musi mieć co najmniej 2 znaki", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (categoryName.length() > 50) {
            Toast.makeText(this, "Nazwa kategorii nie może być dłuższa niż 50 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }
}