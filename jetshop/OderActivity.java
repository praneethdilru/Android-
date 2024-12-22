package com.theempires.jetshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.theempires.jetshop.manage.CartManager;
import com.theempires.jetshop.model.Item;

import java.util.HashMap;
import java.util.Map;

public class OderActivity extends AppCompatActivity {
    private FirebaseStorage storage;
    private Item item;
    private NotificationManager notificationManager;
    private String userId;

    private String channelId = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"INFO",NotificationManager.IMPORTANCE_DEFAULT);

            channel.setShowBadge(true);
            channel.setDescription("This is Information Channel");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long[]{0,1000,1000,1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);

        }
        storage = FirebaseStorage.getInstance();
        item = getIntent().getParcelableExtra("Items");

        if (item != null) {
            String itemName = item.getName();
            String itemDescription = item.getDescription();
            double itemPrice = item.getPrice();
            String itemImageURL = item.getImage();

            ImageView itemImageView = findViewById(R.id.itemImageView);
            TextView itemNameTextView = findViewById(R.id.itemNameTextView);
            TextView itemDescriptionTextView = findViewById(R.id.itemDescriptionTextView);
            TextView itemPriceTextView = findViewById(R.id.itemPriceTextView);
            Button buyButton = findViewById(R.id.buyButton);
            Button addToCartButton = findViewById(R.id.addToCartButton);



            if (itemImageURL != null && !itemImageURL.isEmpty()) {
                storage.getReference("item-images/" + item.getImage())
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Picasso.get()
                                    .load(uri)
                                    .placeholder(R.drawable.loading)
                                    .resize(200, 200)
                                    .centerCrop()
                                    .into(itemImageView);
                        });
            } else {
                itemImageView.setImageResource(R.drawable.loading);
            }




            itemNameTextView.setText(itemName);
            itemDescriptionTextView.setText(itemDescription);
            itemPriceTextView.setText(String.valueOf("LKR:" + itemPrice));

            buyButton.setOnClickListener(view -> {

                showToast("Buy Now clicked");
            });

            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCart();
                    openCartActivity();

                    Intent intent = new Intent(OderActivity.this,CartActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            OderActivity.this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                    );


                    Notification notification = new NotificationCompat.Builder(getApplicationContext(),channelId)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.baseline_notifications_24)
                            .setContentTitle("Jet Shop")
                            .setContentText("You Product add to cart")
                            .setColor(Color.RED)
                            .setContentIntent(pendingIntent)
                            .build();

                    notificationManager.notify(1,notification);
                }



            });
        }
    }



    private void addToCart() {
        String userId = getCurrentUserId();

        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference cartRef = db.collection("carts");


            Map<String, Object> cartItemData = new HashMap<>();
            cartItemData.put("userId", userId);
            cartItemData.put("name", item.getName());
            cartItemData.put("price", item.getPrice());

            cartRef.add(cartItemData)
                    .addOnSuccessListener(documentReference -> {
                        showToast("Item added to cart");
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to add item to cart: " + e.getMessage());
                    });
        } else {
            showToast("User not authenticated. Unable to add item to cart.");
        }
    }



    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            return user.getUid();
        } else {

            return null;
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openCartActivity() {
        // Navigate to CartActivity
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
}
