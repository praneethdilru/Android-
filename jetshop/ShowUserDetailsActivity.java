package com.theempires.jetshop;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowUserDetailsActivity extends AppCompatActivity {

    private ImageView profilePicImageView;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    private String userId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_nav_header_layout);

        db = FirebaseFirestore.getInstance();

        userNameTextView = findViewById(R.id.userNameTextView2);
        userEmailTextView = findViewById(R.id.userEmailTextView2);

        // Fetch and display user details
        fetchAndDisplayUserDetails();
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


                        String userName = document.getString("name");
                        String userEmail = document.getString("email");


                        userNameTextView.setText(userName);
                        userEmailTextView.setText(userEmail);


                    } else {
                    }
                }
            });
        }
    }
}
