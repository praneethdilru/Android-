package com.theempires.jetshop.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theempires.jetshop.CartActivity;
import com.theempires.jetshop.R;
import com.theempires.jetshop.model.Item;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private FirebaseStorage storage;
    private List<Item> cartItems;
    private CartActivity context;
    private CollectionReference itemsCollection;

    public CartAdapter(CartActivity context, CollectionReference itemsCollection) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.itemsCollection = itemsCollection;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Item cartItem = cartItems.get(position);
        holder.itemNameTextView.setText(cartItem.getName());
        holder.itemPriceTextView.setText(String.valueOf(cartItem.getPrice()));
        storage.getReference("item-images/" + cartItem.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(100, 100)
                                .centerCrop()
                                .into(holder.itemImageView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void setCartItems(List<Item> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView itemPriceTextView;
        ImageView itemImageView;
        ImageView deleteProduct;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameTextView = itemView.findViewById(R.id.cartItemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.cartItemPriceTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            deleteProduct = itemView.findViewById(R.id.removeFromCartImageView);

            deleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteProduct(position);
                    }
                }

            });
            StorageReference storageRef = storage.getReference();
            storageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // ...
                    })
                    .addOnFailureListener(e -> {
                        // ...
                    });
        }


        private void deleteProduct(int position) {
            Item item = cartItems.get(position);
            if (item != null) {
                String itemId = item.getId();
                String itemImage = item.getImage();

                StorageReference storageRef = storage.getReference("item-images/" + itemImage);

                storageRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            itemsCollection.document(itemId)
                                    .delete()
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Remove the item from the local list
                                        cartItems.remove(position);
                                        notifyItemRemoved(position);
                                        showToast("Product deleted successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        showToast("Error deleting product from Firestore: " + e.getMessage());
                                    });
                        })
                        .addOnFailureListener(e -> {
                            showToast("Error deleting product from Storage: " + e.getMessage());
                        });
            }
        }


        private void showToast(String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
