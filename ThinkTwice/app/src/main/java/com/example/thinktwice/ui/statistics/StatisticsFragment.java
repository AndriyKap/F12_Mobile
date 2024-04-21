package com.example.thinktwice.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        String firstLetterCapitalizedDay = currentDate.substring(0, 1).toUpperCase() + currentDate.substring(1);

        binding.textViewDate.setText(firstLetterCapitalizedDay);

        BarChart barChart = binding.barChart;
        float barDistance = 0.1f;

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0 - barDistance*2, 10));
        entries.add(new BarEntry(1 - barDistance*2, 20));
        entries.add(new BarEntry(2 - barDistance*2, 15));
        entries.add(new BarEntry(3 - barDistance*2, 25));
        entries.add(new BarEntry(4 - barDistance*2, 30));
        entries.add(new BarEntry(5 - barDistance*2, 18));
        entries.add(new BarEntry(6 - barDistance*2, 22));

        BarDataSet barDataSet = new BarDataSet(entries, "Сума");
        barDataSet.setForm(Legend.LegendForm.CIRCLE);
        Legend legend = barChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        BarData barDataSum = new BarData(barDataSet);
        barDataSum.setBarWidth(0.3f);
        barChart.setData(barDataSum);

        List<BarEntry> expensesEntries = new ArrayList<>();
        expensesEntries.add(new BarEntry(0 + barDistance*2, 5));
        expensesEntries.add(new BarEntry(1 + barDistance*2, 8));
        expensesEntries.add(new BarEntry(2 + barDistance*2, 7));
        expensesEntries.add(new BarEntry(3 + barDistance*2, 10));
        expensesEntries.add(new BarEntry(4 + barDistance*2, 6));
        expensesEntries.add(new BarEntry(5 + barDistance*2, 12));
        expensesEntries.add(new BarEntry(6 + barDistance*2, 9));

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
    private static class MyXAxisValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int intValue = (int) value;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
            String formattedDate = sdf.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_MONTH, intValue);
            formattedDate = sdf.format(calendar.getTime());

            return formattedDate;
        }
    }
}