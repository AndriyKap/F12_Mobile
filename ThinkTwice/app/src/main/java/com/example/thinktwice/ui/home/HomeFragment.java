package com.example.thinktwice.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.widget.*;
import com.example.thinktwice.R;
import com.example.thinktwice.ui.DatabaseHelper;

public class HomeFragment extends Fragment {

//    private EditText titleInput, toCategoryInput, fromCategoryInput, detailsInput, sumInput;
//    private Button addButton;
//
//    private CheckBox isPlanned;
//    private DatePicker datePicker;
//    private DatabaseHelper databaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        datePicker = root.findViewById(R.id.datePicker);
//        titleInput = root.findViewById(R.id.title_input);
//        toCategoryInput = root.findViewById(R.id.toCategory_input);
//        fromCategoryInput = root.findViewById(R.id.fromCategory_input);
//        detailsInput = root.findViewById(R.id.details_input);
//        isPlanned = root.findViewById(R.id.scheduleCheckBox);
//        sumInput = root.findViewById(R.id.sum_input);
//        addButton = root.findViewById(R.id.add_button);
//
//        databaseHelper = new DatabaseHelper(getContext());

//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addTransactionToDatabase();
//            }
//        });


        Button openModalTransactionButton = root.findViewById(R.id.open_modal_transaction_button);
        openModalTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateTransactionDialog dialogFragment = new CreateTransactionDialog();
                dialogFragment.show(getParentFragmentManager(), "AddTransactionDialogFragment");
            }
        });

        Button openModalCategories = root.findViewById(R.id.open_category_button);
        openModalCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesDialogFragment categoriesDialogFragment = new CategoriesDialogFragment();
                categoriesDialogFragment.show(getParentFragmentManager(), "CategoriesDialogFragment");
            }
        });

        return root;
    }

//    private void addTransactionToDatabase() {
//        String title = titleInput.getText().toString().trim();
//        Integer toCategory = Integer.parseInt(toCategoryInput.getText().toString().trim());
//        Integer fromCategory = Integer.parseInt(fromCategoryInput.getText().toString().trim());
//        String details = detailsInput.getText().toString().trim();
//        String sumString = sumInput.getText().toString().trim();
//
//        // Convert sumString to integer or handle it accordingly based on your requirements
//        int sum = Integer.parseInt(sumString);
//
//        int day = datePicker.getDayOfMonth();
//        int month = datePicker.getMonth() + 1; // Month is 0-based, so add 1
//        int year = datePicker.getYear();
//        String dateString = year + "-" + month + "-" + day;
//
//
//        int planned = isPlanned.isChecked() ? 1 : 0;
//        // You may want to perform some validation here before adding to the database
//
//        // Add the transaction to the database
//        databaseHelper.addTransaction(title, details, dateString, sum, planned, toCategory, fromCategory);
//        // Replace the hardcoded values above with appropriate values based on your requirements
//
//        // Clear EditText fields after adding the transaction
//        toCategoryInput.setText("");
//        fromCategoryInput.setText("");
//        detailsInput.setText("");
//        sumInput.setText("");
//
//        isPlanned.setChecked(false);
//    }
}