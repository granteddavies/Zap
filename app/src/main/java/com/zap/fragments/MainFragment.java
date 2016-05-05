package com.zap.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.zap.R;
import com.zap.models.Session;
import com.zap.models.User;

public class MainFragment extends Fragment {

    private TextView availabilityText;
    private Switch availabilitySwitch;

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
        availabilitySwitch = (Switch) rootView.findViewById(R.id.availabilitySwitch);
        availabilityText = (TextView) rootView.findViewById(R.id.availabilityText);

        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleAvailable(isChecked);
            }
        });

        updateUI(Session.user.getAvailable());

        return rootView;
    }

    /**
     * Toggle the user's availability
     */
    private void toggleAvailable(boolean isAvailable) {
        availabilitySwitch.setClickable(false);
        Session.user.setAvailable(isAvailable);
        updateUser();
    }

    private void updateUI(boolean isAvailable) {
        availabilitySwitch.setClickable(true);
        availabilitySwitch.setChecked(isAvailable);
        if (isAvailable) {
            availabilityText.setText(getString(R.string.available));
        }
        else {
            availabilityText.setText(getString(R.string.unavailable));
        }
    }

    private void updateUser() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Session.mClient.getTable(User.class).update(Session.user).get();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            updateUI(Session.user.getAvailable());
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
