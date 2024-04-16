package com.example.thinktwice.ui.home;
import android.util.Log;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thinktwice.R;

import android.view.View;
import android.widget.*;

import com.example.thinktwice.ui.DatabaseHelper;

public class AddTransactions extends AppCompatActivity {

    String TAG = "AddTransactions activity";
    Button add_button;
    EditText tocat_input, fromcat_input, details_input, sum_input;

    TextView dateTextView;
    CheckBox scheduleCheckBox;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);

        add_button = findViewById(R.id.add_button);
        add_button.setText("ДОАДААААТИ");
        Log.d(TAG, "onCreate() method called");
        System.out.println("onCreate() method called");
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_button.setText("Adding...");
                System.out.println("add_button.setOnClickListener(new View.OnClickListener()  method called");
                Log.d(TAG, "add_button.setOnClickListener(new View.OnClickListener()  method called");
                DatabaseHelper db = new DatabaseHelper(AddTransactions.this);
                db.addTransaction(tocat_input.getText().toString(), details_input.getText().toString(), datePicker.toString(),
                        Integer.parseInt(sum_input.getText().toString()), scheduleCheckBox.isChecked() ? 1:0,
                        Integer.parseInt(sum_input.getText().toString()), Integer.parseInt(sum_input.getText().toString()));
            }
        });

        tocat_input = findViewById(R.id.tocat_input);
        fromcat_input = findViewById(R.id.fromcat_input);
        details_input = findViewById(R.id.details_input);
        sum_input = findViewById(R.id.sum_input);
        dateTextView = findViewById(R.id.dateTextView);
        scheduleCheckBox = findViewById(R.id.scheduleCheckBox);
        datePicker = findViewById(R.id.datePicker);

    }
}

