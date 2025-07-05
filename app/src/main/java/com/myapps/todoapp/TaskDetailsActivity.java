package com.myapps.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.materialswitch.MaterialSwitch;
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
    private AutoCompleteTextView categorySpinner, prioritySpinner;
    private Button deadlineButton, saveButton;
    private Calendar deadlineCalendar;
    private List<Category> categoryList;
    private MaterialSwitch recurringSwitch;
    private LinearLayout recurrenceOptionsLayout, deadlineLayout;
    private AutoCompleteTextView recurrenceSpinner;


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
        deadlineLayout = findViewById(R.id.layout_deadline);

        setupRecurrenceOptions();

        deadlineCalendar = Calendar.getInstance();

        setupPrioritySpinner();
        setupCategorySpinner();
        setupDeadlinePicker();
        setupToolbar();

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecurrenceOptions() {
        String[] recurrenceOptions = getResources().getStringArray(R.array.recurrence_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_dropdown_item_1line, recurrenceOptions);
        recurrenceSpinner.setAdapter(adapter);

        recurringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurrenceOptionsLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            deadlineLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });
    }

    private void setupPrioritySpinner() {
        String[] priorities = getResources().getStringArray(R.array.priorities);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, priorities);
        prioritySpinner.setAdapter(adapter);
    }

    private void setupCategorySpinner() {
        viewModel.getAllCategories().observe(this, categories -> {
            this.categoryList = categories;
            String[] categoryNames = new String[categories.size()];
            for (int i = 0; i < categories.size(); i++) {
                categoryNames[i] = categories.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, categoryNames);
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
        // Validate task title
        if (!validateTaskTitle()) {
            return;
        }
        
        // Validate category selection
        Category selectedCategory = validateAndGetSelectedCategory();
        if (selectedCategory == null) {
            return;
        }

        Task newTask = new Task();
        newTask.setTitle(titleEditText.getText().toString().trim());
        newTask.setDescription(descriptionEditText.getText().toString().trim());
        newTask.setCategoryId(selectedCategory.getId());
        
        // Get priority from dropdown text
        String priorityText = prioritySpinner.getText().toString();
        String[] priorities = getResources().getStringArray(R.array.priorities);
        int priorityIndex = 0;
        for (int i = 0; i < priorities.length; i++) {
            if (priorities[i].equals(priorityText)) {
                priorityIndex = i;
                break;
            }
        }
        newTask.setPriority(priorityIndex);
        newTask.setCreationDate(System.currentTimeMillis());

        if (!recurringSwitch.isChecked() && deadlineButton.getText().toString().contains("/")) {
            newTask.setDeadline(deadlineCalendar.getTimeInMillis());
        }

        newTask.setCompleted(false);
        boolean isRecurring = recurringSwitch.isChecked();
        newTask.setRecurring(isRecurring);
        
        if (isRecurring) {
            // Get recurrence from dropdown text
            String recurrenceText = recurrenceSpinner.getText().toString();
            String[] recurrenceOptions = getResources().getStringArray(R.array.recurrence_options);
            String[] recurrenceValues = {"DAILY", "WEEKLY", "MONTHLY"};
            for (int i = 0; i < recurrenceOptions.length; i++) {
                if (recurrenceOptions[i].equals(recurrenceText)) {
                    newTask.setRecurrenceRule(recurrenceValues[i]);
                    break;
                }
            }
        }

        viewModel.insert(newTask);
        Toast.makeText(this, "Zadanie zapisane", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    /**
     * Validates task title according to business rules
     * @return true if title is valid, false otherwise
     */
    private boolean validateTaskTitle() {
        String title = titleEditText.getText().toString().trim();
        
        // Check if title is empty
        if (TextUtils.isEmpty(title)) {
            titleEditText.setError("Tytuł zadania jest wymagany");
            titleEditText.requestFocus();
            Toast.makeText(this, "Tytuł zadania jest wymagany!", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check minimum length
        if (title.length() < 3) {
            titleEditText.setError("Tytuł musi mieć co najmniej 3 znaki");
            titleEditText.requestFocus();
            Toast.makeText(this, "Tytuł musi mieć co najmniej 3 znaki", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Check maximum length
        if (title.length() > 100) {
            titleEditText.setError("Tytuł nie może być dłuższy niż 100 znaków");
            titleEditText.requestFocus();
            Toast.makeText(this, "Tytuł nie może być dłuższy niż 100 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        // Clear any previous errors
        titleEditText.setError(null);
        return true;
    }
    
    /**
     * Validates category selection and returns selected category
     * @return selected Category object if valid, null otherwise
     */
    private Category validateAndGetSelectedCategory() {
        String selectedCategoryName = categorySpinner.getText().toString().trim();
        
        // Check if category is selected
        if (TextUtils.isEmpty(selectedCategoryName)) {
            categorySpinner.setError("Kategoria jest wymagana");
            categorySpinner.requestFocus();
            Toast.makeText(this, "Proszę wybrać kategorię!", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        // Check if category exists in the list
        if (categoryList == null || categoryList.isEmpty()) {
            Toast.makeText(this, "Brak dostępnych kategorii. Proszę najpierw dodać kategorię!", Toast.LENGTH_LONG).show();
            return null;
        }
        
        Category selectedCategory = null;
        for (Category category : categoryList) {
            if (category.getName().equals(selectedCategoryName)) {
                selectedCategory = category;
                break;
            }
        }
        
        if (selectedCategory == null) {
            categorySpinner.setError("Wybrana kategoria nie istnieje");
            categorySpinner.requestFocus();
            Toast.makeText(this, "Wybrana kategoria nie istnieje. Proszę wybrać kategorię z listy!", Toast.LENGTH_SHORT).show();
            return null;
        }
        
        // Clear any previous errors
        categorySpinner.setError(null);
        return selectedCategory;
    }
}
