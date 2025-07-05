package com.myapps.todoapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.todoapp.R;
import com.myapps.todoapp.ui.adapter.TaskListAdapter;
import com.myapps.todoapp.viewmodel.MainViewModel;


public class TasksListFragment extends Fragment {

    private MainViewModel mainViewModel;
    private RecyclerView recyclerView;
    private TaskListAdapter adapter;

    public static TasksListFragment newInstance() {
        return new TasksListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        adapter = new TaskListAdapter(new TaskListAdapter.TaskClickListener() {
            @Override
            public void onCategoryClick(long categoryId) {
                mainViewModel.toggleCategoryExpansion(categoryId);
            }

            @Override
            public void onTaskClick(long taskId) {
                mainViewModel.toggleTaskExpansion(taskId);
            }

            @Override
            public void onTaskCompleted(long taskId, boolean isCompleted) {
                mainViewModel.updateTaskCompletion(taskId, isCompleted);
            }
        });

        recyclerView.setAdapter(adapter);

        mainViewModel.getDisplayList().observe(getViewLifecycleOwner(), displayItems -> {
            adapter.submitList(displayItems);
        });
    }
}