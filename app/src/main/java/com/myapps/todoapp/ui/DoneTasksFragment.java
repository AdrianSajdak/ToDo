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
import com.myapps.todoapp.data.model.Task;
import com.myapps.todoapp.ui.adapter.DoneTaskAdapter;
import com.myapps.todoapp.viewmodel.DoneTasksViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoneTasksFragment extends Fragment {

    private DoneTasksViewModel viewModel;
    private RecyclerView recyclerView;

    public static DoneTasksFragment newInstance() {
        return new DoneTasksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_done_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_done_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DoneTasksViewModel.class);
        
        // Implementacja DoneTaskClickListener
        DoneTaskAdapter.DoneTaskClickListener clickListener = new DoneTaskAdapter.DoneTaskClickListener() {
            @Override
            public void onTaskRestore(long taskId) {
                viewModel.restoreTask(taskId);
            }

            @Override
            public void onTaskDelete(long taskId) {
                viewModel.deleteTask(taskId);
            }
        };
        
        final DoneTaskAdapter adapter = new DoneTaskAdapter(clickListener);
        recyclerView.setAdapter(adapter);

        viewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.submitList(groupTasksByDate(tasks));
        });
    }

    private List<Object> groupTasksByDate(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return new ArrayList<>();

        List<Object> groupedList = new ArrayList<>();
        String lastHeader = "";

        for (Task task : tasks) {
            String currentHeader = getHeaderForDate(task.getCompletionDate());
            if (!currentHeader.equals(lastHeader)) {
                groupedList.add(currentHeader);
                lastHeader = currentHeader;
            }
            groupedList.add(task);
        }
        return groupedList;
    }

    private String getHeaderForDate(Long timestamp) {
        if (timestamp == null) return "Wcześniej";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}