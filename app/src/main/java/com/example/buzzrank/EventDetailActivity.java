package com.example.buzzrank;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class EventDetailActivity extends AppCompatActivity {

    private RecyclerView participantsRecyclerView;
    private FirebaseFirestore db;
    private String eventId;
    // private ArrayList<String> participantsList;
    private ArrayList<Participant> participantsList; // Store Participant objects
    private ParticipantsAdapter participantsAdapter;
    private TextView EventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        EventName = findViewById(R.id.eventDetailTitle);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve eventId from the Intent
        eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("eventName");

        // Set the event name in the TextView
        if (eventName != null) {
            EventName.setText(eventName);
        } else {
            EventName.setText("Unknown Event");
        }

        participantsRecyclerView = findViewById(R.id.participantsRecyclerView);
        participantsList = new ArrayList<>();

        // Set up RecyclerView with LinearLayoutManager
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set it to the RecyclerView
        participantsAdapter = new ParticipantsAdapter(participantsList);
        participantsRecyclerView.setAdapter(participantsAdapter);

        fetchParticipants();
    }

    private void fetchParticipants() {
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Buzzers")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        participantsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String participantName = document.getString("name");
                            Long timestamp = document.getLong("timestamp"); // Retrieve timestamp

                            if (participantName != null && timestamp != null) {
                                // Log timestamp to check if it's being fetched correctly
                                Log.d("Firestore", "Fetched: " + participantName + " - Timestamp: " + timestamp);
                                participantsList.add(new Participant(participantName, timestamp));
                            } else {
                                Log.e("Firestore", "Missing timestamp for: " + participantName);
                            }
                        }

                        // Sort by timestamp
                        participantsList.sort(Comparator.comparingLong(Participant::getTimestamp));

                        participantsAdapter.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(EventDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EventDetailActivity.this, "Error fetching participants", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


