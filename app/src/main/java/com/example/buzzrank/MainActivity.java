package com.example.buzzrank;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private FirebaseFirestore firestore;


    // Retrieve the username from the Intent
  /*  String username = getIntent().getStringExtra("username");
    String eventId = getIntent().getStringExtra("eventId");
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        Button buzzerButton = findViewById(R.id.buzzer_button);
        TextView textView = findViewById(R.id.textView);

        // Retrieve eventId from Intent
        String eventId = getIntent().getStringExtra("eventId");
        String username = getIntent().getStringExtra("username");

        if (eventId != null) {
            Toast.makeText(this, "Joined Event ID: " + eventId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Event ID received", Toast.LENGTH_SHORT).show();
        }
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        buzzerButton.setOnClickListener(v -> {

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
            logBuzzerPressToFirebase(username, eventId, currentUser);
        });
       // logBuzzerPressToFirebase(username, eventId, currentUser);


    }




    private void logBuzzerPressToFirebase(String username, String eventId, FirebaseUser user) {


        if (user == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the buzzer press to Firebase Firestore
        Map<String, Object> buzzerData = new HashMap<>();
        buzzerData.put("name", username);
        buzzerData.put("email", user.getEmail());
        buzzerData.put("userId", user.getUid());
        buzzerData.put("eventId", eventId);
        buzzerData.put("timestamp", System.currentTimeMillis());

        firestore.collection("Buzzers").add(buzzerData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Buzzer", "Buzzer pressed and logged successfully!");

                })
                .addOnFailureListener(e -> {
                    Log.e("Buzzer", "Error logging buzzer press: ", e);
                });
    }




}
