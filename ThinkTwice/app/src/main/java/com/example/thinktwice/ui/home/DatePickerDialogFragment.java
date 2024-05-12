package com.example.thinktwice.ui.home;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.thinktwice.R;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment implements DatePicker.OnDateChangedListener {
    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }

    private OnDateSetListener mListener;

    public void setOnDateSetListener(OnDateSetListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker_dialog, container, false);
        DatePicker datePicker = view.findViewById(R.id.datePicker);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, this); // Set the OnDateChangedListener

        return view;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Notify the listener when the user selects a date
        if (mListener != null) {
            mListener.onDateSet(view, year, monthOfYear, dayOfMonth);
        }
    }
}

