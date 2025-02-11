package com.example.buzzrank;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEditText, passEditText;
    private String email, pass;
    private Button submit;
    private TextView register;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Initialize SharedPreferences to check for admin login state
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);


        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        submit = findViewById(R.id.submit);
        register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check if the admin is already logged in from SharedPreferences
        if (sharedPreferences.getBoolean("isAdminLoggedIn", false)) {
            // If the admin is logged in, directly navigate to AdminActivity
            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return; // Prevent further execution in onCreate
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) {
                Toast.makeText(getApplicationContext(), "Please verify your email before signing in.", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();  // Sign out unverified users
            } else {
                // User is verified, allow automatic login
                if (currentUser.getEmail().equals("mony_cse@lus.ac.bd")) {
                    // Admin login
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    intent.putExtra("isAdmin", true);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    // Regular participant login
                    firestore.collection("Users").document(currentUser.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String username = documentSnapshot.getString("name");

                                    Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                                    intent.putExtra("isAdmin", false);
                                    intent.putExtra("username", username);
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error fetching user data.", Toast.LENGTH_SHORT).show());
                }
            }
        }



        submit.setOnClickListener(v -> {
            email = emailEditText.getText().toString();
            pass = passEditText.getText().toString();

            if (email.isEmpty()) {
                emailEditText.setError("Empty!!");
                emailEditText.requestFocus();
            } else if (pass.isEmpty()) {
                passEditText.setError("Empty!!");
                passEditText.requestFocus();
            }
            // Check if it's admin login
            else if (email.equals("mony_cse@lus.ac.bd") && pass.equals("123456")) {
                // Admin login
                Toast.makeText(getApplicationContext(), "Admin login successful!", Toast.LENGTH_SHORT).show();
                // Store admin login state in SharedPreferences
                sharedPreferences.edit().putBoolean("isAdminLoggedIn", true).apply();
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("isAdmin", true);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }

            else {
                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser user = auth.getCurrentUser();
                    if (task.isSuccessful()) {
                        if (user != null && user.isEmailVerified()) {
                            // Fetch user info from Firestore
                            firestore.collection("Users").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String username = documentSnapshot.getString("name");

                                            Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_SHORT).show();

                                            // Pass the username to MainActivity
                                            Intent intent = new Intent(getApplicationContext(), EventListActivity.class);
                                            intent.putExtra("isAdmin", false);
                                            intent.putExtra("username", username);
                                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error fetching user data.", Toast.LENGTH_SHORT).show());
                        } else {
                            // Email not verified
                            Toast.makeText(getApplicationContext(), "Please verify your email.", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Email or Password!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        register.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });

    }


}