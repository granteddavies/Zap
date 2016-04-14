package com.zap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private ArrayList<String> friendList;

    public FriendsAdapter(ArrayList<String> friendList, Context context) {
        this.friendList = friendList;
    }

    @Override
    public FriendsAdapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_friend, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.FriendViewHolder holder, int position) {
        holder.text.setText(friendList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        protected TextView text;

        public FriendViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.id);
        }
    }
}
