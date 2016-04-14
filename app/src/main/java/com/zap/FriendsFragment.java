package com.zap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FriendsFragment() {
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        ArrayList<String> list = getFriends();

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new FriendsAdapter(list, context));
        }
        return view;
    }

    private ArrayList<String> getFriends() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Angus");
        list.add("Nick");
        list.add("Logan");
        list.add("Garrett");
        list.add("Bryan");
        list.add("Malcolm");
        list.add("Robert");
        list.add("Alex");
        list.add("Joe");
        list.add("Kenzie");
        list.add("Jared");
        list.add("Eric");
        list.add("Tori");
        return list;
    }
}
