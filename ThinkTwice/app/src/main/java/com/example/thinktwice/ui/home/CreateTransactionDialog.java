package com.example.thinktwice.ui.home;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thinktwice.R;
import com.example.thinktwice.ui.DatabaseHelper;
import com.example.thinktwice.ui.statistics.StatisticsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateTransactionDialog extends DialogFragment {

    private Spinner toCategoryInputSpinner, fromCategoryInputSpinner;
    private EditText  detailsInput, sumInput;
    private Button createTransactionButton;
    private TextView dataTextView;

    private CheckBox isPlanned;
    private DatePicker datePicker;
    private DatabaseHelper databaseHelper;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        databaseHelper = new DatabaseHelper(requireContext());

        View view = inflater.inflate(R.layout.fragment_create_transaction_dialog, container, false);
        View dataPickerFragment = inflater.inflate(R.layout.fragment_date_picker_dialog, container, false);

        datePicker = dataPickerFragment.findViewById(R.id.datePicker);

        fromCategoryInputSpinner = view.findViewById(R.id.fromCategory_spinner);
        toCategoryInputSpinner = view.findViewById(R.id.toCategory_spinner);

        List<CharSequence> categoryIncomesList = getCategoryDataByTypeFromDatabase("Дохід");
        List<CharSequence> categoryBalanceList = getCategoryDataByTypeFromDatabase("Баланс");
        List<CharSequence> categoryExpencesList = getCategoryDataByTypeFromDatabase("Витрати");

        List<CharSequence> fromCategoryList = new ArrayList<>();
        fromCategoryList.addAll(categoryIncomesList);
        fromCategoryList.addAll(categoryBalanceList);


        List<CharSequence> toCategoryList = new ArrayList<>();
        toCategoryList.addAll(categoryBalanceList);
        toCategoryList.addAll(categoryExpencesList);


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fromCategoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCategoryInputSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapt = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, toCategoryList);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toCategoryInputSpinner.setAdapter(adapt);


        detailsInput = view.findViewById(R.id.details_input);
        isPlanned = view.findViewById(R.id.scheduleCheckBox);
        sumInput = view.findViewById(R.id.sum_input);
        createTransactionButton = view.findViewById(R.id.create_transaction_button);
        dataTextView = view.findViewById(R.id.date_text_view);

        databaseHelper = new DatabaseHelper(getContext());

        createTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransactionToDatabase();
            }
        });


        isPlanned.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.show(getParentFragmentManager(), "DatePickerDialogFragment");

                dataTextView.setVisibility(View.GONE);
            }
            else {
                dataTextView.setVisibility(View.GONE);
            }
        });

        return view;
    }


    private List<CharSequence> getCategoryDataByTypeFromDatabase(String type) {
        List<CharSequence> categoryDataList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.CATEGORY_TABLE_NAME +
                " WHERE " + databaseHelper.CATEGORY_COLUMN_TYPE + " = '" + type + "'", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_COLUMN_TITLE));
                categoryDataList.add(title);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categoryDataList;
    }

    private int getCategoryIndexByTitle(CharSequence title)
    {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

       Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.CATEGORY_TABLE_NAME +
                " WHERE " + databaseHelper.CATEGORY_COLUMN_TITLE + " = '" + title + "'", null);

        int categoryIndex = -1;

        if (cursor.moveToFirst()) {
            categoryIndex = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return categoryIndex;
    }

    private void addTransactionToDatabase() {
        String title = "";

        CharSequence toCat_name = toCategoryInputSpinner.getSelectedItem().toString().trim();
        Integer toCategory = getCategoryIndexByTitle(toCat_name);

        CharSequence fromCat_name = fromCategoryInputSpinner.getSelectedItem().toString().trim();
        Integer fromCategory = getCategoryIndexByTitle(fromCat_name);

        String details = detailsInput.getText().toString().trim();
        String sumString = sumInput.getText().toString().trim();

        int sum = Integer.parseInt(sumString);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is 0-based, so add 1
        int year = datePicker.getYear();
        String dateString = year + "-" + month + "-" + day;

        int planned = isPlanned.isChecked() ? 1 : 0;

        databaseHelper.addTransaction(title, details, dateString, sum, planned, toCategory, fromCategory);

        detailsInput.setText("");
        sumInput.setText("");

        isPlanned.setChecked(false);
    }
}