package com.example.buzzrank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder> {

    private ArrayList<Participant> participantsList;

    public ParticipantsAdapter(ArrayList<Participant> participantsList) {
        this.participantsList = participantsList;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the participant item layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_item, parent, false);
        return new ParticipantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        // Bind the participant data to the TextViews
        Participant participant = participantsList.get(position);
        holder.participantNameTextView.setText(participant.getName());

        // Convert timestamp to readable format (optional)
        String formattedTime = android.text.format.DateFormat.format("hh:mm:ss a", participant.getTimestamp()).toString();
        holder.timestampTextView.setText(formattedTime); // Display the timestamp
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }

    // ViewHolder class for participant item layout
    public static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        TextView participantNameTextView, timestampTextView;

        public ParticipantViewHolder(View itemView) {
            super(itemView);
            participantNameTextView = itemView.findViewById(R.id.participantName);
            timestampTextView = itemView.findViewById(R.id.timestamp); // Ensure this exists in participant_item.xml
        }
    }
}
