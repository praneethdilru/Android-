package com.theempires.jetshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhoneNumber;
    private Button buttonSave;

    private Button changeEmail;

    private String userId;
    private static final String TAG = "ShowUserDetailsActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        db = FirebaseFirestore.getInstance();
        editTextName = findViewById(R.id.textViewName);
        editTextPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        buttonSave = findViewById(R.id.buttonSave);
        changeEmail = findViewById(R.id.changeEmail);


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserDetails();

            }
        });

        fetchAndDisplayUserDetails();


        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });
    }
        private void sendEmailVerification() {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                currentUser.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Email verification sent successfully
                                    Toast.makeText(UserActivity.this, "Email verification sent to " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to send email verification
                                    Toast.makeText(UserActivity.this, "Failed to send email verification", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error sending email verification", task.getException());
                                }
                            }
                        });
            }
        }







    private void saveUserDetails() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("phoneNumber", phoneNumber);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();

            DocumentReference userRef = db.collection("users").document(userId);
            userRef.set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User details saved successfully
                            Toast.makeText(UserActivity.this, "User details saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to save user details
                            Toast.makeText(UserActivity.this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error saving user details", task.getException());
                        }
                    });
        }
    }

    private void fetchAndDisplayUserDetails() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();


            DocumentReference userRef = db.collection("users").document(userId);


            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        TextView textViewName = findViewById(R.id.textViewName1);
                        TextView textViewEmail = findViewById(R.id.textViewEmail1);
                        TextView textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber1);

                        String userName = document.getString("name");
                        String userEmail = document.getString("email");
                        String userPhoneNumber = document.getString("phoneNumber");


                        textViewName.setText("Name: " + userName);
                        textViewEmail.setText("Email: " + userEmail);
                        textViewPhoneNumber.setText("Phone Number: " + userPhoneNumber);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });


            findViewById(R.id.buttonHome).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserActivity.this, HomeActivity.class);

                    startActivity(intent);
                    finish();

                }
            });
        }
    }
}
