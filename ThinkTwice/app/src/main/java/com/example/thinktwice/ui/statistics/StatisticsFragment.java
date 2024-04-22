package com.example.thinktwice.ui.statistics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thinktwice.databinding.FragmentStatisticsBinding;
import com.example.thinktwice.ui.dashboard.DashboardViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.example.thinktwice.ui.DatabaseHelper;

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private DatabaseHelper dbHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(requireContext());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        String firstLetterCapitalizedDay = currentDate.substring(0, 1).toUpperCase() + currentDate.substring(1);

        binding.textViewDate.setText(firstLetterCapitalizedDay);

        BarChart barChart = binding.barChart;
        float barDistance = 0.1f;

        List<BarEntry> incomeEntries = getIncomeDataFromDatabase();

        BarDataSet barDataSet = new BarDataSet(incomeEntries, "Сума");
        barDataSet.setForm(Legend.LegendForm.CIRCLE);
        Legend legend = barChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        BarData barDataSum = new BarData(barDataSet);
        barDataSum.setBarWidth(0.3f);
        barChart.setData(barDataSum);

        List<BarEntry> expensesEntries = getExpensesDataFromDatabase();

        BarDataSet expensesDataSet = new BarDataSet(expensesEntries, "Витрати");
        expensesDataSet.setForm(Legend.LegendForm.CIRCLE);
        expensesDataSet.setColor(Color.RED);
        expensesDataSet.setValueTextColor(Color.RED);
        Legend expensesLegend = barChart.getLegend();
        expensesLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        BarData barDataExes = new BarData(barDataSet, expensesDataSet);
        barDataExes.setBarWidth(0.3f);
        barChart.setData(barDataExes);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);

        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new MyXAxisValueFormatter());


        //StatisticsViewModel statisticsViewModel =
        //        new ViewModelProvider(this).get(StatisticsViewModel.class);
        //final TextView textView = binding.textStatistics;
        //statisticsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Custom ValueFormatter to format XAxis values as date
    // Custom ValueFormatter to format XAxis values as date
    private static class MyXAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int intValue = (int) value;

            // Отримання дати початкового значення
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
            String formattedDate = sdf.format(calendar.getTime());

            // Додавання до дати значення, отриманого з бази даних
            calendar.add(Calendar.DAY_OF_MONTH, intValue);
            formattedDate = sdf.format(calendar.getTime());

            return formattedDate;
        }
    }



    private List<BarEntry> getIncomeDataFromDatabase() {
        List<BarEntry> incomeEntries = new ArrayList<>();

        // Отримання бази даних в режимі читання
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // SQL-запит для отримання суми доходу за кожен день
        String query = "SELECT " + dbHelper.COLUMN_DATE + ", SUM(" + dbHelper.COLUMN_AMOUNT + ") AS total_income" +
                " FROM " + dbHelper.TABLE_NAME +
                " INNER JOIN " + dbHelper.CATEGORY_TABLE_NAME +
                " ON " + dbHelper.TABLE_NAME + "." + dbHelper.COLUMN_TO + " = " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_ID +
                " WHERE " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_TYPE + " = 'Дохід'" +
                " GROUP BY " + dbHelper.COLUMN_DATE;

        // Виконання запиту та отримання результатів
        Cursor cursor = db.rawQuery(query, null);

        float barDistance = 0.1f;
        // Переміщення курсора на початок
        if (cursor.moveToFirst()) {
            int counter = 0; // Лічильник для значень від 0 до 7
            do {
                @SuppressLint("Range") float totalIncome = cursor.getFloat(cursor.getColumnIndex("total_income"));

                // Обмеження значення лічильника від 0 до 7
                if (counter > 7) {
                    counter = 0; // Починаємо знову з 0, якщо лічильник перевищує 7
                }

                incomeEntries.add(new BarEntry(counter - barDistance * 2, totalIncome));

                counter++;
            } while (cursor.moveToNext());
        }



        // Закриття курсора та бази даних
        cursor.close();
        db.close();

        return incomeEntries;
    }


    private List<BarEntry> getExpensesDataFromDatabase() {
        List<BarEntry> incomeEntries = new ArrayList<>();

        // Отримання бази даних в режимі читання
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // SQL-запит для отримання суми доходу за кожен день
        String query = "SELECT " + dbHelper.COLUMN_DATE + ", SUM(" + dbHelper.COLUMN_AMOUNT + ") AS total_expenses" +
                " FROM " + dbHelper.TABLE_NAME +
                " INNER JOIN " + dbHelper.CATEGORY_TABLE_NAME +
                " ON " + dbHelper.TABLE_NAME + "." + dbHelper.COLUMN_TO + " = " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_ID +
                " WHERE " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_TYPE + " = 'Витрати'" +
                " GROUP BY " + dbHelper.COLUMN_DATE;

        // Виконання запиту та отримання результатів
        Cursor cursor = db.rawQuery(query, null);

        float barDistance = 0.1f;
        // Переміщення курсора на початок
        if (cursor.moveToFirst()) {
            int counter = 0; // Лічильник для значень від 0 до 7
            do {
                @SuppressLint("Range") float totalExpenses = cursor.getFloat(cursor.getColumnIndex("total_expenses"));

                // Обмеження значення лічильника від 0 до 7
                if (counter > 7) {
                    counter = 0; // Починаємо знову з 0, якщо лічильник перевищує 7
                }

                incomeEntries.add(new BarEntry(counter + barDistance * 2, totalExpenses));

                counter++;
            } while (cursor.moveToNext());
        }



        // Закриття курсора та бази даних
        cursor.close();
        db.close();

        return incomeEntries;
    }

}