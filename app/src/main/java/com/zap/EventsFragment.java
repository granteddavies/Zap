package com.zap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Grant on 4/24/2016.
 */
public class EventsFragment extends Fragment {
    private ArrayList<Invite> invites = new ArrayList<>();
    private ArrayList<EventData> events = new ArrayList<>();
    private EventAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView emptyText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventsFragment() {
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        Context context = view.getContext();
        adapter = new EventAdapter(events, context);

        recyclerView = (RecyclerView) view.findViewById(R.id.eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        emptyText = (TextView) view.findViewById(R.id.emptyEvents);

        events.clear();
        adapter.notifyDataSetChanged();

        progressBar = (ProgressBar) view.findViewById(R.id.eventProgress);

        loadEvents();

        return view;
    }

    private void loadEvents() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final MobileServiceList<Invite> result =
                            Profile.mClient.getTable(Invite.class).where()
                                    .field("recipientid").eq(Profile.user.getId())
                                    .execute().get();

                    invites.clear();
                    for (int i = 0; i < result.size(); i++) {
                        invites.add(result.get(i));
                    }

                    initializeEvents();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void initializeEvents() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    events.clear();
                    for (final Invite invite : invites) {
                        final MobileServiceList<Event> result =
                                Profile.mClient.getTable(Event.class).where()
                                        .field("id").eq(invite.getEventid())
                                        .execute().get();

                        if (result.size() == 1) {
                            EventData eventData = new EventData(result.get(0));
                            eventData.addInvite(invite);
                            events.add(eventData);
                        }
                        else {
                            throw new RuntimeException("Unexpected number of matches for event");
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);

                            if (events.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyText.setVisibility(View.VISIBLE);
                            }

                            Collections.sort(events, new EventComparator());
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private class EventComparator implements Comparator<EventData> {
        @Override
        public int compare(EventData ed1, EventData ed2) {
            if (ed1.getEvent().getHostid().equals(Profile.user.getId()) && !ed2.getEvent().getHostid().equals(Profile.user.getId())) {
                return -1;
            }
            if (!ed1.getEvent().getHostid().equals(Profile.user.getId()) && ed2.getEvent().getHostid().equals(Profile.user.getId())) {
                return 1;
            }

            String s1 = ed1.getInvites().get(0).getStatus();
            String s2 = ed2.getInvites().get(0).getStatus();

            int p1 = 0, p2 = 0;

            switch (s1) {
                case EventData.STATUS_PENDING:
                    p1 = 0;
                    break;
                case EventData.STATUS_YES:
                    p1 = 1;
                    break;
                case EventData.STATUS_MAYBE:
                    p1 = 2;
                    break;
                case EventData.STATUS_CANT:
                    p1 = 3;
                    break;
            }
            switch (s2) {
                case EventData.STATUS_PENDING:
                    p2 = 0;
                    break;
                case EventData.STATUS_YES:
                    p2 = 1;
                    break;
                case EventData.STATUS_MAYBE:
                    p2 = 2;
                    break;
                case EventData.STATUS_CANT:
                    p2 = 3;
                    break;
            }

            if (p1 < p2) {
                return -1;
            }
            if (p1 > p2) {
                return 1;
            }

            return ed1.getEvent().getTitle().compareTo(ed2.getEvent().getTitle());
        }
    }
}
