package com.myapps.todoapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.todoapp.R;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private final MainViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;

    public CategoryAdapter(MainViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category currentCategory = categories.get(position);
        holder.textViewCategoryName.setText(currentCategory.getName());

        holder.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        final TaskAdapter taskAdapter = new TaskAdapter(viewModel);
        holder.tasksRecyclerView.setAdapter(taskAdapter);

        viewModel.loadTasksForCategory(currentCategory.getId());

        viewModel.getTaskTreeForCategory(currentCategory.getId()).observe(lifecycleOwner, taskDisplayItems -> {
            taskAdapter.setTaskItems(taskDisplayItems);
        });

        viewModel.tasksForActiveCategory.observe(lifecycleOwner, taskDisplayItems -> {
            if (taskDisplayItems != null && !taskDisplayItems.isEmpty()) {
                if (taskDisplayItems.get(0).task.getCategoryId() == currentCategory.getId()) {
                    taskAdapter.setTaskItems(taskDisplayItems);
                }
            } else {
                taskAdapter.setTaskItems(new ArrayList<>());
            }
        });


        holder.itemView.setOnClickListener(v -> {
            boolean isVisible = holder.tasksRecyclerView.getVisibility() == View.VISIBLE;
            holder.tasksRecyclerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCategoryName;
        private final RecyclerView tasksRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.text_view_category_name);
            tasksRecyclerView = itemView.findViewById(R.id.recycler_view_tasks);
        }
    }
}