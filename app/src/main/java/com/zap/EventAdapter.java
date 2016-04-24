package com.zap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ArrayList<Event> eventList;

    public EventAdapter(ArrayList<Event> eventList, Context context) {
        this.eventList = eventList;
    }

    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventAdapter.EventViewHolder holder, int position) {
        holder.host.setText(eventList.get(position).getHostname());
        holder.title.setText(eventList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView host;
        protected TextView title;

        public EventViewHolder(View itemView) {
            super(itemView);
            host = (TextView) itemView.findViewById(R.id.host);
            title = (TextView) itemView.findViewById(R.id.titled);
        }
    }
}