package com.myapps.todoapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myapps.todoapp.R;
import com.myapps.todoapp.viewmodel.DoneTasksViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoneTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoneTasksFragment extends Fragment {

    private DoneTasksViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_done_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Wewnątrz onViewCreated w DoneTasksFragment
        viewModel = new ViewModelProvider(this).get(DoneTasksViewModel.class);
        final DoneTaskAdapter adapter = new DoneTaskAdapter();
        recyclerView.setAdapter(adapter);

        viewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.submitList(groupTasksByDate(tasks));
        });

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
            // Tu zaimplementuj logikę porównywania dat
            // np. używając Calendar do sprawdzania czy to Dzisiaj, Wczoraj, Ten tydzień itp.
            // Poniżej bardzo uproszczona wersja:
            if (timestamp == null) return "Wcześniej";
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}