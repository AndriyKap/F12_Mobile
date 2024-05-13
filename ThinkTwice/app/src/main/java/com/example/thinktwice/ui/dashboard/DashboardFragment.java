package com.example.thinktwice.ui.dashboard;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thinktwice.R;
import com.example.thinktwice.databinding.FragmentDashboardBinding;
import com.example.thinktwice.ui.DatabaseHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;


public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DatabaseHelper dbHelper;

    private LineChart lineChart;
    HashMap<Date, Float> expensesDict = new HashMap<>();
    HashMap<Date, Float> incomeDict = new HashMap<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHelper = new DatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getAllTransactions();

        if (cursor != null) {

            // Формат дати "dd-MM-yy"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Обчислення балансу, витрат, доходу та заощаджень
            float balance = 0;
            float income = 0;
            float expenses = 0;
            float savings = 0;
            while (((Cursor) cursor).moveToNext()) {
                float amount = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                String toCategory = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TO));
                String fromCategory = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FROM));
                String dateString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                // Отримання типу категорії для кожної транзакції
                String toCategoryType = dbHelper.getCategoryType(toCategory);
                String fromCategoryType = dbHelper.getCategoryType(fromCategory);

                try {
                    // Перетворення рядка дати у об'єкт Date
                    Date date = dateFormat.parse(dateString);
                    // Отримання поточної дати та часу
                    // Отримання поточної дати та часу
                    LocalDateTime now = LocalDateTime.now();

                    // Отримання початку останнього тижня
                    LocalDateTime startOfLastWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                    // Отримання кінця останнього тижня
                    LocalDateTime endOfLastWeek = startOfLastWeek.plusDays(6); // Останній день тижня - неділя

                    // Перевірка, чи дата належить відрізку останнього тижня
                    if (date.after(Date.from(startOfLastWeek.atZone(ZoneId.systemDefault()).toInstant()))
                            && date.before(Date.from(endOfLastWeek.atZone(ZoneId.systemDefault()).toInstant()))) {
                        // Дата належить відрізку останнього тижня

                        // Якщо це витрати, додайте до відповідного словника
                        if (toCategoryType != null && toCategoryType.equals("Витрати")) {
                            if (expensesDict.containsKey(date)) {
                                // Якщо вже є така дата, додайте суму до існуючої суми витрат
                                expensesDict.put(date, expensesDict.get(date) + amount);
                            } else {
                                // Якщо це перша витрата за цей день, створіть новий запис в словнику
                                expensesDict.put(date, amount);
                            }
                        }

                        // Якщо це дохід, додайте до відповідного словника
                        if (fromCategoryType != null && fromCategoryType.equals("Дохід")) {
                            if (incomeDict.containsKey(date)) {
                                // Якщо вже є така дата, додайте суму до існуючої суми доходів
                                incomeDict.put(date, incomeDict.get(date) + amount);
                            } else {
                                // Якщо це перший дохід за цей день, створіть новий запис в словнику
                                incomeDict.put(date, amount);
                            }
                        }


                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (fromCategoryType != null && fromCategoryType.equals("Дохід")) {
                    income += amount;
                }

                if (toCategoryType != null && toCategoryType.equals("Витрати")) {
                    expenses += amount;
                }

                if (toCategoryType != null && toCategoryType.equals("Скарбничка")) {
                    savings += amount;
                }

                if (fromCategoryType!= null && fromCategoryType.equals("Скарбничка")){
                    savings -= amount;
                }

            }
            balance = income-expenses;
            cursor.close();

            // Оновлення текстових полів інформації на дашборді
            TextView balanceTextView = root.findViewById(R.id.balance);
            TextView incomeTextView = root.findViewById(R.id.income);
            TextView expensesTextView = root.findViewById(R.id.expenses);
            TextView savingsTextView = root.findViewById(R.id.savings);

            balanceTextView.setText(String.valueOf(balance));
            incomeTextView.setText(String.valueOf(income));
            expensesTextView.setText(String.valueOf(expenses));
            savingsTextView.setText(String.valueOf(savings));
        }
        displayTransactions();
        lineChart = root.findViewById(R.id.lineChart);
        setupLineChart();

        setDataForChart();

        return root;
    }


    private void displayTransactions() {
        // Отримати всі транзакції
        Cursor cursor = dbHelper.getAllTransactions();
        if (cursor != null) {
            View root = binding.getRoot();
            TableLayout transactionsTable = root.findViewById(R.id.transactionsTable);

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
                addTextViewToTableRow(newRow, details);
                addTextViewToTableRow(newRow, date);
                addTextViewToTableRow(newRow, String.valueOf(amount));
                addTextViewToTableRow(newRow, fromCategoryName);
                addTextViewToTableRow(newRow, toCategoryName);

                // Додати TableRow до TableLayout
                transactionsTable.addView(newRow);
            }
            cursor.close();
        }
    }

    // Метод для додавання TextView до TableRow
    private void addTextViewToTableRow(TableRow row, String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setMaxLines(1);
        textView.setTextSize(10);
        textView.setTextColor(Color.GRAY);
        textView.setPadding(3, 0, 3, 0);
        row.addView(textView);
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setNoDataText("No data available");

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setTextSize(12f);
        xAxis.setLabelCount(6, true); // Встановлення максимальної кількості відображень

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12f);



        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Встановлення більш плавної кривої
        lineChart.animateX(1500);
        lineChart.animateY(1500);

        // Отримання DataSet з графіка
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet dataSet = (LineDataSet) data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = createLineDataSet();
                data.addDataSet(dataSet);
            } else {
                dataSet.setDrawCircles(false); // Відключення відображення точок
            }
        }
    }

    // Метод для створення LineDataSet
    private LineDataSet createLineDataSet() {
        LineDataSet dataSet = new LineDataSet(null, null);
        dataSet.setDrawValues(false); // Відключення відображення значень
        dataSet.setDrawCircles(false); // Відключення відображення точок
        dataSet.setColor(Color.RED); // Встановлення кольору лінії
        dataSet.setLineWidth(2f); // Встановлення товщини лінії
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Встановлення плавної кривої
        return dataSet;
    }



    private void setDataForChart() {
        ArrayList<Entry> expensesEntries = new ArrayList<>();
        ArrayList<Entry> incomeEntries = new ArrayList<>();

        // Отримання діапазону дат
        ArrayList<Date> dates = new ArrayList<>(expensesDict.keySet());
        dates.addAll(incomeDict.keySet());
        Collections.sort(dates);

        // Додавання даних про витрати та доходи для кожної дати
        for (int i = 0; i < dates.size(); i++) {
            Date date = dates.get(i);

            // Додавання нульових значень для днів без даних
            if (!expensesDict.containsKey(date)) {
                expensesDict.put(date, 0f);
            }
            if (!incomeDict.containsKey(date)) {
                incomeDict.put(date, 0f);
            }

            // Додавання значень для графіка
            expensesEntries.add(new Entry(i, expensesDict.get(date)));
            incomeEntries.add(new Entry(i, incomeDict.get(date)));
        }

        // Створення LineDataSet для витрат
        LineDataSet expensesDataSet = new LineDataSet(expensesEntries, "Expenses");
        expensesDataSet.setColor(Color.RED);
        expensesDataSet.setCircleColor(Color.RED);
        expensesDataSet.setLineWidth(2f);
        expensesDataSet.setCircleRadius(3f);
        expensesDataSet.setValueTextSize(12f);
        expensesDataSet.setDrawValues(false);

        // Створення LineDataSet для доходів
        LineDataSet incomeDataSet = new LineDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.BLUE);
        incomeDataSet.setCircleColor(Color.BLUE);
        incomeDataSet.setLineWidth(2f);
        incomeDataSet.setCircleRadius(3f);
        incomeDataSet.setValueTextSize(12f);
        incomeDataSet.setDrawValues(false);

        // Створення LineData та встановлення для графіка
        LineData lineData = new LineData(expensesDataSet, incomeDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // Встановлення кастомних міток для осі X
        final String[] xAxisLabels = new String[dates.size()];
        for (int i = 0; i < dates.size(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
            xAxisLabels[i] = dateFormat.format(dates.get(i));
        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        // Створення легенди для графіка
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10f);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(5f);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setCustom(new ArrayList<>(Arrays.asList(
                new LegendEntry("Expenses", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.RED),
                new LegendEntry("Income", Legend.LegendForm.CIRCLE, 10f, 2f, null, Color.BLUE)
        )));
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}