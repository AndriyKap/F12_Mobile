package com.example.thinktwice.ui.home;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.thinktwice.R;
import com.example.thinktwice.ui.DatabaseHelper;

public class CreateTransactionDialog extends DialogFragment {
//    private CreateTransactionDialogViewModel mViewModel;
//
//    public static CreateTransactionDialog newInstance() {
//        return new CreateTransactionDialog();
//    }

    private EditText toCategoryInput, fromCategoryInput, detailsInput, sumInput;
    private Button createTransactionButton;

    private CheckBox isPlanned;
    private DatePicker datePicker;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_transaction_dialog, container, false);
        View dataPickerFragment = inflater.inflate(R.layout.fragment_date_picker_dialog, container, false);

        datePicker = dataPickerFragment.findViewById(R.id.datePicker);
//        titleInput = view.findViewById(R.id.title_input);
        toCategoryInput = view.findViewById(R.id.toCategory_input);
        fromCategoryInput = view.findViewById(R.id.fromCategory_input);
        detailsInput = view.findViewById(R.id.details_input);
        isPlanned = view.findViewById(R.id.scheduleCheckBox);
        sumInput = view.findViewById(R.id.sum_input);
        createTransactionButton = view.findViewById(R.id.create_transaction_button);

        databaseHelper = new DatabaseHelper(getContext());

        createTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransactionToDatabase();
            }
        });


        isPlanned.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Показати DatePickerDialogFragment при виборі CheckBox
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.show(getParentFragmentManager(), "DatePickerDialogFragment");
            }
        });


        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(CreateTransactionDialogViewModel.class);
//        // TODO: Use the ViewModel
//    }

    private void addTransactionToDatabase() {
        String title = "";
        Integer toCategory = Integer.parseInt(toCategoryInput.getText().toString().trim());
        Integer fromCategory = Integer.parseInt(fromCategoryInput.getText().toString().trim());
        String details = detailsInput.getText().toString().trim();
        String sumString = sumInput.getText().toString().trim();

        int sum = Integer.parseInt(sumString);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is 0-based, so add 1
        int year = datePicker.getYear();
        String dateString = year + "-" + month + "-" + day;

        int planned = isPlanned.isChecked() ? 1 : 0;

        databaseHelper.addTransaction(title, details, dateString, sum, planned, toCategory, fromCategory);

        toCategoryInput.setText("");
        fromCategoryInput.setText("");
        detailsInput.setText("");
        sumInput.setText("");

        isPlanned.setChecked(false);
    }
}