package com.myapps.todoapp.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.myapps.todoapp.R;

public class AddCategoryDialogFragment extends DialogFragment {

    public static final String TAG = "AddCategoryDialog";
    public static final String REQUEST_KEY = "addCategoryRequest";
    public static final String KEY_CATEGORY_NAME = "categoryName";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);
        final EditText categoryNameEditText = view.findViewById(R.id.edit_text_category_name);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nowa kategoria")
                .setView(view)
                .setPositiveButton("Dodaj", (dialogInterface, i) -> {
                    String categoryName = categoryNameEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(categoryName)) {
                        Bundle result = new Bundle();
                        result.putString(KEY_CATEGORY_NAME, categoryName);
                        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
                    }
                })
                .setNegativeButton("Anuluj", (dialogInterface, i) -> {
                    dismiss();
                })
                .create();

        return dialog;
    }
}
