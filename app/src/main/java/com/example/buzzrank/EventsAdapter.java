package com.example.buzzrank;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private ArrayList<Event> eventList;
    private EventClickListener eventClickListener;
    private boolean isAdmin; // Admin check flag

    // Constructor now accepts isAdmin flag
    public EventsAdapter(ArrayList<Event> eventList, EventClickListener eventClickListener, boolean isAdmin) {
        this.eventList = eventList;
        this.eventClickListener = eventClickListener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getEventName());

        // Show delete button only if the user is an admin
        if (isAdmin) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                deleteEvent(event.getId(), holder.itemView.getContext());
                eventList.remove(holder.getAdapterPosition());  // Safer way to remove
                notifyDataSetChanged();  // Refresh entire list
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Set up click listener for opening event details
        holder.itemView.setOnClickListener(v -> eventClickListener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Method to delete event from Firestore
    private void deleteEvent(String eventId, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error deleting event", Toast.LENGTH_SHORT).show());
    }

    // ViewHolder class for event item layout
    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        Button btnDelete;

        public EventViewHolder(View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Interface for event click listener
    public interface EventClickListener {
        void onEventClick(Event event);
    }
}
