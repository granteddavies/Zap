package com.zap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ArrayList<EventData> eventList;
    private Context context;

    public EventAdapter(ArrayList<EventData> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_event, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventAdapter.EventViewHolder holder, int position) {
        holder.title.setText(eventList.get(position).getEvent().getTitle());

        if (eventList.get(position).getEvent().getHostid().equals(Profile.user.getId())) {
            holder.status.setText("HOST");
            holder.status.setTextColor(Color.parseColor("#FF7F00"));
        }
        else {
            String status = eventList.get(position).getInvites().get(0).getStatus();
            holder.status.setText(status);

            switch (status) {
                case EventData.STATUS_PENDING:
                    holder.status.setTextColor(Color.GRAY);
                    break;
                case EventData.STATUS_YES:
                    holder.status.setTextColor(Color.parseColor("#00CC00"));
                    break;
                case EventData.STATUS_MAYBE:
                    holder.status.setTextColor(Color.parseColor("#CCCC00"));
                    break;
                case EventData.STATUS_CANT:
                    holder.status.setTextColor(Color.RED);
                    break;
            }
        }

        holder.setClickable(eventList.get(position).getEvent().getId(), context);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView status;
        protected CardView card;

        public EventViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.eventTitle);
            status = (TextView) itemView.findViewById(R.id.eventStatus);
            card = (CardView) itemView.findViewById(R.id.eventCard);
        }

        public void setClickable(final String eventID, final Context context) {
            itemView.findViewById(R.id.eventCard).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString(EventDetailsActivity.ARGUMENT_EVENT_ID, eventID);
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
            });
        }
    }
}