package com.example.buzzrank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEvents;
    private ArrayList<Event> eventList;
    private EventsAdapter eventsAdapter;
    private FirebaseFirestore db;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Get the isAdmin flag from Intent
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();

        eventList = new ArrayList<>();

        // Pass the isAdmin flag to the adapter
        eventsAdapter = new EventsAdapter(eventList, this::onEventClick, isAdmin); // Pass isAdmin flag

        recyclerViewEvents.setAdapter(eventsAdapter);

        fetchEvents();

        // Set up the logout functionality when TextView is clicked
        TextView logoutTextView = findViewById(R.id.textView);
        if (isAdmin) {
            // Hide logout and refresh for admins
            logoutTextView.setVisibility(View.GONE);
            TextView refreshTextView = findViewById(R.id.refresh);
            refreshTextView.setVisibility(View.GONE);
        } else
        logoutTextView.setOnClickListener(v -> {
            // Log out the user
            FirebaseAuth.getInstance().signOut();

            // Show a toast message
            Toast.makeText(EventListActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to SignInActivity
            Intent intent = new Intent(EventListActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack so user can't go back to EventListActivity
            startActivity(intent);
            finish(); // Close the current activity
        });

        // Set up the refresh functionality when TextView is clicked
        TextView refreshTextView = findViewById(R.id.refresh);
        refreshTextView.setOnClickListener(v -> {
            // Refresh the activity by fetching the events again
            fetchEvents();
        });

    }



    private void fetchEvents() {
        db.collection("events")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());  // Ensure correct Firestore ID
                            eventList.add(event);
                        }
                        eventsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EventListActivity.this, "Error fetching events", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void onEventClick(Event event) {
        // Log eventId to verify it's being passed correctly
        Log.d("EventListActivity", "Passing eventId: " + event.getId());

        // Retrieve the username and isAdmin flag passed from SignInActivity
        String username = getIntent().getStringExtra("username");
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin) {

            // Admin user - navigate to EventDetailActivity

            Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
            intent.putExtra("eventId", event.getId()); // Pass event ID
            intent.putExtra("eventName", event.getEventName()); // Optionally pass event details

            startActivity(intent);
        } else {
            // Regular user (participant) - navigate to MainActivity
            Intent intent = new Intent(EventListActivity.this, MainActivity.class);
            intent.putExtra("username", username); // Pass username
            intent.putExtra("eventId", event.getId()); // Pass event ID to MainActivity
            intent.putExtra("eventName", event.getEventName()); // Optionally pass event details

            Log.d("EventListActivity", "Sent eventId: " + event.getId()); // Debug log



            startActivity(intent);
        }
    }

}