package com.example.buzzrank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private Button btnEvents, btnNewEvents;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnEvents = findViewById(R.id.btnEvents);
        btnNewEvents = findViewById(R.id.btnNewEvents);
        textView = findViewById(R.id.textView);

        // Navigate to EventListActivity when "Events" button is clicked
        btnEvents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, EventListActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });

        // Navigate to create a new event when "New Events" button is clicked
        btnNewEvents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        textView.setOnClickListener(v -> {
            // Log out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Clear admin login state from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("isAdminLoggedIn", false).apply();

            // Navigate to the login screen
            Intent intent = new Intent(AdminActivity.this, SignInActivity.class);
            startActivity(intent);
            finish(); // Close AdminActivity to prevent back navigation
        });

    }




}
