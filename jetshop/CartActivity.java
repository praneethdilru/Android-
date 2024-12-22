package com.theempires.jetshop;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.theempires.jetshop.adapter.CartAdapter;
import com.theempires.jetshop.model.Item;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private CollectionReference cartCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartCollection = db.collection("carts");

        cartAdapter = new CartAdapter(this, cartCollection);
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();
    }

    private void loadCartItems() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            Query query = cartCollection.whereEqualTo("userId", userId);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Item> cartItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item cartItem = document.toObject(Item.class);
                        cartItems.add(cartItem);
                    }

                    if (!cartItems.isEmpty()) {
                        cartAdapter.setCartItems(cartItems);
                    } else {

                    }
                } else {
                    showToast("Failed to load cart items: " + task.getException().getMessage());
                }
            });
        } else {
            showToast("User not authenticated. Unable to load cart items.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
