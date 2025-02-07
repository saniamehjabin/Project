package com.example.buzzrank;



import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private TextView textViewEventName, textViewParticipants;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        textViewEventName = findViewById(R.id.textViewEventName);
        textViewParticipants = findViewById(R.id.textViewParticipants);
        firestore = FirebaseFirestore.getInstance();

        String eventId = getIntent().getStringExtra("eventId");
        String eventName = getIntent().getStringExtra("eventName");

        if (eventId != null) {
            // Set the event name in the TextView
            textViewEventName.setText(eventName);

            // Fetch participants for this event
            fetchParticipants(eventId);
        }
    }

    private void fetchParticipants(String eventId) {
        firestore.collection("Buzzers")
                .whereEqualTo("Id", eventId)
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot result = task.getResult();
                        StringBuilder participants = new StringBuilder();


                        for (DocumentSnapshot document : result) {
                            String name = document.getString("name");
                            Long timestamp = document.getLong("timestamp");

                            if (name != null && timestamp != null) {
                                Log.d("EventDetail", "Raw timestamp: " + timestamp);  // Log the raw timestamp
                                String formattedTime = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault())
                                        .format(new Date(timestamp));
                                participants.append(name).append(" - ").append(formattedTime).append("\n");
                                Log.d("EventDetail", "Formatted Time: " + formattedTime);  // Log the formatted time
                            }
                        }



                        textViewParticipants.setText(participants.toString().isEmpty() ?
                                "No participants yet." : participants.toString());
                    } else {
                        Toast.makeText(this, "Error fetching participants", Toast.LENGTH_SHORT).show();
                        Log.e("EventDetail", "Error: ", task.getException());
                    }
                });
    }
}
