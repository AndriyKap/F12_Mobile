package com.example.thinktwice.ui.statistics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.example.thinktwice.ui.dashboard.DashboardViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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

        List<BarEntry> incomeEntries = getIncomeDataFromDatabase();
        barChart.setFitBars(true);
        barChart.getAxisLeft().setAxisMinimum(0f);

        BarDataSet barDataSet = new BarDataSet(incomeEntries, "Сума");
        barDataSet.setForm(Legend.LegendForm.CIRCLE);
        barDataSet.setColor(Color.rgb(134, 180, 239));
        barDataSet.setValueTextColor(Color.rgb(134, 180, 239));;
        Legend legend = barChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        BarData barDataSum = new BarData(barDataSet);
        barDataSum.setBarWidth(0.3f);
        barChart.setData(barDataSum);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);

        List<BarEntry> expensesEntries = getExpensesDataFromDatabase();

        BarDataSet expensesDataSet = new BarDataSet(expensesEntries, "Витрати");
        expensesDataSet.setForm(Legend.LegendForm.CIRCLE);
        expensesDataSet.setColor(Color.rgb(255, 99, 81));
        expensesDataSet.setValueTextColor(Color.rgb(255, 99, 81));
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


        List<CategoryData> categoryDataList = getCategoryDataFromDatabase();

        // Створення pie chart
        PieChart pieChart = binding.pieChart;
        setupPieChart(pieChart);

        // Додавання даних до pie chart
        addDataToPieChart(pieChart, categoryDataList);



        // Inside onCreateView() method
// Add these lines after setting up the first bar chart (barChart)

// Set up the second bar chart (barChart2)
        BarChart barChart2 = binding.barChart2;
        setupBarChart(barChart2);

// Retrieve category data from the database
        List<CategoryData> categoryDataList2 = getCategoryDataFromDatabase();

// Create bar entries for each category
        List<BarEntry> categoryEntries = getCategoryExpensesDataFromDatabase(categoryDataList2);

// Create a dataset for category expenses
        BarDataSet categoryDataSet = new BarDataSet(categoryEntries, "Category Expenses");
        categoryDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        //////
        XAxis xAxiss = binding.barChart2.getXAxis();
        xAxiss.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxiss.setDrawAxisLine(true);
        xAxiss.setDrawGridLines(false);
        xAxiss.setTextSize(10f);
        xAxiss.setTypeface(Typeface.DEFAULT_BOLD);

        xAxiss.setGranularity(1f);

        // Add labels for the bars
        List<String> barLabels = new ArrayList<>();
        //List<String> okay = getCategoryNamesFromDatabase();
        barLabels.add("");
        for (int i = 0; i < 4; i++) {
            barLabels.add("Category");
        }

        // Convert the list to an array
        final String[] barLabelsA = barLabels.toArray(new String[0]);



        // Set the custom labels for the X-axis
        xAxiss.setValueFormatter(new IndexAxisValueFormatter(barLabelsA));


        /////

// Configure the legend
        Legend categoryLegend = barChart2.getLegend();
        categoryLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

// Create BarData and set it to the bar chart
        BarData categoryBarData = new BarData(categoryDataSet);
        barChart2.setData(categoryBarData);
        barChart2.getDescription().setEnabled(false);
        barChart2.invalidate();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Custom ValueFormatter to format XAxis values as date
    private static class MyXAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int intValue = (int) value;

            // Отримання поточної дати
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, (intValue - 6)); // Віднімаємо 6 днів, щоб поточний день був 0
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
            return sdf.format(calendar.getTime());
        }
    }

    private List<BarEntry> getIncomeDataFromDatabase() {
        List<BarEntry> incomeEntries = new ArrayList<>();

        // Отримати початкову та кінцеву дати для останнього тижня
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6); // Початкова дата - 6 днів назад (бо враховуємо і сьогодні)
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        Calendar endDateCalendar = Calendar.getInstance();
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDateCalendar.getTime());

        // Складаємо запит SQL для отримання доходів за останній тиждень
        String query = "SELECT " + dbHelper.COLUMN_DATE + ", SUM(" + dbHelper.COLUMN_AMOUNT + ") AS total_income" +
                " FROM " + dbHelper.TABLE_NAME +
                " INNER JOIN " + dbHelper.CATEGORY_TABLE_NAME +
                " ON " + dbHelper.TABLE_NAME + "." + dbHelper.COLUMN_TO + " = " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_ID +
                " WHERE " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_TYPE + " = 'Баланс'" +
                " AND " + dbHelper.COLUMN_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                " GROUP BY " + dbHelper.COLUMN_DATE;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SQL_QUERY", query);

        float barDistance = 0.15f;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                float income = cursor.getFloat(cursor.getColumnIndex("total_income"));
                String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_DATE));
                // Отримати день з дати
                int day = getDayFromDate(dateString); // Потрібно реалізувати метод getDayFromDate()

                Calendar calendar1 = Calendar.getInstance(); // Отримання поточної дати
                int currentDay = calendar1.get(Calendar.DAY_OF_MONTH); // Отримання числа поточного дня
                calendar1.add(Calendar.DAY_OF_MONTH, -6);
                int sevenDaysAgo = calendar1.get(Calendar.DAY_OF_MONTH);

                List<Integer> sequence = new ArrayList<>();
                for (int i = sevenDaysAgo; i <= currentDay; i++) {
                    sequence.add(i);
                }

                Map<Integer, Integer> indexedSequence = new HashMap<>();
                for (int i = sevenDaysAgo; i <= currentDay; i++) {
                    indexedSequence.put(i, i - sevenDaysAgo);
                }

                int indexDay = indexedSequence.get(day);

                incomeEntries.add(new BarEntry(indexDay - barDistance, income));
            } while (cursor.moveToNext());
        }

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DAY_OF_MONTH, -6);

        int dayCount = 0;
        // Додати нульові значення для днів, коли немає записів
        for (int i = dayCount; i < 7; i++) {
            incomeEntries.add(new BarEntry(i - barDistance, 0));
        }

        cursor.close();
        db.close();

        return incomeEntries;
    }

    private List<String> getCategoryNamesFromDatabase() {
        List<String> categoryNames = new ArrayList<>();

        List<BarEntry> incomeEntries = new ArrayList<>();

        // Отримати початкову та кінцеву дати для останнього тижня
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6); // Початкова дата - 6 днів назад (бо враховуємо і сьогодні)
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        Calendar endDateCalendar = Calendar.getInstance();
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDateCalendar.getTime());

        // Складаємо запит SQL для отримання доходів за останній тиждень
        String query = "SELECT " + dbHelper.COLUMN_DATE + ", " + dbHelper.CATEGORY_COLUMN_TITLE + ", SUM(" + dbHelper.COLUMN_AMOUNT + ") AS total_income" +
                " FROM " + dbHelper.TABLE_NAME +
                " INNER JOIN " + dbHelper.CATEGORY_TABLE_NAME +
                " ON " + dbHelper.TABLE_NAME + "." + dbHelper.COLUMN_TO + " = " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_ID +
                " WHERE " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_TYPE + " = 'Дохід'" +
                " AND " + dbHelper.COLUMN_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                " GROUP BY " + dbHelper.COLUMN_DATE;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SQL_QUERY", query);

        float barDistance = 0.15f;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String namename = cursor.getString(cursor.getColumnIndex(dbHelper.CATEGORY_COLUMN_TITLE));
                categoryNames.add(namename);
            } while (cursor.moveToNext());
        }

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DAY_OF_MONTH, -6);

        cursor.close();
        db.close();

        return categoryNames;
    }

    // Method to set up the bar chart
    private void setupBarChart(BarChart barChart) {
        // Customize bar chart as needed
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
    }

    // Method to retrieve expenses data for each category
    private List<BarEntry> getCategoryExpensesDataFromDatabase(List<CategoryData> categoryDataList) {
        List<BarEntry> categoryEntries = new ArrayList<>();

        for (int i = 0; i < categoryDataList.size(); i++) {
            // Get category ID
            int categoryId = categoryDataList.get(i).getId();
            String categoryTitle = categoryDataList.get(i).getTitle();

            // Get expenses data for the current category
            double expenses = getExpensesForCategory(categoryId); // Implement this method to fetch expenses for each category

            // Add expenses as a bar entry
            BarEntry barEntry = new BarEntry(i, (float) expenses);
            barEntry.setData(categoryTitle);
            categoryEntries.add(barEntry);
        }

        return categoryEntries;
    }


    private List<BarEntry> getExpensesDataFromDatabase() {
        List<BarEntry> expensesEntries = new ArrayList<>();

        // Отримати початкову та кінцеву дати для останнього тижня
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6); // Початкова дата - 6 днів назад (бо враховуємо і сьогодні)
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        Calendar endDateCalendar = Calendar.getInstance();
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDateCalendar.getTime());

        // Складаємо запит SQL для отримання доходів за останній тиждень
        String query = "SELECT " + dbHelper.COLUMN_DATE + ", SUM(" + dbHelper.COLUMN_AMOUNT + ") AS total_expenses" +
                " FROM " + dbHelper.TABLE_NAME +
                " INNER JOIN " + dbHelper.CATEGORY_TABLE_NAME +
                " ON " + dbHelper.TABLE_NAME + "." + dbHelper.COLUMN_TO + " = " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_ID +
                " WHERE " + dbHelper.CATEGORY_TABLE_NAME + "." + dbHelper.CATEGORY_COLUMN_TYPE + " = 'Витрати'" +
                " AND " + dbHelper.COLUMN_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'" +
                " GROUP BY " + dbHelper.COLUMN_DATE;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        float barDistance = 0.15f;
        if (cursor.moveToFirst()) {
            do {
                float income = cursor.getFloat(cursor.getColumnIndex("total_expenses"));
                String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_DATE));
                // Отримати день з дати
                int day = getDayFromDate(dateString); // Потрібно реалізувати метод getDayFromDate()
                Calendar calendar2 = Calendar.getInstance(); // Отримання поточної дати
                int currentDay = calendar2.get(Calendar.DAY_OF_MONTH); // Отримання числа поточного дня
                calendar2.add(Calendar.DAY_OF_MONTH, -6);
                int sevenDaysAgo = calendar2.get(Calendar.DAY_OF_MONTH);

                List<Integer> sequence = new ArrayList<>();
                for (int i = sevenDaysAgo; i <= currentDay; i++) {
                    sequence.add(i);
                }

                Map<Integer, Integer> indexedSequence = new HashMap<>();
                for (int i = sevenDaysAgo; i <= currentDay; i++) {
                    indexedSequence.put(i, i - sevenDaysAgo);
                }

                int indexDay = indexedSequence.get(day);

                expensesEntries.add(new BarEntry(indexDay + barDistance, income));
            } while (cursor.moveToNext());
        }

        int dayCount = 0;
        // Додати нульові значення для днів, коли немає записів
        for (int i = dayCount; i < 7; i++) {
            expensesEntries.add(new BarEntry(i + barDistance, 0));
        }

        cursor.close();
        db.close();

        return expensesEntries;
    }

    private double getExpensesForCategory(int categoryId) {
        double totalExpenses = 0;

        // Get the start and end dates for the last week
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Construct the SQL query to get expenses for the category for the last week
        String query = "SELECT SUM(" + dbHelper.COLUMN_AMOUNT + ") AS total_expenses" +
                " FROM " + dbHelper.TABLE_NAME +
                " WHERE " + dbHelper.COLUMN_TO + " = " + categoryId +
                " AND " + dbHelper.COLUMN_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndex("total_expenses"));
            cursor.close();
        }

        db.close();

        return totalExpenses;
    }


    private int getDayFromDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Повернути значення за замовчуванням у разі помилки
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


    private void setupPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
    }

    private void addDataToPieChart(PieChart pieChart, List<CategoryData> categoryDataList) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        double totalPercentage = 0.0;

        for (CategoryData categoryData : categoryDataList) {
            double percentageAmount = categoryData.getPercentageAmount();
            totalPercentage += percentageAmount;
            entries.add(new PieEntry((float) (percentageAmount * 100), categoryData.getTitle()));
        }

        // Перевірка, чи сума відсотків менше 1
        if (totalPercentage < 1.0) {
            double remainingPercentage = 1.0 - totalPercentage;
            entries.add(new PieEntry((float) (remainingPercentage * 100), "Невикористані витрати"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category Data");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.invalidate();
    }



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
}