package com.example.buzzrank;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText editTextEventName;
    private Button btnCreateEvent;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();
        editTextEventName = findViewById(R.id.editTextEventName);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        btnCreateEvent.setOnClickListener(v -> {
            String eventName = editTextEventName.getText().toString().trim();
            if (!eventName.isEmpty()) {
                String eventId = UUID.randomUUID().toString();

                // Add event details with timestamp
                Map<String, Object> event = new HashMap<>();
                event.put("eventId", eventId); // Unique ID
                event.put("eventName", eventName); // Event name
                event.put("timestamp", System.currentTimeMillis()); // Current time as timestamp

                db.collection("events").document(eventId).set(event)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreateEventActivity.this, "Event created", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(CreateEventActivity.this, "Error creating event", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(CreateEventActivity.this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
