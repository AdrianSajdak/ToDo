package com.myapps.todoapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.myapps.todoapp.MainActivity;
import com.myapps.todoapp.R;
import com.myapps.todoapp.TaskDetailsActivity;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.ui.dialog.AddCategoryDialogFragment;
import com.myapps.todoapp.viewmodel.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksListFragment extends Fragment {

    private MainViewModel mainViewModel;

    public TasksListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final CategoryAdapter adapter = new CategoryAdapter(mainViewModel, getViewLifecycleOwner());
        recyclerView.setAdapter(adapter);

        mainViewModel.getAllCategories().observe(getViewLifecycleOwner(), adapter::setCategories);

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(this::showPopupMenu);

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