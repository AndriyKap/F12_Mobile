package com.example.thinktwice.ui.home;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.thinktwice.R;
import com.example.thinktwice.ui.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDialogFragment extends DialogFragment {

//    private CategoriesDialogViewModel mViewModel;

//    public static CategoriesDialogFragment newInstance() {
//        return new CategoriesDialogFragment();
//    }

    private DatabaseHelper dbHelper;
    private EditText categoryNameInput, percentageAmountInput;
    private Spinner categoryTypeSpinner;
    private Button createCategoryButton;

    private static class CategoryData {
        private int id;
        private String title;
        private double percentageAmount;
        private String type;

        public CategoryData(int id, String title, double percentageAmount, String type) {
            this.id = id;
            this.title = title;
            this.percentageAmount = percentageAmount;
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public double getPercentageAmount() {
            return percentageAmount;
        }

        public String getType() {
            return type;
        }
    }

    private List<CategoryData> getCategoryDataFromDatabase() {
        List<CategoryData> categoryDataList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.CATEGORY_TABLE_NAME +
                " WHERE " + DatabaseHelper.CATEGORY_COLUMN_PERCENTAGEAMOUNT + " > 0", null);

        if (cursor.moveToFirst()) {
            do {
                int categoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CATEGORY_COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_COLUMN_TITLE));
                double percentageAmount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CATEGORY_COLUMN_PERCENTAGEAMOUNT));
                String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_COLUMN_TYPE));

                categoryDataList.add(new CategoryData(categoryId, title, percentageAmount, type));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categoryDataList;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(requireContext());
        View view = inflater.inflate(R.layout.fragment_categories_dialog, container, false);

        categoryTypeSpinner = view.findViewById(R.id.category_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.category_types_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryTypeSpinner.setAdapter(adapter);

        categoryNameInput = view.findViewById(R.id.category_name);
        percentageAmountInput = view.findViewById(R.id.percentage_amount_input);
        createCategoryButton = view.findViewById(R.id.create_category_button);

        createCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {addCategoryToDatabase();}
        });


        categoryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if (selectedType.equals("Витрати")) {
                    percentageAmountInput.setVisibility(View.VISIBLE);
                } else {
                    percentageAmountInput.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the scenario where nothing is selected
            }
        });

        return view;
    }

    public void addCategoryToDatabase() {
        String name = categoryNameInput.getText().toString().trim();
        Integer isGeneral = 0;
        String type = categoryTypeSpinner.getSelectedItem().toString().trim();

        String percentageString = percentageAmountInput.getText().toString().trim();
        Double percentage = percentageString.isEmpty() ? 0.0 : Double.parseDouble(percentageString);

        dbHelper.addCategory(name, isGeneral, percentage / 100, type);
    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(CategoriesDialogViewModel.class);
//        // TODO: Use the ViewModel
//    }

}