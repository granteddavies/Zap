package com.zap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {

    private ArrayList<User> friends = new ArrayList<>();
    private FriendsAdapter adapter;

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

        Context context = view.getContext();
        adapter = new FriendsAdapter(friends, context);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }

        loadFriends();

        return view;
    }

    private void loadFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray fbFriends = response.getJSONObject().getJSONArray("data");

                            friends.clear();
                            if (fbFriends != null) {
                                for (int i = 0; i < fbFriends.length(); i++) {
                                    JSONObject fbFriend = fbFriends.getJSONObject(i);
                                    User friend = new User(fbFriend.getString("name"),
                                            fbFriend.getString("id"));
                                    friends.add(friend);
                                }
                            }

                            // TODO: remove this test line
                            friends.add(Profile.user);
                            //

                            initializeFriends();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateAdapter();
                    }
                }
        ).executeAsync();
    }

    private void initializeFriends() {
        for (int i = 0; i < friends.size(); i++) {
            final int index = i;
            final User friend = friends.get(index);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final MobileServiceList<User> result =
                                Profile.mClient.getTable(User.class).where()
                                        .field("id").eq(friend.getId())
                                        .execute().get();

                        if (result.size() == 1) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    friends.set(index, result.get(0));
                                    updateAdapter();
                                }
                            });
                        }
                        else if (result.size() == 0) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    friend.setAvailable(false);
                                    friend.setActivity(null);
                                    updateAdapter();
                                }
                            });
                        }
                        else {
                            throw new RuntimeException("Unexpected number of matches for profile user");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();
        }
    }

    private void updateAdapter() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
