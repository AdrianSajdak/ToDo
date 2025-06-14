package com.myapps.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;
import com.myapps.todoapp.viewmodel.TaskDetailsViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskDetailsActivity extends AppCompatActivity {

    private TaskDetailsViewModel viewModel;
    private TextInputEditText titleEditText, descriptionEditText;
    private Spinner categorySpinner, prioritySpinner;
    private Button deadlineButton, saveButton;
    private Calendar deadlineCalendar;
    private List<Category> categoryList;
    private SwitchMaterial recurringSwitch;
    private LinearLayout recurrenceOptionsLayout;
    private Spinner recurrenceSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        viewModel = new ViewModelProvider(this).get(TaskDetailsViewModel.class);

        titleEditText = findViewById(R.id.edit_text_task_title);
        descriptionEditText = findViewById(R.id.edit_text_task_description);
        categorySpinner = findViewById(R.id.spinner_category);
        prioritySpinner = findViewById(R.id.spinner_priority);
        deadlineButton = findViewById(R.id.button_deadline);
        saveButton = findViewById(R.id.button_save_task);
        recurringSwitch = findViewById(R.id.switch_recurring);
        recurrenceOptionsLayout = findViewById(R.id.layout_recurrence_options);
        recurrenceSpinner = findViewById(R.id.spinner_recurrence);

        setupRecurrenceOptions();

        deadlineCalendar = Calendar.getInstance();

        setupPrioritySpinner();
        setupCategorySpinner();
        setupDeadlinePicker();

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void setupRecurrenceOptions() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recurrence_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurrenceSpinner.setAdapter(adapter);

        recurringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurrenceOptionsLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void setupPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
    }

    private void setupCategorySpinner() {
        viewModel.getAllCategories().observe(this, categories -> {
            this.categoryList = categories;
            ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        });
    }

    private void setupDeadlinePicker() {
        deadlineButton.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
                deadlineCalendar.set(Calendar.YEAR, year);
                deadlineCalendar.set(Calendar.MONTH, month);
                deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDeadlineButtonText();
            };

            new DatePickerDialog(TaskDetailsActivity.this, dateSetListener,
                    deadlineCalendar.get(Calendar.YEAR),
                    deadlineCalendar.get(Calendar.MONTH),
                    deadlineCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateDeadlineButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        deadlineButton.setText(sdf.format(deadlineCalendar.getTime()));
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Tytuł zadania jest wymagany!", Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        if (selectedCategory == null) {
            Toast.makeText(this, "Proszę najpierw dodać kategorię!", Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task();
        newTask.setTitle(title);
        newTask.setDescription(descriptionEditText.getText().toString().trim());
        newTask.setCategoryId(selectedCategory.getId());
        newTask.setPriority(prioritySpinner.getSelectedItemPosition());
        newTask.setCreationDate(System.currentTimeMillis());

        if (deadlineButton.getText().toString().contains("/")) {
            newTask.setDeadline(deadlineCalendar.getTimeInMillis());
        }

        newTask.setCompleted(false);
        boolean isRecurring = recurringSwitch.isChecked();
        newTask.setRecurring(isRecurring);
        if (isRecurring) {
            // Zapisujemy regułę jako prosty tekst, np. "DAILY", "WEEKLY"
            // W bardziej zaawansowanej wersji stworzylibyśmy bardziej złożony system reguł
            String[] recurrenceValues = {"DAILY", "WEEKLY", "MONTHLY"};
            newTask.setRecurrenceRule(recurrenceValues[recurrenceSpinner.getSelectedItemPosition()]);
        } else {
            // Zapisz deadline tylko dla zadań niecyklicznych
            if (deadlineButton.getText().toString().contains("/")) {
                newTask.setDeadline(deadlineCalendar.getTimeInMillis());
            }
        }

        viewModel.insert(newTask);
        Toast.makeText(this, "Zadanie zapisane", Toast.LENGTH_SHORT).show();
        finish();
    }
}
