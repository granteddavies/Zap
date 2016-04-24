package com.zap;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

public class MainFragment extends Fragment {

    private ToggleButton toggleAvailable;
    private EditText editActivity;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialization logic for widgets
        toggleAvailable = (ToggleButton) rootView.findViewById(R.id.toggleAvailable);
        toggleAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleAvailable(isChecked);
            }
        });
        editActivity = (EditText) rootView.findViewById(R.id.editActivity);
        editActivity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        updateUI(Profile.user.getAvailable());

        return rootView;
    }

    /**
     * Toggle the user's availability
     */
    private void toggleAvailable(boolean isAvailable) {
        Profile.user.setAvailable(isAvailable);
        updateUser();
    }

    private void updateUI(boolean isAvailable) {
        toggleAvailable.setChecked(isAvailable);
        if (isAvailable) {
            toggleAvailable.setTextColor(Color.GREEN);
        }
        else {
            toggleAvailable.setTextColor(Color.RED);
        }
    }

    private void updateUser() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Profile.mClient.getTable(User.class).update(Profile.user).get();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateUI(Profile.user.getAvailable());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
