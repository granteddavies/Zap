package com.zap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.EventViewHolder> {
    private ArrayList<Invite> inviteList;
    private Context context;

    public InviteAdapter(ArrayList<Invite> inviteList, Context context) {
        this.inviteList = inviteList;
        this.context = context;
    }

    @Override
    public InviteAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_member, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InviteAdapter.EventViewHolder holder, int position) {
        holder.memberName.setText(inviteList.get(position).getRecipientname());
        holder.memberStatus.setText(inviteList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView memberName;
        protected TextView memberStatus;

        public EventViewHolder(View itemView) {
            super(itemView);
            memberName = (TextView) itemView.findViewById(R.id.memberName);
            memberStatus = (TextView) itemView.findViewById(R.id.memberStatus);
        }
    }
}