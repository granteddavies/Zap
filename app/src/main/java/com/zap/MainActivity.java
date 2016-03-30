package com.zap;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private TextView textAvailable;
    private ToggleButton toggleAvailable;
    private EditText editActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization logic for widgets
        textAvailable = (TextView) findViewById(R.id.textAvailable);
        toggleAvailable = (ToggleButton) findViewById(R.id.toggleAvailable);
        toggleAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleAvailable(isChecked);
            }
        });
        editActivity = (EditText) findViewById(R.id.editActivity);
        editActivity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
    }

    /**
     * Toggle the user's availability
     */
    private void toggleAvailable(boolean isAvailable) {
        if (isAvailable) {
            textAvailable.setText(getResources().getString(R.string.available));
            textAvailable.setTextColor(Color.GREEN);
        }
        else {
            textAvailable.setText(getResources().getString(R.string.unavailable));
            textAvailable.setTextColor(Color.RED);
        }
    }
}
