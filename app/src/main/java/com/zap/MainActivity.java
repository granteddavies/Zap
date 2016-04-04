package com.zap;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.login.LoginManager;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
