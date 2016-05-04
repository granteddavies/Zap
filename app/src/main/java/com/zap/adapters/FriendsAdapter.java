package com.zap.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zap.R;
import com.zap.models.User;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private ArrayList<User> friendList;

    public FriendsAdapter(ArrayList<User> friendList, Context context) {
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
        holder.name.setText(friendList.get(position).getName());

        boolean available = friendList.get(position).getAvailable();
        if (available) {
            String activity = friendList.get(position).getActivity();
            if (activity != null) {
                holder.availability.setText(activity);
            }
            else {
                holder.availability.setText(R.string.available);
            }
            holder.availability.setTextColor(Color.BLACK);
        }
        else {
            holder.availability.setText(R.string.unavailable);
            holder.availability.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView availability;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            availability = (TextView) itemView.findViewById(R.id.availability);
        }
    }
}
