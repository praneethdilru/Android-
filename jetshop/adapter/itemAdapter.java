package com.theempires.jetshop.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.theempires.jetshop.CategoryFragment;
import com.theempires.jetshop.HomeFragment;
import com.theempires.jetshop.model.Item;
import com.theempires.jetshop.R;
import java.util.ArrayList;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private FirebaseStorage storage;
    private CategoryFragment context;
    private HomeFragment context2;
    private OnItemClickListener onItemClickListener;

    public itemAdapter(ArrayList<Item> items, CategoryFragment context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    public itemAdapter(ArrayList<Item> items, HomeFragment context2) {
        this.items = items;
        this.context2 = context2;
        this.storage = FirebaseStorage.getInstance();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public itemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_card, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull itemAdapter.ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textName.setText(item.getName());
        holder.textDesc.setText(item.getDescription());
        holder.textPrice.setText(String.valueOf(item.getPrice()));

        storage.getReference("item-images/" + item.getImage())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(200, 200)
                                .centerCrop()
                                .into(holder.image);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDesc, textPrice;
        ImageView image;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textDesc = itemView.findViewById(R.id.itemDiscription);
            textPrice = itemView.findViewById(R.id.itemPrice);
            image = itemView.findViewById(R.id.itemImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
