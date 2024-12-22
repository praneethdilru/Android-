package com.theempires.jetshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.theempires.jetshop.adapter.itemAdapter;
import com.theempires.jetshop.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<Item> items;
    private List<Item> filteredItems;
    private itemAdapter itemAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        items = new ArrayList<>();
        filteredItems = new ArrayList<>();

        RecyclerView itemView = view.findViewById(R.id.categoriesView);
        itemAdapter = new itemAdapter((ArrayList<Item>) filteredItems, HomeFragment.this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        itemView.setLayoutManager(layoutManager);
        itemView.setAdapter(itemAdapter);

        fetchItems();

        return view;
    }

    private void fetchItems() {
        firestore.collection("Items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                items.clear();
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    Item item = snapshot.toObject(Item.class);
                    items.add(item);
                }
                filterItems("");
            } else {

            }
        });
    }

    private void filterItems(String query) {
        filteredItems.clear();

        for (Item item : items) {
            if (item.getName() != null && item.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                filteredItems.add(item);
            }
        }

        itemAdapter.notifyDataSetChanged();
    }
}
