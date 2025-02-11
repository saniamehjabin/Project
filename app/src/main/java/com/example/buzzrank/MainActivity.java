package com.example.buzzrank;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    private Button buzzerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);



        firestore = FirebaseFirestore.getInstance();
        buzzerButton = findViewById(R.id.buzzer_button); // Button reference
        TextView textView = findViewById(R.id.textView);
        TextView timeStampTextView = findViewById(R.id.time_stamp);

        // Set time_stamp visibility to GONE initially
        timeStampTextView.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String username = getIntent().getStringExtra("username");
        String eventId = getIntent().getStringExtra("eventId");





        // Ensure buzzer button is enabled when the activity starts
        buzzerButton.setEnabled(true);
        buzzerButton.setText("Press Buzzer");

        // Check if the user has already pressed the buzzer when the activity is created
        if (currentUser != null && eventId != null) {
            checkIfUserPressedBuzzer(eventId, currentUser.getUid(), buzzerButton);
        }

        buzzerButton.setOnClickListener(v -> {

            // Ensure buzzer button is enabled when the activity starts
            buzzerButton.setEnabled(true);
            buzzerButton.setText("Press Buzzer");

            // Check if the user has already pressed the buzzer when the activity is created
            if (currentUser != null && eventId != null) {
                checkIfUserPressedBuzzer(eventId, currentUser.getUid(), buzzerButton);
            }
            // Play buzzer sound
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.buzzer_sound);
            mediaPlayer.start();

            // Vibrate
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(500); // Vibrate for 500 milliseconds
            }

            // Add button press animation
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    buzzerButton,
                    PropertyValuesHolder.ofFloat("scaleX", 0.9f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.9f)
            );
            scaleDown.setDuration(300);
            scaleDown.setRepeatCount(1);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
            scaleDown.start();

            // Release MediaPlayer resources when done
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);

            // Get the current time when the buzzer is pressed
            long timestamp = System.currentTimeMillis();

            // Format the timestamp into a readable format (e.g., hh:mm:ss)
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = sdf.format(new Date(timestamp));
            timeStampTextView.setVisibility(View.VISIBLE);  // Make the TextView visible

            // Update the time_stamp TextView with the formatted timestamp
            timeStampTextView.setText("Time: " + formattedTime);  // Display formatted time

            // Log buzzer press to Firestore
            if (eventId != null) {
                logBuzzerPressToFirebase(username, eventId, buzzerButton);
            }
            textView.setVisibility(View.GONE);

        });



    }



    private void logBuzzerPressToFirebase(String username, String eventId, Button buzzerButton) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user has already pressed the buzzer
        firestore.collection("Buzzers")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // User has already pressed the buzzer
                            Toast.makeText(this, "You have already pressed the buzzer for this event!", Toast.LENGTH_SHORT).show();
                            buzzerButton.setEnabled(false);  // Disable button
                            buzzerButton.setText("Already Pressed");  // Update text
                        } else {
                            // User has not pressed the buzzer, log the press
                            Map<String, Object> buzzerData = new HashMap<>();
                            buzzerData.put("name", username);
                            buzzerData.put("email", user.getEmail());
                            buzzerData.put("userId", user.getUid());
                            buzzerData.put("eventId", eventId);  // Make sure eventId is passed
                            buzzerData.put("timestamp", System.currentTimeMillis());


                            // Add buzzer data to Firestore
                            firestore.collection("Buzzers").add(buzzerData)
                                    .addOnSuccessListener(documentReference -> {
                                        // Successfully logged buzzer press
                                        Log.d("Buzzer", "Buzzer press successfully logged for event: " + eventId);
                                        Toast.makeText(this, "Buzzer pressed!", Toast.LENGTH_SHORT).show();
                                        buzzerButton.setEnabled(false);  // Disable button
                                        buzzerButton.setText("Already Pressed");  // Update text
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Buzzer", "Error logging buzzer press: ", e);
                                        Toast.makeText(this, "Error pressing buzzer!", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    } else {
                        Log.e("Buzzer", "Error checking Firestore: " + task.getException());
                    }
                });
    }





    private void checkIfUserPressedBuzzer(String eventId, String userId, Button buzzerButton) {
        firestore.collection("Buzzers")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // User has already pressed the buzzer
                            Log.d("Buzzer", "User has already pressed the buzzer for event: " + eventId);
                            buzzerButton.setEnabled(false);  // Disable button
                            buzzerButton.setText("Already Pressed");  // Update text

                        } else {
                            // User has not pressed the buzzer yet
                            Log.d("Buzzer", "User has not pressed the buzzer for event: " + eventId);
                        }
                    } else {
                        Log.e("Buzzer", "Error checking Firestore: " + task.getException());
                    }
                });
    }




}

