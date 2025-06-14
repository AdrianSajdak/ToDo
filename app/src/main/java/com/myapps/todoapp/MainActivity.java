package com.myapps.todoapp;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.ui.adapter.CategoryAdapter;
import com.myapps.todoapp.ui.dialog.AddCategoryDialogFragment;
import com.myapps.todoapp.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Moje Zadania");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final CategoryAdapter adapter = new CategoryAdapter(mainViewModel, this);
        recyclerView.setAdapter(adapter);

        mainViewModel.getAllCategories().observe(this, categories -> {
            adapter.setCategories(categories);
        });

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(view -> {
            showPopupMenu(view);
        });

        setupAddCategoryResultListener();
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_add_category) {
                new AddCategoryDialogFragment().show(getSupportFragmentManager(), AddCategoryDialogFragment.TAG);
                return true;
            } else if (itemId == R.id.action_add_task) {
                Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void setupAddCategoryResultListener() {
        getSupportFragmentManager().setFragmentResultListener(AddCategoryDialogFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
            String categoryName = bundle.getString(AddCategoryDialogFragment.KEY_CATEGORY_NAME);
            if (categoryName != null && !categoryName.isEmpty()) {
                mainViewModel.insert(new Category(categoryName));
            }
        });
    }
}