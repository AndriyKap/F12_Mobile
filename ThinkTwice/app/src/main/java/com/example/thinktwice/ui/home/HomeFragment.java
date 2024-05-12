package com.example.thinktwice.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.thinktwice.databinding.FragmentHomeBinding;
import com.example.thinktwice.ui.DatabaseHelper;

import java.io.Console;

public class HomeFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dbHelper = new DatabaseHelper(requireContext());


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

        displayPlannedTransactions();
        displayTransactions();

        return root;
    }

    private void displayPlannedTransactions()
    {
        String query = "SELECT * FROM " + dbHelper.TABLE_NAME + " WHERE " + dbHelper.COLUMN_PLANNED + " = 1;";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            View root = binding.getRoot();
            TableLayout plannedTransactionsTable = root.findViewById(R.id.plannedTransactionsTable);

            while (cursor.moveToNext()) {
                // Отримати дані про транзакцію
                String details = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                int amount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                int fromCategoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_FROM));
                int toCategoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TO));

                // Отримати назви категорій from та to
                String fromCategoryName = dbHelper.getCategoryName(String.valueOf(fromCategoryId));
                String toCategoryName = dbHelper.getCategoryName(String.valueOf(toCategoryId));

                // Створити новий TableRow
                TableRow newRow = new TableRow(requireContext());

                // Додати TextView для кожного поля в транзакції
                addTextViewToTableRow(newRow, fromCategoryName);
                addTextViewToTableRow(newRow, toCategoryName);
                addTextViewToTableRow(newRow, details);
                addTextViewToTableRow(newRow, date);
                addTextViewToTableRow(newRow, String.valueOf(amount));

                // Додати TableRow до TableLayout
                plannedTransactionsTable.addView(newRow);
            }
            cursor.close();
        }
    }

    private void displayTransactions() {
        // Отримати всі транзакції
        Cursor cursor = dbHelper.getAllTransactions();
        View root = binding.getRoot();
        TableLayout transactionsTable = root.findViewById(R.id.transactionsTable);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Отримати дані про транзакцію
                String details = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DETAILS));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                int amount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                int fromCategoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_FROM));
                int toCategoryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TO));

                // Отримати назви категорій from та to
                String fromCategoryName = dbHelper.getCategoryName(String.valueOf(fromCategoryId));
                String toCategoryName = dbHelper.getCategoryName(String.valueOf(toCategoryId));

                // Створити новий TableRow
                TableRow newRow = new TableRow(requireContext());

                // Додати TextView для кожного поля в транзакції
                addTextViewToTableRow(newRow, fromCategoryName);
                addTextViewToTableRow(newRow, toCategoryName);
                addTextViewToTableRow(newRow, details);
                addTextViewToTableRow(newRow, date);
                addTextViewToTableRow(newRow, String.valueOf(amount));

                // Додати TableRow до TableLayout
                transactionsTable.addView(newRow);
            }
            cursor.close();
        }
    }

    private void addTextViewToTableRow(TableRow row, String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setMaxLines(1);
        textView.setTextSize(10);
        textView.setTextColor(Color.GRAY);
        textView.setPadding(3, 0, 3, 0);
        textView.setGravity(Gravity.CENTER);
        row.addView(textView);
    }

}