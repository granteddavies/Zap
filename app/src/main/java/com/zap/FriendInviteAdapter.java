package com.zap;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendInviteAdapter extends RecyclerView.Adapter<FriendInviteAdapter.FriendViewHolder> {
    private ArrayList<User> friendList;

    public FriendInviteAdapter(ArrayList<User> friendList, Context context) {
        this.friendList = friendList;
    }

    @Override
    public FriendInviteAdapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_invite, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendInviteAdapter.FriendViewHolder holder, int position) {
        holder.checkBox.setText(friendList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        protected CheckBox checkBox;

        public FriendViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.friendCheck);
        }
    }
}
